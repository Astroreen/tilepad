package me.astroreen.tilepad.ui.settings;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import me.astroreen.tilepad.model.AppConfig;
import me.astroreen.tilepad.model.ThemeConfig;
import me.astroreen.tilepad.service.ConfigService;
import me.astroreen.tilepad.service.ThemeService;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ThemeSettingsView extends VBox {

    private final AppConfig appConfig;
    private final ConfigService configService;
    private final ThemeService themeService;

    /** Map of display label → (getter, setter) for each ThemeConfig color field */
    private record ColorField(String label, Function<ThemeConfig, String> getter,
                              BiConsumer<ThemeConfig, String> setter) {}

    private static final ColorField[] COLOR_FIELDS = {
        new ColorField("Main Background",     ThemeConfig::getColorBg,             (t, v) -> t.setColorBg(v)),
        new ColorField("Deep Background",     ThemeConfig::getColorBgDeep,         (t, v) -> t.setColorBgDeep(v)),
        new ColorField("Bar / Toolbar",       ThemeConfig::getColorBar,            (t, v) -> t.setColorBar(v)),
        new ColorField("Surface / Panel",     ThemeConfig::getColorSurface,        (t, v) -> t.setColorSurface(v)),
        new ColorField("Button Background",   ThemeConfig::getColorSurfaceHigh,    (t, v) -> t.setColorSurfaceHigh(v)),
        new ColorField("Button Pressed",      ThemeConfig::getColorSurfaceHighest, (t, v) -> t.setColorSurfaceHighest(v)),
        new ColorField("Button Hover",        ThemeConfig::getColorSurfaceBright,  (t, v) -> t.setColorSurfaceBright(v)),
        new ColorField("Properties Panel",    ThemeConfig::getColorSurfaceProps,   (t, v) -> t.setColorSurfaceProps(v)),
        new ColorField("Primary Text",        ThemeConfig::getColorText,           (t, v) -> t.setColorText(v)),
        new ColorField("Secondary Text",      ThemeConfig::getColorTextVariant,    (t, v) -> t.setColorTextVariant(v)),
        new ColorField("Subtle / Placeholder",ThemeConfig::getColorTextSubtle,     (t, v) -> t.setColorTextSubtle(v)),
        new ColorField("Border",              ThemeConfig::getColorBorder,         (t, v) -> t.setColorBorder(v)),
        new ColorField("Accent (soft)",       ThemeConfig::getColorAccent,         (t, v) -> t.setColorAccent(v)),
        new ColorField("Accent (strong)",     ThemeConfig::getColorAccentStrong,   (t, v) -> t.setColorAccentStrong(v)),
        new ColorField("Secondary Accent",    ThemeConfig::getColorSecondary,      (t, v) -> t.setColorSecondary(v)),
        new ColorField("Tertiary Accent",     ThemeConfig::getColorTertiary,       (t, v) -> t.setColorTertiary(v)),
        new ColorField("Error",               ThemeConfig::getColorError,          (t, v) -> t.setColorError(v)),
    };

    private static final String[][] PRESETS = {
        {"Default", "default"},
        {"Gruvbox", "gruvbox"},
        {"Nord",    "nord"},
        {"Catppuccin", "catppuccin"},
        {"Light",   "light"},
    };

    public ThemeSettingsView(AppConfig appConfig, ConfigService configService, ThemeService themeService) {
        this.appConfig = appConfig;
        this.configService = configService;
        this.themeService = themeService;
        getStyleClass().add("settings-content");
        build();
    }

    private void build() {
        Label title = new Label("Themes");
        title.getStyleClass().add("settings-section-title");

        Label subtitle = new Label("Choose a preset or customize every color individually. Changes apply live.");
        subtitle.getStyleClass().add("settings-section-subtitle");

        // ---- Preset row ----
        Label presetsLabel = new Label("PRESETS");
        presetsLabel.getStyleClass().add("settings-group-title");

        HBox presetRow = new HBox(10);
        presetRow.setAlignment(Pos.CENTER_LEFT);
        String activePreset = appConfig.getTheme().getPreset();

        for (String[] preset : PRESETS) {
            String displayName = preset[0];
            String presetKey  = preset[1];
            Button btn = new Button(displayName);
            btn.getStyleClass().add("theme-preset-btn");
            if (presetKey.equals(activePreset)) {
                btn.getStyleClass().add("theme-preset-btn-active");
            }
            btn.setOnAction(e -> {
                ThemeConfig newTheme = ThemeConfig.forPreset(presetKey);
                appConfig.setTheme(newTheme);
                themeService.apply(newTheme);
                configService.save(appConfig, configService.resolveConfigPath(appConfig));
                // Rebuild to show updated pickers + active button
                getChildren().clear();
                build();
            });
            presetRow.getChildren().add(btn);
        }

        // ---- Color pickers ----
        Label colorsLabel = new Label("CUSTOM COLORS");
        colorsLabel.getStyleClass().add("settings-group-title");

        Label colorsDesc = new Label("Selecting a custom color sets the preset to \"custom\".");
        colorsDesc.getStyleClass().add("settings-section-subtitle");
        colorsDesc.setStyle("-fx-padding: 0 0 8 0;");

        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(8);
        grid.setPadding(new Insets(0, 0, 16, 0));

        ThemeConfig theme = appConfig.getTheme();
        for (int i = 0; i < COLOR_FIELDS.length; i++) {
            ColorField field = COLOR_FIELDS[i];

            Label lbl = new Label(field.label());
            lbl.getStyleClass().add("color-row-label");

            ColorPicker picker = new ColorPicker(parseColor(field.getter().apply(theme)));
            picker.getStyleClass().addAll("color-picker", "split-button");
            picker.setPrefWidth(140);

            final ColorField capturedField = field;
            picker.setOnAction(e -> {
                String hex = toHex(picker.getValue());
                capturedField.setter().accept(appConfig.getTheme(), hex);
                appConfig.getTheme().setPreset("custom");
                themeService.apply(appConfig.getTheme());
                configService.save(appConfig, configService.resolveConfigPath(appConfig));
            });

            int col = (i < 9) ? 0 : 2;
            int row = (i < 9) ? i : i - 9;
            grid.add(lbl, col, row);
            grid.add(picker, col + 1, row);
        }

        getChildren().addAll(title, subtitle, presetsLabel, presetRow,
                colorsLabel, colorsDesc, grid);
        setSpacing(4);
        setPadding(new Insets(32, 40, 32, 40));
    }

    // ---- Helpers ----

    private static Color parseColor(String hex) {
        try {
            return Color.web(hex);
        } catch (Exception e) {
            return Color.web("#888888");
        }
    }

    private static String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(c.getRed()   * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue()  * 255));
    }
}
