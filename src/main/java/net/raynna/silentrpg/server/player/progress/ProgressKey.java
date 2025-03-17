package net.raynna.silentrpg.server.player.progress;

public enum ProgressKey {

    MINED_STONE(Integer.class, 0),
    TEST_DOUBLEKEY(Double.class, 0.0),
    UNLOCKED_CRIMSON_IRON(Boolean.class, false),
    TEST_STRINGKEY(String.class, "this key has a stringValue"),

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
