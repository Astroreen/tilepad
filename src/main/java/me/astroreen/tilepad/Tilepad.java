package me.astroreen.tilepad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.service.ActionService;
import me.astroreen.tilepad.service.ConfigService;
import me.astroreen.tilepad.service.ThemeService;
import me.astroreen.tilepad.ui.FontLoader;
import me.astroreen.tilepad.ui.MainController;
import me.astroreen.tilepad.ui.SettingsDialog;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tilepad extends Application {

    public static final Logger LOG = Logger.getLogger("TILEPAD");

    @Override
    public void start(Stage stage) throws IOException {
        FontLoader.loadMaterialIconsFont();

        ConfigService configService = new ConfigService();
        ActionService actionService = new ActionService();

        String defaultPath = ConfigService.getDefaultConfigPath();
        Path defaultFile = Paths.get(defaultPath);
        if (!Files.exists(defaultFile)) {
            try (InputStream is = Tilepad.class.getResourceAsStream(
                    "/me/astroreen/tilepad/config/default-config.json")) {
                if (is != null) {
                    if (defaultFile.getParent() != null) {
                        Files.createDirectories(defaultFile.getParent());
                    }
                    Files.copy(is, defaultFile);
                } else {
                    LOG.warning("Default config template not found in resources");
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Could not seed default config", e);
            }
        }

        AppConfig appConfig = configService.load(defaultPath);
        String savedPath = appConfig.getConfigPath();
        if (savedPath != null && !savedPath.isBlank() && !savedPath.equals(defaultPath)
                && Files.exists(Paths.get(savedPath))) {
            appConfig = configService.load(savedPath);
        }
        if (appConfig.getConfigPath() == null || appConfig.getConfigPath().isBlank()) {
            appConfig.setConfigPath(defaultPath);
        }

        FXMLLoader loader = new FXMLLoader(
                Tilepad.class.getResource("/me/astroreen/tilepad/main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setTitle("Tilepad");
        stage.setScene(scene);

        ThemeService themeService = new ThemeService();
        themeService.init(scene);
        themeService.apply(appConfig.getTheme());

        SettingsDialog.restoreWindowBounds(stage, appConfig);
        controller.initialize(appConfig, configService, actionService, themeService, stage);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
