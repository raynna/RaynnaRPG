package net.raynna.raynnarpg.server.player.progress;

import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.Map;

public class Progress { 

    private final Map<ProgressKey, ProgressEntry<?>> progressMap = new EnumMap<>(ProgressKey.class);

    public Progress() {
        updateProgressMap();
    }

    private void updateProgressMap() {
        for (ProgressKey key : ProgressKey.values()) {
            progressMap.putIfAbsent(key, new ProgressEntry<>(key.getType(), key.getDefaultValue()));
        }
    }

    public void resetProgress() {
        for (ProgressKey key : ProgressKey.values()) {
            progressMap.put(key, new ProgressEntry<>(key.getType(), key.getDefaultValue()));
        }
    }
    @SuppressWarnings("unchecked")
    public <T> T get(ProgressKey key) {
        ProgressEntry<?> entry = getEntryOrThrow(key);
        return (T) entry.getValue();
    }
    
    public <T> void set(ProgressKey key, T value) {
        getEntryOrThrow(key).setValue(value);
    }
    
    public void increase(ProgressKey key, int value) {
        modifyNumberEntry(key, value);
    }
    
    public void decrease(ProgressKey key, int value) {
        modifyNumberEntry(key, -value);
    }
    
    public void toggle(ProgressKey key) {
        ProgressEntry<?> entry = getEntryOrThrow(key);
        Object currentValue = entry.getValue();

        if (currentValue instanceof Boolean) {
            boolean newValue = (!(Boolean) currentValue);
            entry.setValue(newValue);
        } else if (currentValue instanceof Byte) {
            boolean newValue = ((Byte) currentValue == 1);
            entry.setValue(newValue);
        } else {
            throw new IllegalArgumentException("Invalid boolean progress entry for key: " + key);
        }
    }

    public boolean isActive(ProgressKey key) {
        Object value = getEntryOrThrow(key).getValue();
        if (!(value instanceof Boolean)) {
            throw new IllegalArgumentException("Invalid boolean progress entry for key: " + key);
        }
        return (Boolean) value;
    }

    private ProgressEntry<?> getEntryOrThrow(ProgressKey key) {
        ProgressEntry<?> entry = progressMap.get(key);
        if (entry == null) {
            throw new IllegalArgumentException("No progress entry found for key: " + key);
        }
        return entry;
    }

    private void modifyNumberEntry(ProgressKey key, int delta) {
        ProgressEntry<?> entry = getEntryOrThrow(key);
        if (!(entry.getValue() instanceof Number)) {
            throw new IllegalArgumentException("Invalid numeric progress entry for key: " + key);
        }
        entry.setValue(((Number) entry.getValue()).intValue() + delta);
    }

    public CompoundTag toNBT() {
        CompoundTag progressTag = new CompoundTag();
        for (Map.Entry<ProgressKey, ProgressEntry<?>> entry : progressMap.entrySet()) {
            String key = entry.getKey().name();
            ProgressEntry<?> progressEntry = entry.getValue();
            progressTag.put(key, progressEntry.toNBT());
        }
        return progressTag;
    }

    public static Progress fromNBT(CompoundTag tag) {
        Progress progress = new Progress();

        for (String key : tag.getAllKeys()) {
            try {
                ProgressKey progressKey = ProgressKey.valueOf(key);

                ProgressEntry<?> progressEntry = ProgressEntry.fromNBT(tag.getCompound(key));
                progress.progressMap.put(progressKey, progressEntry);

            } catch (IllegalArgumentException e) {
                System.out.println("Skipping invalid ProgressKey: " + key);
            }
        }

        return progress;
    }


}
