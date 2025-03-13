package com.raynna.silentrpg.player.progress;

public enum ProgressKey {

    MINED_STONE(Integer.class, 0),
    TEST_DOUBLEKEY(Double.class, 0.0),
    TEST_UNLOCKKEY(Boolean.class, false),


    ;

    private final Class<?> type;
    private final Object defaultValue;

    ProgressKey(Class<?> type, Object defaultValue) {
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public Class<?> getType() {
        return type;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }
}
