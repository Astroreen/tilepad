package me.astroreen.tilepad.model;

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

    public TileConfig() {}

    public TileConfig(String id, int col, int row, int colSpan, int rowSpan,
                      String title, IconConfig icon, TextPosition textPosition,
                      BackgroundConfig background, ActionConfig action) {
        this.id = id;
        this.col = col;
        this.row = row;
        this.colSpan = colSpan;
        this.rowSpan = rowSpan;
        this.title = title;
        this.icon = icon;
        this.textPosition = textPosition;
        this.background = background;
        this.action = action;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColSpan() {
        return colSpan;
    }

    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public IconConfig getIcon() {
        return icon;
    }

    public void setIcon(IconConfig icon) {
        this.icon = icon;
    }

    public TextPosition getTextPosition() {
        return textPosition;
    }

    public void setTextPosition(TextPosition textPosition) {
        this.textPosition = textPosition;
    }

    public BackgroundConfig getBackground() {
        return background;
    }

    public void setBackground(BackgroundConfig background) {
        this.background = background;
    }

    public ActionConfig getAction() {
        return action;
    }

    public void setAction(ActionConfig action) {
        this.action = action;
    }
}
