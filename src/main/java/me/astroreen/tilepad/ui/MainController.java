package me.astroreen.tilepad.ui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.model.ProfileConfig;
import me.astroreen.tilepad.service.ActionService;
import me.astroreen.tilepad.service.ConfigService;
import me.astroreen.tilepad.ui.editor.EditorController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainController {

    @FXML private ComboBox<String> profileComboBox;
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

        refreshProfileComboBox();
        profileComboBox.setValue(appConfig.getActiveProfile());

        profileComboBox.setOnAction(e -> {
            String selected = profileComboBox.getValue();
            if (selected != null) {
                appConfig.setActiveProfile(selected);
                reloadGrid();
            }
        });

        reloadGrid();

        stage.setOnCloseRequest(e -> {
            SettingsDialog.saveWindowBounds(stage, appConfig);
            configService.save(appConfig, configService.resolveConfigPath(appConfig));
        });
    }

    public void reloadGrid() {
        tileGridPane.loadProfile(getActiveProfile(), actionService);
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
            });

            editorStage.show();
        } catch (IOException e) {
            showError("Failed to open editor: " + e.getMessage());
        }
    }

    @FXML
    private void onOpenSettings() {
        SettingsDialog.show(appConfig, config ->
                configService.save(config, configService.resolveConfigPath(config)));
    }

    @FXML
    private void onAddProfile() {
        TextInputDialog dialog = new TextInputDialog("New Profile");
        dialog.setTitle("Add Profile");
        dialog.setHeaderText("Enter profile name:");
        dialog.setContentText("Name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.isBlank()) return;
            ProfileConfig newProfile = new ProfileConfig(name);
            appConfig.getProfiles().add(newProfile);
            appConfig.setActiveProfile(name);
            refreshProfileComboBox();
            profileComboBox.setValue(name);
            reloadGrid();
        });
    }

    @FXML
    private void onRenameProfile() {
        String current = appConfig.getActiveProfile();
        TextInputDialog dialog = new TextInputDialog(current);
        dialog.setTitle("Rename Profile");
        dialog.setHeaderText("Rename profile '" + current + "':");
        dialog.setContentText("New name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            if (newName.isBlank()) return;
            ProfileConfig profile = getActiveProfile();
            if (profile != null) {
                profile.setName(newName);
                appConfig.setActiveProfile(newName);
                refreshProfileComboBox();
                profileComboBox.setValue(newName);
            }
        });
    }

    @FXML
    private void onDeleteProfile() {
        if (appConfig.getProfiles().size() <= 1) {
            showError("Cannot delete the last profile. Rename it instead.");
            return;
        }
        String current = appConfig.getActiveProfile();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Profile");
        confirm.setHeaderText("Delete profile '" + current + "'?");
        confirm.setContentText("This action cannot be undone.");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                appConfig.getProfiles().removeIf(p -> p.getName().equals(current));
                String newActive = appConfig.getProfiles().get(0).getName();
                appConfig.setActiveProfile(newActive);
                refreshProfileComboBox();
                profileComboBox.setValue(newActive);
                reloadGrid();
            }
        });
    }

    private void refreshProfileComboBox() {
        List<String> names = appConfig.getProfiles().stream()
                .map(ProfileConfig::getName)
                .toList();
        profileComboBox.setItems(FXCollections.observableArrayList(names));
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
