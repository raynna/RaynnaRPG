package com.raynna.silentrpg.player.progress;

import net.minecraft.nbt.CompoundTag;

import java.util.EnumMap;
import java.util.Map;

public class Progress {

    //TODO Add add(int) method to avoid long lines to increase a progressValue
    //     Add remove(int) method to avoid long lines to reduce a progressValue

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
        if (!(entry.getValue() instanceof Boolean)) {
            throw new IllegalArgumentException("Invalid boolean progress entry for key: " + key);
        }
        entry.setValue(!(Boolean) entry.getValue());
    }

    public boolean isActive(ProgressKey key) {
        ProgressEntry<?> entry = progressMap.get(key);
        if (entry instanceof ProgressEntry<?> && entry.getValue() instanceof Boolean) {
            return (Boolean) entry.getValue();
        }
        throw new IllegalArgumentException("Invalid progress entry for key: " + key);
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
