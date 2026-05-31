package me.astroreen.tilepad;

import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.model.ProfileConfig;
import me.astroreen.tilepad.model.TileConfig;
import me.astroreen.tilepad.model.ActionConfig;
import me.astroreen.tilepad.model.ActionType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    @Test
    void defaultValues() {
        TileConfig tile = new TileConfig();
        assertEquals(0, tile.getCol());
        assertEquals(0, tile.getRow());
        assertEquals(0, tile.getColSpan());
        assertEquals(0, tile.getRowSpan());
        assertNull(tile.getTitle());
        assertNull(tile.getId());
        assertNull(tile.getAction());
        assertNull(tile.getBackground());
        assertNull(tile.getIcon());
    }

    @Test
    void createDefault() {
        AppConfig config = AppConfig.createDefault();
        assertEquals("default", config.getActiveProfile());
        assertEquals(1280, config.getWindowWidth(), 0.01);
        assertEquals(800, config.getWindowHeight(), 0.01);
        assertNotNull(config.getProfiles());
        assertFalse(config.getProfiles().isEmpty());
        assertEquals("default", config.getProfiles().get(0).getName());
        assertEquals(6, config.getProfiles().get(0).getGridColumns());
    }

    @Test
    void gridColumns() {
        ProfileConfig profile = new ProfileConfig("test");
        assertEquals(6, profile.getGridColumns());

        profile.setGridColumns(12);
        assertEquals(12, profile.getGridColumns());
    }

    @Test
    void actionConfig() {
        ActionConfig action = new ActionConfig(ActionType.COMMAND, "echo hello");
        assertEquals(ActionType.COMMAND, action.getType());
        assertEquals("echo hello", action.getValue());

        action.setType(ActionType.URL);
        action.setValue("https://example.com");
        assertEquals(ActionType.URL, action.getType());
        assertEquals("https://example.com", action.getValue());
    }
}
