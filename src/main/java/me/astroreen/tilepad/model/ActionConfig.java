package me.astroreen.tilepad.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Minimal stub for compilation. Full implementation added in Task 4.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActionConfig {
    private ActionType type;
    private String value;
}
