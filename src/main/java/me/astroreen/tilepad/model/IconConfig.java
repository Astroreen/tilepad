package me.astroreen.tilepad.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class IconConfig {
    private IconType type;
    private String value;
    private IconPosition position;
}
