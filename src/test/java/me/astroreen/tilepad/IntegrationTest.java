package me.astroreen.tilepad;

import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.model.ProfileConfig;
import me.astroreen.tilepad.model.TileConfig;
import me.astroreen.tilepad.model.ActionConfig;
import me.astroreen.tilepad.model.ActionType;
import me.astroreen.tilepad.model.BackgroundConfig;
import me.astroreen.tilepad.model.BackgroundType;
import me.astroreen.tilepad.service.ConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {

    private final ConfigService configService = new ConfigService();

    @Test
    void fullConfigRoundtrip(@TempDir Path tempDir) {
        Path file = tempDir.resolve("integration-config.json");

        // Build a config with 2 profiles and 2 tiles
        AppConfig original = new AppConfig();
        original.setActiveProfile("main");
        original.setWindowWidth(1440);
        original.setWindowHeight(900);

        TileConfig tile1 = new TileConfig();
        tile1.setId("t1");
        tile1.setTitle("Terminal");
        tile1.setCol(0); tile1.setRow(0);
        tile1.setColSpan(2); tile1.setRowSpan(1);
        tile1.setBackground(new BackgroundConfig(BackgroundType.COLOR, "#1a2b3c"));
        tile1.setAction(new ActionConfig(ActionType.COMMAND, "xterm"));

        TileConfig tile2 = new TileConfig();
        tile2.setId("t2");
        tile2.setTitle("Browser");
        tile2.setCol(2); tile2.setRow(0);
        tile2.setColSpan(1); tile2.setRowSpan(1);
        tile2.setAction(new ActionConfig(ActionType.URL, "https://example.com"));

        List<TileConfig> tiles = new ArrayList<>(List.of(tile1, tile2));
        ProfileConfig main = new ProfileConfig("main", 6, tiles);
        ProfileConfig work = new ProfileConfig("work", 4, new ArrayList<>());
        original.setProfiles(new ArrayList<>(List.of(main, work)));

        configService.save(original, file.toString());
        AppConfig loaded = configService.load(file.toString());

        assertEquals("main", loaded.getActiveProfile());
        assertEquals(1440, loaded.getWindowWidth(), 0.01);
        assertEquals(2, loaded.getProfiles().size());

        ProfileConfig loadedMain = loaded.getProfiles().stream()
                .filter(p -> p.getName().equals("main"))
                .findFirst().orElseThrow();
        assertEquals(6, loadedMain.getGridColumns());
        assertEquals(2, loadedMain.getTiles().size());

        TileConfig loadedTile1 = loadedMain.getTiles().get(0);
        assertEquals("t1", loadedTile1.getId());
        assertEquals("Terminal", loadedTile1.getTitle());
        assertEquals(2, loadedTile1.getColSpan());
        assertEquals(ActionType.COMMAND, loadedTile1.getAction().getType());
        assertEquals("xterm", loadedTile1.getAction().getValue());
    }

    @Test
    void profileSwitch() {
        AppConfig config = AppConfig.createDefault();
        config.getProfiles().add(new ProfileConfig("gaming"));
        config.setActiveProfile("gaming");

        assertEquals("gaming", config.getActiveProfile());
        assertEquals(2, config.getProfiles().size());

        // Switch back
        config.setActiveProfile("default");
        assertEquals("default", config.getActiveProfile());
    }

    @Test
    void editorSave_tileModificationPersists(@TempDir Path tempDir) {
        Path file = tempDir.resolve("editor-save-test.json");

        AppConfig config = AppConfig.createDefault();
        TileConfig tile = new TileConfig();
        tile.setId("edit-me");
        tile.setTitle("Original");
        tile.setCol(0); tile.setRow(0);
        tile.setColSpan(1); tile.setRowSpan(1);
        config.getProfiles().get(0).setTiles(new ArrayList<>(List.of(tile)));

        configService.save(config, file.toString());

        // Simulate editor modifying the tile
        AppConfig loaded = configService.load(file.toString());
        loaded.getProfiles().get(0).getTiles().get(0).setTitle("Edited");
        configService.save(loaded, file.toString());

        // Reload and verify
        AppConfig reloaded = configService.load(file.toString());
        assertEquals("Edited",
                reloaded.getProfiles().get(0).getTiles().get(0).getTitle());
    }
}
