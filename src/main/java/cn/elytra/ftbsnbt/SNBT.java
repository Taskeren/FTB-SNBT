/*
 * All Rights Reserved
 *
 * Copyright (c) 2025 Feed The Beast Ltd
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cn.elytra.ftbsnbt;

import org.glavo.nbt.tag.ByteTag;
import org.glavo.nbt.tag.CompoundTag;
import org.glavo.nbt.tag.DoubleTag;
import org.glavo.nbt.tag.FloatTag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class SNBT {

    public static final ByteTag NULL_TAG = new ByteTag((byte) 0);
    public static final DoubleTag POS_INFINITE_TAG = new DoubleTag(Double.POSITIVE_INFINITY);
    public static final DoubleTag NEG_INFINITE_TAG = new DoubleTag(Double.NEGATIVE_INFINITY);
    public static final DoubleTag NAN_TAG = new DoubleTag(Double.NaN);
    public static final FloatTag POS_INFINITE_FLOAT_TAG = new FloatTag(Float.POSITIVE_INFINITY);
    public static final FloatTag NEG_INFINITE_FLOAT_TAG = new FloatTag(Float.NEGATIVE_INFINITY);
    public static final FloatTag NAN_FLOAT_TAG = new FloatTag(Float.NaN);

    private SNBT() {
        throw new UnsupportedOperationException();
    }

    public static CompoundTag readLines(List<String> lines) {
        return SNBTParser.read(lines);
    }

    public static CompoundTag tryRead(Path path) throws IOException {
        return readLines(Files.readAllLines(path, StandardCharsets.UTF_8));
    }
}
