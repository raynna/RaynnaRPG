package net.raynna.silentrpg.server.player.progress;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class ProgressEntry<T> {

    private final Class<T> type;
    private Object value;

    public ProgressEntry(Class<T> type, Object value) {
        this.type = type;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Class<T> getType() {
        return type;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    public CompoundTag toNBT() {
        CompoundTag tag = new CompoundTag();
        switch (value) {
            case Integer intValue -> tag.putInt("value", intValue);
            case Double doubleValue -> tag.putDouble("value", doubleValue);
            case String stringValue -> tag.putString("value", stringValue);
            case Boolean booleanValue -> tag.putByte("value", (byte) (booleanValue ? 1 : 0));
            case null, default -> {
                assert value != null;
                throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
            }
        }
        return tag;
    }

    @SuppressWarnings("unchecked")
    public static <T> ProgressEntry<T> fromNBT(CompoundTag tag) {
        T value = extractValue(tag);
        assert value != null;
        Class<T> valueType = (Class<T>) value.getClass();
        return new ProgressEntry<>(valueType, value);
    }

    @SuppressWarnings("unchecked")
    private static <T> T extractValue(CompoundTag tag) {
        if (!tag.contains("value")) {
            return null;
        }
        if (tag.contains("value", Tag.TAG_INT)) {
            return (T) Integer.valueOf(tag.getInt("value"));
        } else if (tag.contains("value", Tag.TAG_DOUBLE)) {
            return (T) Double.valueOf(tag.getDouble("value"));
        } else if (tag.contains("value", Tag.TAG_STRING)) {
            return (T) tag.getString("value");
        } else if (tag.contains("value", Tag.TAG_BYTE)) {
            return (T) Boolean.valueOf(tag.getByte("value") != 0);
        }
        throw new IllegalArgumentException("Unsupported value type in tag");
    }
}
