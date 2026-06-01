package me.astroreen.tilepad.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TileConfig {
    private String id;
    private int col;
    private int row;
    private int colSpan;
    private int rowSpan;
    private String title;
    private IconConfig icon;
    private TextPosition textPosition;
    private BackgroundConfig background;
    private ActionConfig action;
}
