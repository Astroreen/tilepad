package me.astroreen.tilepad.ui.editor;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.model.ProfileConfig;
import me.astroreen.tilepad.model.TileConfig;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EditorController {

    @FXML private ComboBox<String> profileComboBox;
    @FXML private Button removeTileButton;
    @FXML private EditorCanvas editorCanvas;
    @FXML private TilePropertiesPanel propertiesPanel;

    private AppConfig appConfig;
    private Stage stage;
    private Runnable onSaveCallback;
    private boolean hasUnsavedChanges = false;

    public void initialize(AppConfig appConfig, Stage stage, Runnable onSaveCallback) {
        this.appConfig = appConfig;
        this.stage = stage;
        this.onSaveCallback = onSaveCallback;

        refreshProfileComboBox();
        profileComboBox.setValue(appConfig.getActiveProfile());

        profileComboBox.setOnAction(e -> {
            String selected = profileComboBox.getValue();
            if (selected != null && !selected.equals(appConfig.getActiveProfile())) {
                if (hasUnsavedChanges && !confirmDiscardChanges()) {
                    profileComboBox.setValue(appConfig.getActiveProfile());
                    return;
                }
                appConfig.setActiveProfile(selected);
                loadCurrentProfile();
                hasUnsavedChanges = false;
            }
        });

        editorCanvas.setOnTileSelected(tile -> {
            propertiesPanel.loadTile(tile);
            removeTileButton.setDisable(tile == null);
        });
        propertiesPanel.setOnChange(() -> hasUnsavedChanges = true);

        stage.setOnCloseRequest(e -> {
            if (hasUnsavedChanges) {
                e.consume();
                if (confirmDiscardChanges()) {
                    hasUnsavedChanges = false;
                    stage.close();
                }
            }
        });

        removeTileButton.setDisable(true);
        loadCurrentProfile();
    }

    @FXML
    private void onAddTile() {
        TileConfig newTile = new TileConfig();
        newTile.setId(UUID.randomUUID().toString());
        newTile.setTitle("New Tile");
        newTile.setColSpan(1);
        newTile.setRowSpan(1);

        ProfileConfig profile = getActiveProfile();
        int col = 0, row = 0;
        if (profile != null && profile.getTiles() != null && !profile.getTiles().isEmpty()) {
            TileConfig last = profile.getTiles().get(profile.getTiles().size() - 1);
            col = last.getCol() + last.getColSpan();
            if (col >= profile.getGridColumns()) { col = 0; row = last.getRow() + 1; }
        }
        newTile.setCol(col);
        newTile.setRow(row);

        editorCanvas.addTile(newTile);
        propertiesPanel.loadTile(newTile);
        hasUnsavedChanges = true;
    }

    @FXML
    private void onRemoveTile() {
        TileConfig selected = propertiesPanel.getCurrentTile();
        if (selected == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Remove Tile");
        confirm.setHeaderText("Remove tile '" + selected.getTitle() + "'?");
        confirm.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    editorCanvas.removeTile(selected);
                    propertiesPanel.clear();
                    removeTileButton.setDisable(true);
                    hasUnsavedChanges = true;
                }
        });
    }

    @FXML
    private void onSave() {
        hasUnsavedChanges = false;
        if (onSaveCallback != null) onSaveCallback.run();
    }

    @FXML
    private void onCancel() {
        if (hasUnsavedChanges && !confirmDiscardChanges()) return;
        stage.close();
    }

    @FXML
    private void onAddProfile() {
        TextInputDialog dialog = new TextInputDialog("New Profile");
        dialog.setTitle("Add Profile");
        dialog.setHeaderText("Profile name:");
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            if (name.isBlank()) return;
            ProfileConfig p = new ProfileConfig(name);
            appConfig.getProfiles().add(p);
            appConfig.setActiveProfile(name);
            refreshProfileComboBox();
            profileComboBox.setValue(name);
            loadCurrentProfile();
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
            new Alert(Alert.AlertType.WARNING, "Cannot delete the last profile.").showAndWait();
            return;
        }
        String current = appConfig.getActiveProfile();
        appConfig.getProfiles().removeIf(p -> p.getName().equals(current));
        String newActive = appConfig.getProfiles().get(0).getName();
        appConfig.setActiveProfile(newActive);
        refreshProfileComboBox();
        profileComboBox.setValue(newActive);
        loadCurrentProfile();
        hasUnsavedChanges = true;
    }

    @FXML
    private void onOpenConfigSettings() {
        String current = appConfig.getConfigPath() != null ? appConfig.getConfigPath() : "";
        TextInputDialog dialog = new TextInputDialog(current);
        dialog.setTitle("Config Path");
        dialog.setHeaderText("Config file path:");
        dialog.setContentText("Path (blank = default ./tilepad-config.json):");
        dialog.getEditor().setPrefWidth(380);
        dialog.showAndWait().ifPresent(path -> {
            appConfig.setConfigPath(path.trim());
            hasUnsavedChanges = true;
        });
    }

    private void loadCurrentProfile() {
        editorCanvas.loadProfile(getActiveProfile());
        propertiesPanel.clear();
        removeTileButton.setDisable(true);
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

    private boolean confirmDiscardChanges() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Unsaved Changes");
        confirm.setHeaderText("You have unsaved changes.");
        confirm.setContentText("Discard changes and continue?");
        Optional<ButtonType> result = confirm.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
