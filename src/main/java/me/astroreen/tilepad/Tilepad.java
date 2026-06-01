package me.astroreen.tilepad;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.service.ActionService;
import me.astroreen.tilepad.service.ConfigService;
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

        // Seed default config if no config file exists yet
        String configPath = System.getProperty("user.dir") + "/tilepad-config.json";
        Path configFile = Paths.get(configPath);
        if (!Files.exists(configFile)) {
            try (InputStream is = Tilepad.class.getResourceAsStream(
                    "/me/astroreen/tilepad/config/default-config.json")) {
                if (is != null) {
                    if (configFile.getParent() != null) {
                        Files.createDirectories(configFile.getParent());
                    }
                    Files.copy(is, configFile);
                }
                else {
                    LOG.warning("Default config template not found in resources");
                }
            } catch (Exception e) {
                LOG.log(Level.WARNING, "Could not seed default config", e);
            }
        }

        AppConfig appConfig = configService.load(configPath);
        appConfig.setConfigPath(configPath);

        FXMLLoader loader = new FXMLLoader(
                Tilepad.class.getResource("/me/astroreen/tilepad/main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.getController();

        Scene scene = new Scene(root);
        stage.setTitle("Tilepad");
        stage.setScene(scene);

        SettingsDialog.restoreWindowBounds(stage, appConfig);
        controller.initialize(appConfig, configService, actionService, stage);

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
