package me.astroreen.tilepad.service;

import javafx.scene.Scene;
import me.astroreen.tilepad.model.ThemeConfig;

public class ThemeService {

    private Scene scene;

    public void init(Scene scene) {
        this.scene = scene;
    }

    public void apply(ThemeConfig theme) {
        applyToScene(theme, scene);
    }

    public void applyToScene(ThemeConfig theme, Scene target) {
        if (target == null || target.getRoot() == null) return;
        target.getRoot().setStyle(generateInlineStyle(theme));
    }

    private String generateInlineStyle(ThemeConfig t) {
        return String.format(
                "tilepad-bg: %s; " +
                "tilepad-bg-deep: %s; " +
                "tilepad-bar: %s; " +
                "tilepad-surface: %s; " +
                "tilepad-surface-high: %s; " +
                "tilepad-surface-highest: %s; " +
                "tilepad-surface-bright: %s; " +
                "tilepad-surface-props: %s; " +
                "tilepad-text: %s; " +
                "tilepad-text-variant: %s; " +
                "tilepad-text-subtle: %s; " +
                "tilepad-border: %s; " +
                "tilepad-accent: %s; " +
                "tilepad-accent-strong: %s; " +
                "tilepad-secondary: %s; " +
                "tilepad-tertiary: %s; " +
                "tilepad-error: %s;",
                t.getColorBg(),
                t.getColorBgDeep(),
                t.getColorBar(),
                t.getColorSurface(),
                t.getColorSurfaceHigh(),
                t.getColorSurfaceHighest(),
                t.getColorSurfaceBright(),
                t.getColorSurfaceProps(),
                t.getColorText(),
                t.getColorTextVariant(),
                t.getColorTextSubtle(),
                t.getColorBorder(),
                t.getColorAccent(),
                t.getColorAccentStrong(),
                t.getColorSecondary(),
                t.getColorTertiary(),
                t.getColorError()
        );
    }
}
