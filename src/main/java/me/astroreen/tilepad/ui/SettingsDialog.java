package me.astroreen.tilepad.ui;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import me.astroreen.tilepad.model.AppConfig;

import java.util.function.Consumer;

public class SettingsDialog {

    public static void show(AppConfig config, Consumer<AppConfig> onSave) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Settings");
        dialog.setHeaderText("Application Settings");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField configPathField = new TextField(
                config.getConfigPath() != null ? config.getConfigPath() : "");
        configPathField.setPromptText("Leave blank for default (./tilepad-config.json)");
        configPathField.setPrefWidth(420);

        grid.add(new Label("Config file path:"), 0, 0);
        grid.add(configPathField, 1, 0);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                config.setConfigPath(configPathField.getText().trim());
                if (onSave != null) {
                    onSave.accept(config);
                }
            }
        });
    }

    public static void saveWindowBounds(Stage stage, AppConfig config) {
        if (stage == null || config == null) return;
        config.setWindowX(stage.getX());
        config.setWindowY(stage.getY());
        config.setWindowWidth(stage.getWidth());
        config.setWindowHeight(stage.getHeight());
    }

    public static void restoreWindowBounds(Stage stage, AppConfig config) {
        if (stage == null || config == null) return;
        if (config.getWindowWidth() > 0) stage.setWidth(config.getWindowWidth());
        if (config.getWindowHeight() > 0) stage.setHeight(config.getWindowHeight());
        if (config.getWindowX() != 0) stage.setX(config.getWindowX());
        if (config.getWindowY() != 0) stage.setY(config.getWindowY());
    }
}
