package me.astroreen.tilepad;

import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.model.ProfileConfig;
import me.astroreen.tilepad.service.ConfigService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConfigServiceTest {

    private final ConfigService configService = new ConfigService();

    @Test
    void roundtrip(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("config.json");

        AppConfig original = AppConfig.createDefault();
        original.setWindowWidth(1920);
        original.setWindowHeight(1080);
        original.setActiveProfile("default");
        ProfileConfig profile = new ProfileConfig("default", 8, List.of());
        original.setProfiles(List.of(profile));

        configService.save(original, file.toString());
        assertTrue(Files.exists(file), "Config file should exist after save");

        AppConfig loaded = configService.load(file.toString());
        assertEquals(1920, loaded.getWindowWidth(), 0.01);
        assertEquals(1080, loaded.getWindowHeight(), 0.01);
        assertEquals("default", loaded.getActiveProfile());
        assertEquals(1, loaded.getProfiles().size());
        assertEquals("default", loaded.getProfiles().get(0).getName());
        assertEquals(8, loaded.getProfiles().get(0).getGridColumns());
    }

    @Test
    void nonExistent(@TempDir Path tempDir) {
        Path file = tempDir.resolve("does-not-exist.json");
        AppConfig config = configService.load(file.toString());
        assertNotNull(config, "Should return default config for missing file");
        assertEquals("default", config.getActiveProfile());
        assertFalse(config.getProfiles().isEmpty(), "Default config should have at least one profile");
    }

    @Test
    void corruptJson(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("corrupt.json");
        Files.writeString(file, "{ this is not valid json !!!");
        AppConfig config = configService.load(file.toString());
        assertNotNull(config, "Should return default config for corrupt JSON");
        assertEquals("default", config.getActiveProfile());
    }

    @Test
    void resolveConfigPath() {
        AppConfig blank = new AppConfig();
        blank.setConfigPath("");
        String resolved = configService.resolveConfigPath(blank);
        assertTrue(resolved.endsWith("tilepad-config.json"),
                "Blank path should resolve to tilepad-config.json, got: " + resolved);

        AppConfig withPath = new AppConfig();
        withPath.setConfigPath("/tmp/my-config.json");
        assertEquals("/tmp/my-config.json", configService.resolveConfigPath(withPath));
    }
}
