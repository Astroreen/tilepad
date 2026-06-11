package me.astroreen.tilepad.service;

import static me.astroreen.tilepad.Tilepad.LOG;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import me.astroreen.tilepad.model.AppConfig;

public class ConfigService {

    private final Gson gson;

    public ConfigService() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Load AppConfig from a JSON file.
     * - If file does not exist: return AppConfig.createDefault()
     * - If JSON is corrupt (JsonSyntaxException): show error Alert + return
     * AppConfig.createDefault()
     * - If IO error: show error Alert + return AppConfig.createDefault()
     */
    public AppConfig load(String filePath) {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            return AppConfig.createDefault();
        }
        try (FileReader reader = new FileReader(filePath)) {
            AppConfig config = gson.fromJson(reader, AppConfig.class);
            if (config == null) {
                return AppConfig.createDefault();
            }
            return config;
        } catch (JsonSyntaxException e) {
            showErrorAlert("Config file is corrupt. Starting with empty config.",
                    "The configuration file at " + filePath + " contains invalid JSON.");
            LOG.throwing(ConfigService.class.getName(), "load", e);
            return AppConfig.createDefault();
        } catch (IOException e) {
            showErrorAlert("Failed to read config file.",
                    "Could not read: " + filePath + "\n" + e.getMessage());
            LOG.throwing(ConfigService.class.getName(), "load", e);
            return AppConfig.createDefault();
        }
    }

    /**
     * Save AppConfig to a JSON file.
     * Creates parent directories if needed.
     */
    public void save(AppConfig config, String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (FileWriter writer = new FileWriter(filePath)) {
                gson.toJson(config, writer);
            }
        } catch (IOException e) {
            showErrorAlert("Failed to save config file.",
                    "Could not write to: " + filePath + "\n" + e.getMessage());
        }
    }

    /**
     * Resolve the effective config file path.
     * If config.getConfigPath() is blank, use the platform default location.
     * Otherwise use config.getConfigPath()
     */
    public String resolveConfigPath(AppConfig config) {
        String path = config.getConfigPath();
        if (path == null || path.isBlank()) {
            return getDefaultConfigPath();
        }
        return path;
    }

    /**
     * Returns the platform-appropriate default config file path.
     * Windows: %APPDATA%\Tilepad\tilepad-config.json
     * Other:   <user.dir>/tilepad-config.json  (preserves existing behaviour)
     */
    public static String getDefaultConfigPath() {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData == null || appData.isBlank()) {
                appData = System.getProperty("user.home") + "\\AppData\\Roaming";
            }
            return appData + "\\Tilepad\\tilepad-config.json";
        }
        return System.getProperty("user.dir") + "/tilepad-config.json";
    }

    /**
     * Show a JavaFX error Alert on the JavaFX Application Thread.
     * Uses Platform.runLater() to ensure thread safety.
     * In headless/test contexts, gracefully skips if JavaFX not initialized.
     */
    private void showErrorAlert(String header, String content) {
        try {
            if (Platform.isFxApplicationThread()) {
                createAndShowAlert(header, content);
            } else {
                Platform.runLater(() -> createAndShowAlert(header, content));
            }
        } catch (IllegalStateException e) {
            // JavaFX toolkit not initialized (e.g., in unit tests) - log to stderr only
            String msg = "ConfigService error: " + header + " - " + content;
            LOG.log(Level.WARNING, msg, e);
        }
    }

    private void createAndShowAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Configuration Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
