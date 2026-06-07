package me.astroreen.tilepad.ui.settings;

import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.service.ConfigService;
import me.astroreen.tilepad.service.ThemeService;

/**
 * Root settings panel: content area (left, grows) + sidebar nav (right, fixed).
 *
 * Layout:
 *   HBox
 *   ├── ScrollPane → active content view  (grows)
 *   └── VBox sidebar                      (fixed 160px, right)
 *       ├── [Themes]
 *       ├── [General]
 *       ├── [Import / Export]
 */
public class SettingsPanel extends HBox {

    private enum Section { THEMES, GENERAL, IMPORT_EXPORT }

    private final AppConfig appConfig;
    private final ConfigService configService;
    private final ThemeService themeService;

    private final ScrollPane contentPane = new ScrollPane();
    private Section activeSection = Section.THEMES;

    // Sidebar nav buttons — kept as fields so we can toggle active style
    private Button themesBtn;
    private Button generalBtn;
    private Button importExportBtn;

    public SettingsPanel(AppConfig appConfig, ConfigService configService, ThemeService themeService) {
        this.appConfig = appConfig;
        this.configService = configService;
        this.themeService = themeService;
        getStyleClass().add("settings-root");
        build();
    }

    private void build() {
        // ---- Content scroll pane ----
        contentPane.setFitToWidth(true);
        contentPane.setFitToHeight(false);
        contentPane.getStyleClass().add("scroll-pane");
        HBox.setHgrow(contentPane, Priority.ALWAYS);

        // ---- Sidebar ----
        VBox sidebar = buildSidebar();

        getChildren().addAll(contentPane, sidebar);
        HBox.setHgrow(this, Priority.ALWAYS);

        // Show default section
        showSection(Section.THEMES);
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox();
        sidebar.getStyleClass().add("settings-sidebar");

        themesBtn      = navButton("Themes",         Section.THEMES);
        generalBtn     = navButton("General",        Section.GENERAL);
        importExportBtn = navButton("Import / Export", Section.IMPORT_EXPORT);

        // Spacer at top
        Region spacer = new Region();
        spacer.setPrefHeight(8);

        sidebar.getChildren().addAll(spacer, themesBtn, generalBtn, importExportBtn);
        return sidebar;
    }

    private Button navButton(String text, Section section) {
        Button btn = new Button(text);
        btn.getStyleClass().add("settings-nav-item");
        btn.setOnAction(e -> showSection(section));
        return btn;
    }

    private void showSection(Section section) {
        activeSection = section;
        updateNavStyles();

        javafx.scene.Node view = switch (section) {
            case THEMES       -> new ThemeSettingsView(appConfig, configService, themeService);
            case GENERAL      -> new GeneralSettingsView(appConfig, configService);
            case IMPORT_EXPORT -> new ImportExportView(appConfig, configService);
        };

        contentPane.setContent(view);
        contentPane.setVvalue(0); // scroll to top on section change
    }

    private void updateNavStyles() {
        // Reset all
        for (Button btn : new Button[]{themesBtn, generalBtn, importExportBtn}) {
            btn.getStyleClass().remove("settings-nav-item-active");
        }
        // Mark active
        Button active = switch (activeSection) {
            case THEMES        -> themesBtn;
            case GENERAL       -> generalBtn;
            case IMPORT_EXPORT -> importExportBtn;
        };
        active.getStyleClass().add("settings-nav-item-active");
    }
}
