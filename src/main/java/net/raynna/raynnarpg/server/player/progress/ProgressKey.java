package net.raynna.raynnarpg.server.player.progress;

public enum ProgressKey {

    MINED_STONE(Integer.class, 0),
    FIRST_TIME_LOGGED_IN(Boolean.class, true)

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
