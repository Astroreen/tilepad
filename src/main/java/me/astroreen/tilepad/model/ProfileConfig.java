package me.astroreen.tilepad.model;

import java.util.ArrayList;
import java.util.List;

public class ProfileConfig {
    private String name;
    private int gridColumns = 6;
    private List<TileConfig> tiles = new ArrayList<>();

    public ProfileConfig() {}

    public ProfileConfig(String name) {
        this.name = name;
        this.gridColumns = 6;
        this.tiles = new ArrayList<>();
    }

    public ProfileConfig(String name, int gridColumns, List<TileConfig> tiles) {
        this.name = name;
        this.gridColumns = gridColumns;
        this.tiles = tiles != null ? tiles : new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGridColumns() {
        return gridColumns;
    }

    public void setGridColumns(int gridColumns) {
        this.gridColumns = gridColumns;
    }

    public List<TileConfig> getTiles() {
        return tiles;
    }

    public void setTiles(List<TileConfig> tiles) {
        this.tiles = tiles;
    }
}
