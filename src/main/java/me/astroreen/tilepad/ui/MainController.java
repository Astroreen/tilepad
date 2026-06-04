package me.astroreen.tilepad.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.model.ProfileConfig;
import me.astroreen.tilepad.service.ActionService;
import me.astroreen.tilepad.service.ConfigService;
import me.astroreen.tilepad.ui.editor.EditorController;

import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML private Label profileNameLabel;
    @FXML private ScrollPane scrollPane;

    private AppConfig appConfig;
    private ConfigService configService;
    private ActionService actionService;
    private Stage stage;
    private TileGridPane tileGridPane;

    public void initialize(AppConfig appConfig, ConfigService configService,
                           ActionService actionService, Stage stage) {
        this.appConfig = appConfig;
        this.configService = configService;
        this.actionService = actionService;
        this.stage = stage;

        tileGridPane = new TileGridPane();
        scrollPane.setContent(tileGridPane);
        scrollPane.setFitToWidth(true);

        refreshProfileLabel();
        reloadGrid();

        stage.setOnCloseRequest(e -> {
            SettingsDialog.saveWindowBounds(stage, appConfig);
            configService.save(appConfig, configService.resolveConfigPath(appConfig));
        });
    }

    public void reloadGrid() {
        tileGridPane.loadProfile(getActiveProfile(), actionService);
    }

    private void refreshProfileLabel() {
        if (profileNameLabel != null && appConfig.getActiveProfile() != null) {
            profileNameLabel.setText(appConfig.getActiveProfile());
        }
    }

    @FXML
    private void onPrevProfile() {
        List<ProfileConfig> profiles = appConfig.getProfiles();
        if (profiles == null || profiles.isEmpty()) return;
        int idx = profiles.indexOf(getActiveProfile());
        int prev = (idx - 1 + profiles.size()) % profiles.size();
        appConfig.setActiveProfile(profiles.get(prev).getName());
        refreshProfileLabel();
        reloadGrid();
    }

    @FXML
    private void onNextProfile() {
        List<ProfileConfig> profiles = appConfig.getProfiles();
        if (profiles == null || profiles.isEmpty()) return;
        int idx = profiles.indexOf(getActiveProfile());
        int next = (idx + 1) % profiles.size();
        appConfig.setActiveProfile(profiles.get(next).getName());
        refreshProfileLabel();
        reloadGrid();
    }

    @FXML
    private void onOpenEditor() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/me/astroreen/tilepad/editor.fxml"));
            Parent root = loader.load();
            EditorController editorController = loader.getController();

            Stage editorStage = new Stage();
            editorStage.setTitle("Tile Editor");
            editorStage.setScene(new Scene(root, 1000, 700));

            editorController.initialize(appConfig, editorStage, () -> {
                configService.save(appConfig, configService.resolveConfigPath(appConfig));
                reloadGrid();
                refreshProfileLabel();
            });

            editorStage.show();
        } catch (IOException e) {
            showError("Failed to open editor: " + e.getMessage());
        }
    }

    private void onOpenSettings() {
        SettingsDialog.show(appConfig, config ->
                configService.save(config, configService.resolveConfigPath(config)));
    }

    private ProfileConfig getActiveProfile() {
        String active = appConfig.getActiveProfile();
        if (active == null || appConfig.getProfiles() == null) return null;
        return appConfig.getProfiles().stream()
                .filter(p -> active.equals(p.getName()))
                .findFirst()
                .orElse(null);
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
