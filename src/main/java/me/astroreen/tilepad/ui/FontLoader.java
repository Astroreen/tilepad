package me.astroreen.tilepad.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.scene.text.Font;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class FontLoader {

    private static Map<String, String> codepoints = null;
    private static final String FALLBACK_CODEPOINT = "e887"; // help_outline

    private FontLoader() {
        // Static utility class, prevent instantiation
    }

    /**
     * Load Material Icons Outlined font into JavaFX font registry.
     * Must be called before any Label uses the font family "Material Icons Outlined".
     */
    public static void loadMaterialIconsFont() {
        try (InputStream is = FontLoader.class.getResourceAsStream(
                "/me/astroreen/tilepad/fonts/MaterialIconsOutlined-Regular.otf")) {
            if (is != null) {
                Font.loadFont(is, 24);
            } else {
                System.err.println("FontLoader: MaterialIconsOutlined-Regular.otf not found in resources");
            }
        } catch (Exception e) {
            System.err.println("FontLoader: Failed to load Material Icons font: " + e.getMessage());
        }
    }

    /**
     * Get the Unicode hex codepoint string for a named icon.
     * Returns FALLBACK_CODEPOINT ("e887" = help_outline) if not found.
     */
    public static String getIconCodepoint(String iconName) {
        if (codepoints == null) {
            loadCodepoints();
        }
        return codepoints.getOrDefault(iconName, FALLBACK_CODEPOINT);
    }

    /**
     * Convert a hex codepoint string (e.g. "e88a") to a Unicode character string.
     */
    public static String codeToChar(String hexCodepoint) {
        try {
            int codePoint = Integer.parseInt(hexCodepoint, 16);
            return String.valueOf((char) codePoint);
        } catch (NumberFormatException e) {
            // Return help_outline fallback char
            return String.valueOf((char) Integer.parseInt(FALLBACK_CODEPOINT, 16));
        }
    }

    public static Set<String> getIconNames() {
        if (codepoints == null) loadCodepoints();
        return Collections.unmodifiableSet(codepoints.keySet());
    }

    private static void loadCodepoints() {
        codepoints = new HashMap<>();
        try (InputStream is = FontLoader.class.getResourceAsStream(
                "/me/astroreen/tilepad/config/icon-codepoints.json");
             Reader reader = new InputStreamReader(is)) {
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> loaded = gson.fromJson(reader, mapType);
            if (loaded != null) {
                codepoints.putAll(loaded);
            }
        } catch (Exception e) {
            System.err.println("FontLoader: Failed to load icon-codepoints.json: " + e.getMessage());
        }
    }
}
