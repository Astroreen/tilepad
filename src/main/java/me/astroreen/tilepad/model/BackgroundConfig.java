package me.astroreen.tilepad.model;

public class BackgroundConfig {
    private BackgroundType type;
    private String value;

    public BackgroundConfig() {}

    public BackgroundConfig(BackgroundType type, String value) {
        this.type = type;
        this.value = value;
    }

    public BackgroundType getType() {
        return type;
    }

    public void setType(BackgroundType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
