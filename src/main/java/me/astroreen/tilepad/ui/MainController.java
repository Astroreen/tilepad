package me.astroreen.tilepad.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.model.ProfileConfig;
import me.astroreen.tilepad.service.ActionService;
import me.astroreen.tilepad.service.ConfigService;
import me.astroreen.tilepad.service.ThemeService;
import me.astroreen.tilepad.ui.editor.EditorController;
import me.astroreen.tilepad.ui.settings.SettingsPanel;

import java.io.IOException;
import java.util.List;

public class MainController {

    // --- FXML bindings ---
    @FXML private Label profileNameLabel;
    @FXML private ScrollPane scrollPane;
    @FXML private BorderPane bottomBar;
    @FXML private Button editorButton;
    @FXML private HBox profileNavBox;
    @FXML private Button settingsButton;

    // --- Services / state ---
    private AppConfig appConfig;
    private ConfigService configService;
    private ActionService actionService;
    private ThemeService themeService;
    private TileGridPane tileGridPane;
    private boolean settingsOpen = false;

    // --- Bottom bar: saved original nodes for restoring after settings ---
    private javafx.scene.Node savedLeft;
    private javafx.scene.Node savedCenter;
    private javafx.scene.Node savedRight;

    public void initialize(AppConfig appConfig, ConfigService configService,
                           ActionService actionService, ThemeService themeService,
                           Stage stage) {
        this.appConfig = appConfig;
        this.configService = configService;
        this.actionService = actionService;
        this.themeService = themeService;

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

    /** Kept for backward compat — delegates to overload without ThemeService. */
    public void initialize(AppConfig appConfig, ConfigService configService,
                           ActionService actionService, Stage stage) {
        initialize(appConfig, configService, actionService, null, stage);
    }

    public void reloadGrid() {
        tileGridPane.loadProfile(getActiveProfile(), actionService);
    }

    private void refreshProfileLabel() {
        if (profileNameLabel != null && appConfig.getActiveProfile() != null) {
            profileNameLabel.setText(appConfig.getActiveProfile());
        }
    }

    // -------------------------------------------------------------------------
    // Bottom-bar navigation
    // -------------------------------------------------------------------------

    @FXML
    private void onOpenSettings() {
        if (settingsOpen) return;
        settingsOpen = true;

        // Save original bottom-bar nodes
        savedLeft   = bottomBar.getLeft();
        savedCenter = bottomBar.getCenter();
        savedRight  = bottomBar.getRight();

        // Build settings-mode bottom bar
        Button backBtn = new Button("← Back");
        backBtn.getStyleClass().add("toolbar-button");
        backBtn.setOnAction(e -> closeSettings());

        Label settingsLabel = new Label("Settings");
        settingsLabel.getStyleClass().add("profile-name-label");

        HBox centerBox = new HBox(settingsLabel);
        centerBox.setAlignment(Pos.CENTER);

        bottomBar.setLeft(backBtn);
        bottomBar.setCenter(centerBox);
        bottomBar.setRight(null);

        // Swap center content to SettingsPanel
        BorderPane root = (BorderPane) bottomBar.getParent();
        SettingsPanel settingsPanel = new SettingsPanel(appConfig, configService, themeService);
        root.setCenter(settingsPanel);
    }

    private void closeSettings() {
        if (!settingsOpen) return;
        settingsOpen = false;

        // Restore bottom bar
        bottomBar.setLeft(savedLeft);
        bottomBar.setCenter(savedCenter);
        bottomBar.setRight(savedRight);

        // Restore tile grid
        BorderPane root = (BorderPane) bottomBar.getParent();
        root.setCenter(scrollPane);

        // Reload grid in case profile was changed while in settings
        reloadGrid();
    }

    // -------------------------------------------------------------------------
    // Profile navigation
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Editor
    // -------------------------------------------------------------------------

    @FXML
    private void onOpenEditor() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/me/astroreen/tilepad/editor.fxml"));
            Parent root = loader.load();
            EditorController editorController = loader.getController();

            Stage editorStage = new Stage();
            editorStage.setTitle("Tile Editor");
            Scene editorScene = new Scene(root, 1000, 700);
            if (themeService != null) themeService.applyToScene(appConfig.getTheme(), editorScene);
            editorStage.setScene(editorScene);

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

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

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
