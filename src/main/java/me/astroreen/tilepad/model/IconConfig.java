package me.astroreen.tilepad.model;

public class IconConfig {
    private IconType type;
    private String value;
    private IconPosition position;

    public IconConfig() {}

    public IconConfig(IconType type, String value, IconPosition position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public IconType getType() {
        return type;
    }

    public void setType(IconType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public IconPosition getPosition() {
        return position;
    }

    public void setPosition(IconPosition position) {
        this.position = position;
    }
}
