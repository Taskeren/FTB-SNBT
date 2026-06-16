package cn.elytra.ftbsnbt.kyori;

import cn.elytra.ftbsnbt.NBTAdaptor;
import net.kyori.adventure.nbt.*;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class KyoriNBTAdaptor implements NBTAdaptor<BinaryTag> {

    @Override
    public BinaryTag ofNull() {
        return EndBinaryTag.endBinaryTag();
    }

    @Override
    public BinaryTag ofString(String s) {
        return StringBinaryTag.stringBinaryTag(s);
    }

    @Override
    public BinaryTag ofBoolean(boolean b) {
        return b ? ByteBinaryTag.ONE : ByteBinaryTag.ZERO;
    }

    @Override
    public BinaryTag ofDouble(double d) {
        return DoubleBinaryTag.doubleBinaryTag(d);
    }

    @Override
    public BinaryTag ofFloat(float f) {
        return FloatBinaryTag.floatBinaryTag(f);
    }

    @Override
    public BinaryTag ofByte(byte b) {
        return ByteBinaryTag.byteBinaryTag(b);
    }

    @Override
    public BinaryTag ofShort(short s) {
        return ShortBinaryTag.shortBinaryTag(s);
    }

    @Override
    public BinaryTag ofInt(int i) {
        return IntBinaryTag.intBinaryTag(i);
    }

    @Override
    public BinaryTag ofLong(long l) {
        return LongBinaryTag.longBinaryTag(l);
    }

    @Override
    public BinaryTag ofCompound(Map<String, ? extends BinaryTag> c) {
        return CompoundBinaryTag.from(c);
    }

    @Override
    public BinaryTag ofList(List<? extends BinaryTag> l) {
        return ListBinaryTag.from(l);
    }

    @Override
    public BinaryTag ofArray(int[] a) {
        return IntArrayBinaryTag.intArrayBinaryTag(a);
    }

    @Override
    public BinaryTag ofArray(long[] a) {
        return LongArrayBinaryTag.longArrayBinaryTag(a);
    }

    @Override
    public BinaryTag ofArray(byte[] a) {
        return ByteArrayBinaryTag.byteArrayBinaryTag(a);
    }

    @Override
    public @Nullable Number getNumberOf(BinaryTag t) {
        if (t instanceof NumberBinaryTag num) {
            return num.numberValue();
        }
        return null;
    }
}
