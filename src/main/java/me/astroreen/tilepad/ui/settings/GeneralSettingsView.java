package me.astroreen.tilepad.ui.settings;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.model.ProfileConfig;
import me.astroreen.tilepad.service.ConfigService;

import java.io.File;
import java.util.Collections;

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

        // --- Profile order ---
        VBox profileOrderSection = buildProfileOrderSection();

        getChildren().addAll(title, subtitle, configGroupLabel, configLabel, configRow, saveBtn, profileOrderSection);
        setSpacing(4);
        setPadding(new Insets(32, 40, 32, 40));
    }

    private VBox buildProfileOrderSection() {
        Label groupLabel = new Label("PROFILE ORDER");
        groupLabel.getStyleClass().add("settings-group-title");

        Label hintLabel = new Label("The first profile loads on startup.");
        hintLabel.getStyleClass().add("settings-section-subtitle");

        ObservableList<ProfileConfig> items =
                FXCollections.observableArrayList(appConfig.getProfiles());

        ListView<ProfileConfig> listView = new ListView<>(items);
        listView.getStyleClass().add("profile-order-list");

        int cellHeight = 44;
        listView.setFixedCellSize(cellHeight);
        // Height = min(count * (cellHeight+2), 220) + 2 for border
        listView.setPrefHeight(Math.min(items.size() * (cellHeight + 2), 220) + 4);

        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ProfileConfig profile, boolean empty) {
                super.updateItem(profile, empty);
                if (empty || profile == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                int idx = getIndex();
                int size = items.size();

                Label dragHandle = new Label("\u2807");
                dragHandle.getStyleClass().add("profile-order-drag-handle");

                Label nameLabel = new Label(profile.getName());
                nameLabel.getStyleClass().add("profile-order-name");
                HBox.setHgrow(nameLabel, Priority.ALWAYS);

                Button upBtn = new Button("\u25B2");
                upBtn.getStyleClass().add("profile-order-btn");
                upBtn.setDisable(idx == 0);
                upBtn.setOnAction(e -> {
                    int i = getIndex();
                    if (i > 0) {
                        Collections.swap(items, i, i - 1);
                        syncAndSave(items);
                    }
                });

                Button downBtn = new Button("\u25BC");
                downBtn.getStyleClass().add("profile-order-btn");
                downBtn.setDisable(idx == size - 1);
                downBtn.setOnAction(e -> {
                    int i = getIndex();
                    if (i < items.size() - 1) {
                        Collections.swap(items, i, i + 1);
                        syncAndSave(items);
                    }
                });

                HBox row = new HBox(8, dragHandle, nameLabel, upBtn, downBtn);
                row.getStyleClass().add("profile-order-row");

                // Drag source: transfer this row's index as plain text
                row.setOnDragDetected(e -> {
                    var db = row.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(DataFormat.PLAIN_TEXT, String.valueOf(getIndex()));
                    db.setContent(cc);
                    e.consume();
                });

                // Accept drops from other rows
                row.setOnDragOver(e -> {
                    if (e.getGestureSource() != row
                            && e.getDragboard().hasContent(DataFormat.PLAIN_TEXT)) {
                        e.acceptTransferModes(TransferMode.MOVE);
                    }
                    e.consume();
                });

                // Perform the reorder
                row.setOnDragDropped(e -> {
                    var db = e.getDragboard();
                    boolean success = false;
                    if (db.hasContent(DataFormat.PLAIN_TEXT)) {
                        int from = Integer.parseInt((String) db.getContent(DataFormat.PLAIN_TEXT));
                        int to = getIndex();
                        if (from >= 0 && from < items.size() && to >= 0 && to < items.size()
                                && from != to) {
                            ProfileConfig moved = items.remove(from);
                            items.add(to, moved);
                            syncAndSave(items);
                        }
                        success = true;
                    }
                    e.setDropCompleted(success);
                    e.consume();
                });

                row.setOnDragDone(e -> e.consume());

                setGraphic(row);
                setText(null);
            }
        });

        VBox section = new VBox(4, groupLabel, hintLabel, listView);
        return section;
    }

    private void syncAndSave(ObservableList<ProfileConfig> items) {
        appConfig.getProfiles().clear();
        appConfig.getProfiles().addAll(items);
        configService.save(appConfig, configService.resolveConfigPath(appConfig));
    }
}
