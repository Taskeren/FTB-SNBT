/*
 * All Rights Reserved
 *
 * Copyright (c) 2025 Feed The Beast Ltd
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cn.elytra.ftbsnbt;

import cn.elytra.ftbsnbt.exception.SNBTEOFException;
import cn.elytra.ftbsnbt.exception.SNBTSyntaxException;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import org.glavo.nbt.tag.*;

import java.util.ArrayList;
import java.util.List;

import static cn.elytra.ftbsnbt.SpecialTags.*;

final class SNBTParser {

    public static final char[] ESCAPE_CHARS = Util.make(new char[128], array -> {
        array['"'] = '\"';
        array['\\'] = '\\';
        array['\t'] = 't';
        array['\b'] = 'b';
        array['\n'] = 'n';
        array['\r'] = 'r';
        array['\f'] = 'f';
    });
    public static final char[] REVERSE_ESCAPE_CHARS = Util.make(new char[128], array -> {
        for (var i = 0; i < array.length; i++) {
            if (ESCAPE_CHARS[i] != 0) {
                array[ESCAPE_CHARS[i]] = (char) i;
            }
        }
    });

    private final char[] buffer;
    private int position;

    public SNBTParser(List<String> lines) {
        StringBuilder bufferBuilder = new StringBuilder();

        for (String line : lines) {
            String trimmed = line.trim();
            // ignore comments
            if (!trimmed.startsWith("//") && !trimmed.startsWith("#")) {
                bufferBuilder.append(line);
            }
            bufferBuilder.append("\n");
        }

        this.buffer = bufferBuilder.toString().toCharArray();

        if (this.buffer.length < 2) {
            throw new IllegalStateException("File has to have at least 2 characters!");
        }

        this.position = 0;
    }

    static CompoundTag read(List<String> lines) {
        SNBTParser p = new SNBTParser(lines);
        return (CompoundTag) p.readTag(p.nextNS());
    }

    private String posString() {
        return posString(position);
    }

    private String posString(int p) {
        if (p >= buffer.length) {
            return "EOF";
        }

        int row = 0;
        int col = 0;

        for (int i = 0; i < p; i++) {
            if (buffer[i] == '\n') {
                row++;
                col = 0;
            } else {
                col++;
            }
        }

        return (row + 1) + ":" + (col + 1);
    }

    private char next() {
        if (position >= buffer.length) {
            throw new SNBTEOFException();
        }

        char c = buffer[position];
        position++;
        return c;
    }

    private char nextNS() {
        while (true) {
            char c = next();
            if (c > ' ') {
                return c;
            }
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    private Tag readTag(char first) {
        return switch (first) {
            case '{' -> readCompound();
            case '[' -> readCollection();
            case '"', '\'' -> new StringTag(readQuotedString(first));
            default -> {
                String s = readWordString(first);
                yield switch (s) {
                    case "true" -> TRUE.clone();
                    case "false" -> FALSE.clone();
                    case "null", "end", "END" -> NULL_TAG.clone();
                    case "Infinity", "Infinityd", "+Infinity", "+Infinityd", "∞", "∞d", "+∞", "+∞d" -> POS_INFINITE_TAG.clone();
                    case "-Infinity", "-Infinityd", "-∞", "-∞d" -> NEG_INFINITE_TAG.clone();
                    case "NaN", "NaNd" -> NAN_TAG.clone();
                    case "Infinityf", "+Infinityf", "∞f", "+∞f" -> POS_INFINITE_FLOAT_TAG.clone();
                    case "-Infinityf", "-∞f" -> NEG_INFINITE_FLOAT_TAG.clone();
                    case "NaNf" -> NAN_FLOAT_TAG.clone();
                    default -> {
                        char last = Character.toLowerCase(s.charAt(s.length() - 1));
                        TagType<? extends Tag> type = getNumberType(s, last);
                        if (type == TagType.BYTE) {
                            yield new ByteTag(Byte.parseByte(s.substring(0, s.length() - 1)));
                        } else if (type == TagType.SHORT) {
                            yield new ShortTag(Short.parseShort(s.substring(0, s.length() - 1)));
                        } else if (type == TagType.INT) {
                            yield new IntTag(Integer.parseInt(s));
                        } else if (type == TagType.LONG) {
                            yield new LongTag(Long.parseLong(s.substring(0, s.length() - 1)));
                        } else if (type == TagType.FLOAT) {
                            yield new FloatTag(Float.parseFloat(s.substring(0, s.length() - 1)));
                        } else if (type == TagType.DOUBLE) {
                            if (last == 'd') {
                                yield new DoubleTag(Double.parseDouble(s.substring(0, s.length() - 1)));
                            } else {
                                yield new DoubleTag(Double.parseDouble(s));
                            }
                        } else if (type == TagType.STRING) {
                            yield new StringTag(s);
                        } else {
                            // probably unreachable
                            throw new SNBTSyntaxException("Unexpected tag type: " + type.name() + " @ " + posString());
                        }
                    }
                };
            }
        };
    }

    private CompoundTag readCompound() {
        CompoundTag tag = new CompoundTag();

        while (true) {
            char c = nextNS();

            if (c == '}') {
                return tag;
            } else if (c == ',' || c == '\n') {
                continue;
            }

            String key;
            if (c == '"' || c == '\'') {
                key = readQuotedString(c);
            } else {
                key = readWordString(c);
            }

            char n = nextNS();
            if (n == ':' || n == '=') {
                Tag t = readTag(nextNS());
                tag.addTag(key, t);
            } else {
                throw new SNBTSyntaxException("Expected ':', got '" + n + "' @ " + posString());
            }
        }
    }

    private ParentTag<?> readCollection() {
        int prevPos = position;
        char type = nextNS();
        char semi = nextNS();

        if (semi == ';' && (type == 'I' || type == 'i' || type == 'L' || type == 'l' || type == 'B' || type == 'b')) {
            return readArray(prevPos, type);
        } else {
            position = prevPos;
            return readList();
        }
    }

    private ListTag<?> readList() {
        ListTag<Tag> tag = new ListTag<>();

        while (true) {
            var prevPos = position;
            char c = nextNS();

            if (c == ']') {
                return tag;
            } else if (c == ',') {
                continue;
            }

            Tag t = readTag(c);
            try {
                tag.addTag(t);
            } catch (IllegalArgumentException e) {
                throw new SNBTSyntaxException("Unexpected tag '" + t + "' in list @ " + posString(prevPos) + " - can't mix two different tag types in a list!");
            }
        }
    }

    private ArrayTag<?, ?, ?, ?> readArray(int pos, char type) {
        List<Number> list = new ArrayList<>();
        type = Character.toLowerCase(type);

        while (true) {
            char c = nextNS();
            if (c == ']') {
                return switch (type) {
                    case 'i' -> new IntArrayTag(list.stream().mapToInt(Number::intValue).toArray());
                    case 'l' -> new LongArrayTag(list.stream().mapToLong(Number::longValue).toArray());
                    case 'b' -> new ByteArrayTag(listToByteArray(list));
                    default -> throw new SNBTSyntaxException("Unknown array type: " + type + " @ " + posString(pos));
                };
            } else if (c == ',') {
                continue;
            }

            Tag tag = readTag(c);
            if (tag instanceof IntTag intTag) {
                list.add(intTag.getValue());
            } else if (tag instanceof LongTag longTag) {
                list.add(longTag.getValue());
            } else if (tag instanceof ByteTag byteTag) {
                list.add(byteTag.getValue());
            } else {
                throw new SNBTSyntaxException("Unexpected tag '" + tag + "' in list @ " + posString() + " - expected a numeric tag!");
            }
        }
    }

    private String readWordString(char first) {
        StringBuilder sb = new StringBuilder();
        sb.append(first);

        while (true) {
            char c = next();

            if (isSimpleCharacter(c)) {
                sb.append(c);
            } else {
                position--;
                return sb.toString();
            }
        }
    }


    private String readQuotedString(char stop) {
        StringBuilder sb = new StringBuilder();
        boolean escape = false;

        while (true) {
            char c = next();

            if (c == '\n') {
                throw new SNBTSyntaxException("New line without closing string with " + stop + " @ " + posString(position - 1) + "!");
            } else if (escape) {
                escape = false;

                if (REVERSE_ESCAPE_CHARS[c] != 0) {
                    sb.append(REVERSE_ESCAPE_CHARS[c]);
                }
            } else if (c == '\\') {
                escape = true;
            } else if (c == stop) {
                return sb.toString();
            } else {
                sb.append(c);
            }
        }
    }

    private static boolean isSimpleCharacter(char c) {
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '.' || c == '_' || c == '-' || c == '+' || c == '∞';
    }

    private static TagType<? extends Tag> getNumberType(String s, char last) {
        if (s.isEmpty()) {
            return TagType.STRING;
        }

        if (Character.isDigit(last) && Ints.tryParse(s) != null) {
            return TagType.INT;
        }

        String start = s.substring(0, s.length() - 1);
        if (last == 'b' && Ints.tryParse(start) != null) {
            return TagType.BYTE;
        } else if (last == 's' && Ints.tryParse(start) != null) {
            return TagType.SHORT;
        } else if (last == 'l' && Longs.tryParse(start) != null) {
            return TagType.LONG;
        } else if (last == 'f' && Floats.tryParse(start) != null) {
            return TagType.FLOAT;
        } else if (last == 'd' && Doubles.tryParse(start) != null) {
            return TagType.DOUBLE;
        } else if (Floats.tryParse(s) != null) {
            return TagType.DOUBLE;
        } else {
            return TagType.STRING;
        }
    }

    private static byte[] listToByteArray(List<? extends Number> list) {
        byte[] b = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            b[i] = list.get(i).byteValue();
        }
        return b;
    }
}
