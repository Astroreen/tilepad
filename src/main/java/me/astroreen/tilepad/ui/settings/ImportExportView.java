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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ImportExportView extends VBox {

    private final AppConfig appConfig;
    private final ConfigService configService;

    public ImportExportView(AppConfig appConfig, ConfigService configService) {
        this.appConfig = appConfig;
        this.configService = configService;
        getStyleClass().add("settings-content");
        build();
    }

    private void build() {
        Label title = new Label("Import / Export");
        title.getStyleClass().add("settings-section-title");

        Label subtitle = new Label("Back up or restore your Tilepad configuration.");
        subtitle.getStyleClass().add("settings-section-subtitle");

        getChildren().addAll(title, subtitle,
                buildExportCard(),
                buildImportCard());
        setSpacing(16);
        setPadding(new Insets(32, 40, 32, 40));
    }

    private VBox buildExportCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("import-export-card");

        Label cardTitle = new Label("Export Config");
        cardTitle.getStyleClass().add("import-export-title");

        Label desc = new Label("Save a copy of your current config file to a chosen location.");
        desc.getStyleClass().add("import-export-desc");
        desc.setMaxWidth(500);

        TextField pathField = new TextField();
        pathField.setPromptText("Choose destination file...");
        HBox.setHgrow(pathField, Priority.ALWAYS);

        Button browseBtn = new Button("Browse...");
        browseBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Export Config To");
            chooser.setInitialFileName("tilepad-config.json");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON files", "*.json"));
            Window window = getScene() != null ? getScene().getWindow() : null;
            File chosen = chooser.showSaveDialog(window);
            if (chosen != null) pathField.setText(chosen.getAbsolutePath());
        });

        HBox row = new HBox(8, pathField, browseBtn);

        Button exportBtn = new Button("Export");
        exportBtn.getStyleClass().add("button-primary");
        exportBtn.setOnAction(e -> {
            String dest = pathField.getText().trim();
            if (dest.isBlank()) { showInfo(card, "Choose a destination file first."); return; }
            try {
                String src = configService.resolveConfigPath(appConfig);
                Files.copy(Paths.get(src), Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
                showInfo(card, "Exported to: " + dest);
            } catch (IOException ex) {
                showInfo(card, "Export failed: " + ex.getMessage());
            }
        });

        card.getChildren().addAll(cardTitle, desc, row, exportBtn);
        return card;
    }

    private VBox buildImportCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add("import-export-card");

        Label cardTitle = new Label("Import Config");
        cardTitle.getStyleClass().add("import-export-title");

        Label desc = new Label("Load a config file and replace the current configuration. The app will reload.");
        desc.getStyleClass().add("import-export-desc");
        desc.setMaxWidth(500);

        TextField pathField = new TextField();
        pathField.setPromptText("Choose source file...");
        HBox.setHgrow(pathField, Priority.ALWAYS);

        Button browseBtn = new Button("Browse...");
        browseBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Import Config From");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("JSON files", "*.json"));
            Window window = getScene() != null ? getScene().getWindow() : null;
            File chosen = chooser.showOpenDialog(window);
            if (chosen != null) pathField.setText(chosen.getAbsolutePath());
        });

        HBox row = new HBox(8, pathField, browseBtn);

        Button importBtn = new Button("Import & Reload");
        importBtn.getStyleClass().add("button-primary");
        importBtn.setOnAction(e -> {
            String src = pathField.getText().trim();
            if (src.isBlank()) { showInfo(card, "Choose a source file first."); return; }
            try {
                String dest = configService.resolveConfigPath(appConfig);
                Files.copy(Paths.get(src), Paths.get(dest), StandardCopyOption.REPLACE_EXISTING);
                showInfo(card, "Imported. Restart the app to apply.");
            } catch (IOException ex) {
                showInfo(card, "Import failed: " + ex.getMessage());
            }
        });

        card.getChildren().addAll(cardTitle, desc, row, importBtn);
        return card;
    }

    /** Shows a temporary status label at the bottom of the card. */
    private void showInfo(VBox card, String message) {
        card.getChildren().removeIf(n -> "status-label".equals(n.getUserData()));
        Label lbl = new Label(message);
        lbl.setUserData("status-label");
        lbl.getStyleClass().add("text-on-surface-variant");
        lbl.setStyle("-fx-font-size: 12px;");
        card.getChildren().add(lbl);
    }
}
