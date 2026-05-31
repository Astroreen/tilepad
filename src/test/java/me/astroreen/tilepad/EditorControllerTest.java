package me.astroreen.tilepad;

import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.model.ProfileConfig;
import me.astroreen.tilepad.model.TileConfig;
import me.astroreen.tilepad.model.ActionConfig;
import me.astroreen.tilepad.model.ActionType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Model-level tests for editor logic (no JavaFX toolkit required).
 */
class EditorControllerTest {

    @Test
    void preventDeleteLastProfile() {
        AppConfig config = AppConfig.createDefault();
        assertEquals(1, config.getProfiles().size());
        // Simulates the guard: cannot remove when size <= 1
        boolean canDelete = config.getProfiles().size() > 1;
        assertFalse(canDelete, "Should prevent deletion of the last profile");
    }

    @Test
    void addProfile_increasesCount() {
        AppConfig config = AppConfig.createDefault();
        int before = config.getProfiles().size();
        config.getProfiles().add(new ProfileConfig("work"));
        assertEquals(before + 1, config.getProfiles().size());
        assertEquals("work", config.getProfiles().get(before).getName());
    }

    @Test
    void deleteProfile_switchesActive() {
        AppConfig config = AppConfig.createDefault();
        config.getProfiles().add(new ProfileConfig("work"));
        config.setActiveProfile("work");
        assertEquals(2, config.getProfiles().size());

        // Delete "work"
        config.getProfiles().removeIf(p -> p.getName().equals("work"));
        config.setActiveProfile(config.getProfiles().get(0).getName());

        assertEquals(1, config.getProfiles().size());
        assertEquals("default", config.getActiveProfile());
    }

    @Test
    void addTile_toProfile() {
        ProfileConfig profile = new ProfileConfig("default");
        profile.setTiles(new ArrayList<>());
        assertEquals(0, profile.getTiles().size());

        TileConfig tile = new TileConfig();
        tile.setTitle("Test Tile");
        tile.setCol(0);
        tile.setRow(0);
        tile.setColSpan(1);
        tile.setRowSpan(1);
        profile.getTiles().add(tile);

        assertEquals(1, profile.getTiles().size());
        assertEquals("Test Tile", profile.getTiles().get(0).getTitle());
    }

    @Test
    void removeTile_fromProfile() {
        ProfileConfig profile = new ProfileConfig("default");
        profile.setTiles(new ArrayList<>());

        TileConfig tile = new TileConfig();
        tile.setTitle("Remove Me");
        profile.getTiles().add(tile);
        assertEquals(1, profile.getTiles().size());

        profile.getTiles().remove(tile);
        assertEquals(0, profile.getTiles().size());
    }
}
