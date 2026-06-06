/*
 * All Rights Reserved
 *
 * Copyright (c) 2025 Feed The Beast Ltd
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package cn.elytra.ftbsnbt;

import org.glavo.nbt.tag.ByteTag;
import org.glavo.nbt.tag.DoubleTag;
import org.glavo.nbt.tag.FloatTag;

final class SpecialTags {
    static final ByteTag TRUE = new ByteTag((byte) 1);
    static final ByteTag FALSE = new ByteTag((byte) 0);
    static final ByteTag NULL_TAG = new ByteTag((byte) 0);
    static final DoubleTag POS_INFINITE_TAG = new DoubleTag(Double.POSITIVE_INFINITY);
    static final DoubleTag NEG_INFINITE_TAG = new DoubleTag(Double.NEGATIVE_INFINITY);
    static final DoubleTag NAN_TAG = new DoubleTag(Double.NaN);
    static final FloatTag POS_INFINITE_FLOAT_TAG = new FloatTag(Float.POSITIVE_INFINITY);
    static final FloatTag NEG_INFINITE_FLOAT_TAG = new FloatTag(Float.NEGATIVE_INFINITY);
    static final FloatTag NAN_FLOAT_TAG = new FloatTag(Float.NaN);
}
