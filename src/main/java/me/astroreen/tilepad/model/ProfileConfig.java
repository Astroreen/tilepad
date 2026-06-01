package me.astroreen.tilepad.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
