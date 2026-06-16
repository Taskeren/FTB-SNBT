package cn.elytra.ftbsnbt;

import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

/// The adaptor to create NBT instances.
public interface NBTAdaptor<Tag> {

    Tag ofNull();

    Tag ofString(String s);

    Tag ofBoolean(boolean b);

    Tag ofDouble(double d);

    default Tag ofNaN() {
        return ofDouble(Double.NaN);
    }

    default Tag ofPositiveInfinite() {
        return ofDouble(Double.POSITIVE_INFINITY);
    }

    default Tag ofNegativeInfinite() {
        return ofDouble(Double.NEGATIVE_INFINITY);
    }

    Tag ofFloat(float f);

    default Tag ofNaNFloat() {
        return ofFloat(Float.NaN);
    }

    default Tag ofPositiveInfiniteFloat() {
        return ofFloat(Float.POSITIVE_INFINITY);
    }

    default Tag ofNegativeInfiniteFloat() {
        return ofFloat(Float.NEGATIVE_INFINITY);
    }

    Tag ofByte(byte b);

    Tag ofShort(short s);

    Tag ofInt(int i);

    Tag ofLong(long l);

    Tag ofCompound(Map<String, ? extends Tag> c);

    Tag ofList(List<? extends Tag> l);

    Tag ofArray(int[] a);

    Tag ofArray(long[] a);

    Tag ofArray(byte[] a);

    /// Get the numeric value of the tag, or `null` if not capable.
    @Nullable Number getNumberOf(Tag t);
}
