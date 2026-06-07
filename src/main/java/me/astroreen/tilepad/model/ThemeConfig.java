package me.astroreen.tilepad.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Stores the active theme preset name and all 17 named lookup color tokens.
 * Defaults match the original "Command & Control" dark theme.
 *
 * Color tokens map to JavaFX CSS named colors defined on .root:
 *   tilepad-bg, tilepad-bg-deep, tilepad-bar, tilepad-surface,
 *   tilepad-surface-high, tilepad-surface-highest, tilepad-surface-bright,
 *   tilepad-surface-props, tilepad-text, tilepad-text-variant,
 *   tilepad-text-subtle, tilepad-border, tilepad-accent, tilepad-accent-strong,
 *   tilepad-secondary, tilepad-tertiary, tilepad-error
 */
@Getter
@Setter
@NoArgsConstructor
public class ThemeConfig {

    /** Name of the active preset. "custom" means user-edited individual colors. */
    private String preset = "default";

    // --- Backgrounds ---
    /** Main window background */
    private String colorBg = "#091421";
    /** Deepest background (editor canvas, darkest surfaces) */
    private String colorBgDeep = "#050f1c";
    /** Bottom bar and toolbar background */
    private String colorBar = "#121c2a";
    /** Panel, input field, tile background */
    private String colorSurface = "#16202e";
    /** Button background, container-high surfaces */
    private String colorSurfaceHigh = "#212b39";
    /** Button pressed, container-highest surfaces */
    private String colorSurfaceHighest = "#2b3544";
    /** Button hover, surface-bright */
    private String colorSurfaceBright = "#303a48";
    /** Properties panel background */
    private String colorSurfaceProps = "#0d1825";

    // --- Text ---
    /** Primary text (on-surface) */
    private String colorText = "#d9e3f6";
    /** Secondary text (on-surface-variant) */
    private String colorTextVariant = "#c2c6d6";
    /** Subtle / placeholder text (outline) */
    private String colorTextSubtle = "#8c909f";

    // --- Borders ---
    /** Border / outline-variant */
    private String colorBorder = "#424754";

    // --- Accents ---
    /** Primary accent (soft, used for selected text, focus rings) */
    private String colorAccent = "#adc6ff";
    /** Primary accent strong (buttons-primary bg, links) */
    private String colorAccentStrong = "#4d8eff";
    /** Secondary accent (success, green) */
    private String colorSecondary = "#4edea3";
    /** Tertiary accent (warning, orange) */
    private String colorTertiary = "#ffb786";
    /** Error / destructive */
    private String colorError = "#ffb4ab";

    // -------------------------------------------------------------------------
    // Built-in presets
    // -------------------------------------------------------------------------

    public static ThemeConfig defaultTheme() {
        return new ThemeConfig(); // all defaults
    }

    public static ThemeConfig gruvboxTheme() {
        ThemeConfig t = new ThemeConfig();
        t.preset = "gruvbox";
        t.colorBg = "#282828";
        t.colorBgDeep = "#1d2021";
        t.colorBar = "#1d2021";
        t.colorSurface = "#3c3836";
        t.colorSurfaceHigh = "#504945";
        t.colorSurfaceHighest = "#665c54";
        t.colorSurfaceBright = "#7c6f64";
        t.colorSurfaceProps = "#32302f";
        t.colorText = "#ebdbb2";
        t.colorTextVariant = "#d5c4a1";
        t.colorTextSubtle = "#928374";
        t.colorBorder = "#504945";
        t.colorAccent = "#83a598";
        t.colorAccentStrong = "#458588";
        t.colorSecondary = "#b8bb26";
        t.colorTertiary = "#fabd2f";
        t.colorError = "#fb4934";
        return t;
    }

    public static ThemeConfig nordTheme() {
        ThemeConfig t = new ThemeConfig();
        t.preset = "nord";
        t.colorBg = "#2e3440";
        t.colorBgDeep = "#242933";
        t.colorBar = "#242933";
        t.colorSurface = "#3b4252";
        t.colorSurfaceHigh = "#434c5e";
        t.colorSurfaceHighest = "#4c566a";
        t.colorSurfaceBright = "#616e88";
        t.colorSurfaceProps = "#2e3440";
        t.colorText = "#eceff4";
        t.colorTextVariant = "#e5e9f0";
        t.colorTextSubtle = "#d8dee9";
        t.colorBorder = "#4c566a";
        t.colorAccent = "#88c0d0";
        t.colorAccentStrong = "#81a1c1";
        t.colorSecondary = "#a3be8c";
        t.colorTertiary = "#ebcb8b";
        t.colorError = "#bf616a";
        return t;
    }

    public static ThemeConfig catppuccinTheme() {
        ThemeConfig t = new ThemeConfig();
        t.preset = "catppuccin";
        t.colorBg = "#1e1e2e";
        t.colorBgDeep = "#11111b";
        t.colorBar = "#181825";
        t.colorSurface = "#313244";
        t.colorSurfaceHigh = "#45475a";
        t.colorSurfaceHighest = "#585b70";
        t.colorSurfaceBright = "#6c7086";
        t.colorSurfaceProps = "#181825";
        t.colorText = "#cdd6f4";
        t.colorTextVariant = "#bac2de";
        t.colorTextSubtle = "#a6adc8";
        t.colorBorder = "#45475a";
        t.colorAccent = "#89b4fa";
        t.colorAccentStrong = "#74c7ec";
        t.colorSecondary = "#a6e3a1";
        t.colorTertiary = "#f9e2af";
        t.colorError = "#f38ba8";
        return t;
    }

    public static ThemeConfig lightTheme() {
        ThemeConfig t = new ThemeConfig();
        t.preset = "light";
        t.colorBg = "#f8f9fa";
        t.colorBgDeep = "#e9ecef";
        t.colorBar = "#e9ecef";
        t.colorSurface = "#ffffff";
        t.colorSurfaceHigh = "#f1f3f5";
        t.colorSurfaceHighest = "#dee2e6";
        t.colorSurfaceBright = "#ced4da";
        t.colorSurfaceProps = "#f8f9fa";
        t.colorText = "#212529";
        t.colorTextVariant = "#495057";
        t.colorTextSubtle = "#868e96";
        t.colorBorder = "#ced4da";
        t.colorAccent = "#228be6";
        t.colorAccentStrong = "#1971c2";
        t.colorSecondary = "#2f9e44";
        t.colorTertiary = "#e67700";
        t.colorError = "#e03131";
        return t;
    }

    /** Returns the preset ThemeConfig matching the given name, or default. */
    public static ThemeConfig forPreset(String name) {
        return switch (name == null ? "default" : name) {
            case "gruvbox" -> gruvboxTheme();
            case "nord" -> nordTheme();
            case "catppuccin" -> catppuccinTheme();
            case "light" -> lightTheme();
            default -> defaultTheme();
        };
    }
}
