/*
 * All Rights Reserved
 *
 * Copyright (c) 2025 Feed The Beast Ltd
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cn.elytra.ftbsnbt.internal;

import cn.elytra.ftbsnbt.NBTAdaptor;
import cn.elytra.ftbsnbt.exception.SNBTEOFException;
import cn.elytra.ftbsnbt.exception.SNBTSyntaxException;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import java.util.*;
import java.util.function.Consumer;

public final class SNBTParser<Tag> {

    public static final char[] ESCAPE_CHARS = make(new char[128], array -> {
        array['"'] = '\"';
        array['\\'] = '\\';
        array['\t'] = 't';
        array['\b'] = 'b';
        array['\n'] = 'n';
        array['\r'] = 'r';
        array['\f'] = 'f';
    });
    public static final char[] REVERSE_ESCAPE_CHARS = make(new char[128], array -> {
        for (var i = 0; i < array.length; i++) {
            if (ESCAPE_CHARS[i] != 0) {
                array[ESCAPE_CHARS[i]] = (char) i;
            }
        }
    });

    private final char[] buffer;
    private int position;

    private final NBTAdaptor<Tag> adaptor;

    public SNBTParser(List<String> lines, NBTAdaptor<Tag> adaptor) {
        this.adaptor = adaptor;

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

    public static <T> T read(List<String> lines, NBTAdaptor<T> adaptor) {
        SNBTParser<T> p = new SNBTParser<>(lines, adaptor);
        return p.readTag(p.nextNS());
    }

    private static <T> T make(T value, Consumer<T> builder) {
        builder.accept(value);
        return value;
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
            case '"', '\'' -> adaptor.ofString(readQuotedString(first));
            default -> {
                String s = readWordString(first);
                yield switch (s) {
                    case "true" -> adaptor.ofBoolean(true);
                    case "false" -> adaptor.ofBoolean(false);
                    case "null", "end", "END" -> adaptor.ofNull();
                    case "Infinity", "Infinityd", "+Infinity", "+Infinityd", "∞", "∞d", "+∞", "+∞d" ->
                            adaptor.ofPositiveInfinite();
                    case "-Infinity", "-Infinityd", "-∞", "-∞d" -> adaptor.ofNegativeInfinite();
                    case "NaN", "NaNd" -> adaptor.ofNaN();
                    case "Infinityf", "+Infinityf", "∞f", "+∞f" -> adaptor.ofPositiveInfiniteFloat();
                    case "-Infinityf", "-∞f" -> adaptor.ofNegativeInfiniteFloat();
                    case "NaNf" -> adaptor.ofNaNFloat();
                    default -> {
                        char last = Character.toLowerCase(s.charAt(s.length() - 1));
                        TagType type = getNumberType(s, last);
                        if (type == TagType.BYTE) {
                            yield adaptor.ofByte(Byte.parseByte(s.substring(0, s.length() - 1)));
                        } else if (type == TagType.SHORT) {
                            yield adaptor.ofShort(Short.parseShort(s.substring(0, s.length() - 1)));
                        } else if (type == TagType.INT) {
                            yield adaptor.ofInt(Integer.parseInt(s));
                        } else if (type == TagType.LONG) {
                            yield adaptor.ofLong(Long.parseLong(s.substring(0, s.length() - 1)));
                        } else if (type == TagType.FLOAT) {
                            yield adaptor.ofFloat(Float.parseFloat(s.substring(0, s.length() - 1)));
                        } else if (type == TagType.DOUBLE) {
                            if (last == 'd') {
                                yield adaptor.ofDouble(Double.parseDouble(s.substring(0, s.length() - 1)));
                            } else {
                                yield adaptor.ofDouble(Double.parseDouble(s));
                            }
                        } else if (type == TagType.STRING) {
                            yield adaptor.ofString(s);
                        } else {
                            // probably unreachable
                            throw new SNBTSyntaxException("Unexpected tag type: " + type.name() + " @ " + posString());
                        }
                    }
                };
            }
        };
    }

    private Tag readCompound() {
        Map<String, Tag> tag = new LinkedHashMap<>();

        while (true) {
            char c = nextNS();

            if (c == '}') {
                return adaptor.ofCompound(tag);
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
                tag.put(key, t);
            } else {
                throw new SNBTSyntaxException("Expected ':', got '" + n + "' @ " + posString());
            }
        }
    }

    private Tag readCollection() {
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

    private Tag readList() {
        List<Tag> tag = new LinkedList<>();

        while (true) {
            var prevPos = position;
            char c = nextNS();

            if (c == ']') {
                return adaptor.ofList(tag);
            } else if (c == ',') {
                continue;
            }

            Tag t = readTag(c);
            try {
                tag.add(t);
            } catch (IllegalArgumentException e) {
                throw new SNBTSyntaxException("Unexpected tag '" + t + "' in list @ " + posString(prevPos) + " - can't mix two different tag types in a list!");
            }
        }
    }

    private Tag readArray(int pos, char type) {
        List<Number> list = new ArrayList<>();
        type = Character.toLowerCase(type);

        while (true) {
            char c = nextNS();
            if (c == ']') {
                return switch (type) {
                    case 'i' -> adaptor.ofArray(list.stream().mapToInt(Number::intValue).toArray());
                    case 'l' -> adaptor.ofArray(list.stream().mapToLong(Number::longValue).toArray());
                    case 'b' -> adaptor.ofArray(listToByteArray(list));
                    default -> throw new SNBTSyntaxException("Unknown array type: " + type + " @ " + posString(pos));
                };
            } else if (c == ',') {
                continue;
            }

            Tag tag = readTag(c);
            Number n = adaptor.getNumberOf(tag);
            if (n instanceof Integer || n instanceof Long || n instanceof Byte) {
                list.add(n);
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

    private enum TagType {
        STRING,
        INT,
        BYTE,
        SHORT,
        LONG,
        FLOAT,
        DOUBLE,
    }

    private static TagType getNumberType(String s, char last) {
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
