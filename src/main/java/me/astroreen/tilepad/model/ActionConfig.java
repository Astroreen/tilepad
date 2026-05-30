package me.astroreen.tilepad.model;

/**
 * Minimal stub for compilation. Full implementation added in Task 4.
 */
public class ActionConfig {
    private ActionType type;
    private String value;

    public ActionConfig() {}

    public ActionConfig(ActionType type, String value) {
        this.type = type;
        this.value = value;
    }

    public ActionType getType() {
        return type;
    }

    public void setType(ActionType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
