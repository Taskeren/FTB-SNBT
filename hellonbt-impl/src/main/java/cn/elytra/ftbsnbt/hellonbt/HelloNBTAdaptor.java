package cn.elytra.ftbsnbt.hellonbt;

import cn.elytra.ftbsnbt.NBTAdaptor;
import org.glavo.nbt.tag.*;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class HelloNBTAdaptor implements NBTAdaptor<Tag> {

    public static final Tag NULL_MARKER = new IntTag(0);

    @Override
    public Tag ofNull() {
        return NULL_MARKER;
    }

    @Override
    public Tag ofString(String s) {
        return new StringTag(s);
    }

    @Override
    public Tag ofBoolean(boolean b) {
        return new ByteTag((byte) (b ? 1 : 0));
    }

    @Override
    public Tag ofDouble(double d) {
        return new DoubleTag(d);
    }

    @Override
    public Tag ofFloat(float f) {
        return new FloatTag(f);
    }

    @Override
    public Tag ofByte(byte b) {
        return new ByteTag(b);
    }

    @Override
    public Tag ofShort(short s) {
        return new ShortTag(s);
    }

    @Override
    public Tag ofInt(int i) {
        return new IntTag(i);
    }

    @Override
    public Tag ofLong(long l) {
        return new LongTag(l);
    }

    @Override
    public Tag ofCompound(Map<String, ? extends Tag> c) {
        CompoundTag tag = new CompoundTag();
        c.forEach(tag::addTag);
        return tag;
    }

    @Override
    public Tag ofList(List<? extends Tag> l) {
        ListTag<Tag> tag = new ListTag<>();
        l.forEach(tag::addTag);
        return tag;
    }

    @Override
    public Tag ofArray(int[] a) {
        IntArrayTag tag = new IntArrayTag();
        for (int i : a) tag.add(i);
        return tag;
    }

    @Override
    public Tag ofArray(long[] a) {
        LongArrayTag tag = new LongArrayTag();
        for (long i : a) tag.add(i);
        return tag;
    }

    @Override
    public Tag ofArray(byte[] a) {
        ByteArrayTag tag = new ByteArrayTag();
        for (byte i : a) tag.add(i);
        return tag;
    }

    @Override
    public @Nullable Number getNumberOf(Tag t) {
        if (t instanceof ValueTag<?> valueTag && !(valueTag instanceof StringTag)) {
            return (Number) valueTag.getValue();
        }
        return null;
    }
}
