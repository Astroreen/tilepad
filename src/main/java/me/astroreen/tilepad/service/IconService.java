package me.astroreen.tilepad.service;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import me.astroreen.tilepad.model.IconConfig;
import me.astroreen.tilepad.model.IconType;
import me.astroreen.tilepad.ui.FontLoader;

public class IconService {

    /**
     * Create a JavaFX Node representing the icon described by iconConfig.
     * @param iconConfig - the icon configuration (may be null)
     * @param size - icon size in pixels (for font size or ImageView fit dimensions)
     * @return a Label (for MATERIAL) or ImageView (for IMAGE), or fallback Label if config is null/invalid
     */
    public Node createIcon(IconConfig iconConfig, double size) {
        if (iconConfig == null) {
            return createFallbackLabel(size);
        }

        if (iconConfig.getType() == IconType.MATERIAL) {
            return createMaterialIcon(iconConfig.getValue(), size);
        } else if (iconConfig.getType() == IconType.IMAGE) {
            return createImageIcon(iconConfig.getValue(), size);
        }

        return createFallbackLabel(size);
    }

    /**
     * Apply a color to an icon node.
     * For Label (Material icon): sets -fx-text-fill style.
     * For ImageView (PNG): no-op (PNG shows as-is).
     */
    public void applyIconColor(Node icon, String hexColor) {
        if (icon instanceof Label label) {
            label.setStyle(label.getStyle() + " -fx-text-fill: " + hexColor + ";");
        }
        // ImageView: no color override (PNG is shown as-is)
    }

    private Label createMaterialIcon(String iconName, double size) {
        String codepoint = FontLoader.getIconCodepoint(iconName);
        String iconChar = FontLoader.codeToChar(codepoint);
        Label label = new Label(iconChar);
        label.setStyle(
            "-fx-font-family: 'Material Icons Outlined';" +
            "-fx-font-size: " + size + "px;" +
            "-fx-text-fill: #d9e3f6;"
        );
        label.getStyleClass().add("tile-icon-label");
        return label;
    }

    private Node createImageIcon(String filePath, double size) {
        try {
            Image image = new Image("file:" + filePath, size, size, true, true);
            if (image.isError()) {
                return createFallbackLabel(size);
            }
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(size);
            imageView.setFitHeight(size);
            imageView.setPreserveRatio(true);
            return imageView;
        } catch (Exception e) {
            return createFallbackLabel(size);
        }
    }

    private Label createFallbackLabel(double size) {
        String fallbackChar = FontLoader.codeToChar(FontLoader.getIconCodepoint("help_outline"));
        Label label = new Label(fallbackChar);
        label.setStyle(
            "-fx-font-family: 'Material Icons Outlined';" +
            "-fx-font-size: " + size + "px;" +
            "-fx-text-fill: #8c909f;"
        );
        label.getStyleClass().add("tile-icon-label");
        return label;
    }
}
