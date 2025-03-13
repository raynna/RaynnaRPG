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
        ProgressEntry<?> entry = progressMap.get(key);
        if (entry == null) {
            throw new IllegalArgumentException("No progress entry found for key: " + key);
        }
        return (T) entry.getValue();
    }

    public <T> void set(ProgressKey key, T value) {
        ProgressEntry<?> entry = progressMap.get(key);
        if (entry == null) {
            throw new IllegalArgumentException("No progress entry found for key: " + key);
        }
        entry.setValue(value);
    }

public void increase(ProgressKey key, int value) {
    ProgressEntry<?> entry = progressMap.get(key);
    if (entry == null || !(entry.getValue() instanceof Number)) {
        throw new IllegalArgumentException("Invalid progress entry for key: " + key);
    }
    Number current = (Number) entry.getValue();
    entry.setValue(current.intValue() + value);
}

public void decrease(ProgressKey key, int value) {
    ProgressEntry<?> entry = progressMap.get(key);
    if (entry == null || !(entry.getValue() instanceof Number)) {
        throw new IllegalArgumentException("Invalid progress entry for key: " + key);
    }
    Number current = (Number) entry.getValue();
    entry.setValue(current.intValue() - value);
}

public void toggle(ProgressKey key) {
    ProgressEntry<?> entry = progressMap.get(key);
    if (entry == null || !(entry.getValue() instanceof Boolean)) {
        throw new IllegalArgumentException("Invalid progress entry for key: " + key);
    }
    Boolean current = (Boolean) entry.getValue();
    entry.setValue(!current);
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
