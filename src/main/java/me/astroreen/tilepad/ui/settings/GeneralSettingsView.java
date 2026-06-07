package me.astroreen.tilepad.ui.settings;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.service.ConfigService;

import java.io.File;

public class GeneralSettingsView extends VBox {

    private final AppConfig appConfig;
    private final ConfigService configService;

    public GeneralSettingsView(AppConfig appConfig, ConfigService configService) {
        this.appConfig = appConfig;
        this.configService = configService;
        getStyleClass().add("settings-content");
        build();
    }

    private void build() {
        Label title = new Label("General");
        title.getStyleClass().add("settings-section-title");

        Label subtitle = new Label("Application configuration and file paths.");
        subtitle.getStyleClass().add("settings-section-subtitle");

        // --- Config path ---
        Label configGroupLabel = new Label("CONFIG FILE");
        configGroupLabel.getStyleClass().add("settings-group-title");

        Label configLabel = new Label("Config file path");
        configLabel.getStyleClass().add("settings-row-label");

        TextField configPathField = new TextField(
                appConfig.getConfigPath() != null ? appConfig.getConfigPath() : "");
        configPathField.setPromptText("Leave blank for default (./tilepad-config.json)");
        HBox.setHgrow(configPathField, Priority.ALWAYS);

        Button browseBtn = new Button("Browse...");
        browseBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Config File");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON files", "*.json"));
            if (appConfig.getConfigPath() != null && !appConfig.getConfigPath().isBlank()) {
                File current = new File(appConfig.getConfigPath());
                if (current.getParentFile() != null && current.getParentFile().exists()) {
                    chooser.setInitialDirectory(current.getParentFile());
                }
            }
            Window window = getScene() != null ? getScene().getWindow() : null;
            File chosen = chooser.showOpenDialog(window);
            if (chosen != null) {
                configPathField.setText(chosen.getAbsolutePath());
            }
        });

        HBox configRow = new HBox(8, configPathField, browseBtn);
        configRow.getStyleClass().add("settings-row");

        Button saveBtn = new Button("Save");
        saveBtn.getStyleClass().add("button-primary");
        saveBtn.setOnAction(e -> {
            appConfig.setConfigPath(configPathField.getText().trim());
            configService.save(appConfig, configService.resolveConfigPath(appConfig));
        });

        VBox.setMargin(saveBtn, new Insets(8, 0, 0, 0));

        getChildren().addAll(title, subtitle, configGroupLabel, configLabel, configRow, saveBtn);
        setSpacing(4);
        setPadding(new Insets(32, 40, 32, 40));
    }
}
