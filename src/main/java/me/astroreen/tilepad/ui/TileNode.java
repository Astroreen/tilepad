package me.astroreen.tilepad.ui;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import me.astroreen.tilepad.model.BackgroundConfig;
import me.astroreen.tilepad.model.BackgroundType;
import me.astroreen.tilepad.model.TileConfig;

public class TileNode extends StackPane {

    private final TileConfig config;
    private static final double DEFAULT_BG_COLOR_ALPHA = 1.0;

    /**
     * Creates a TileNode (StackPane) with background applied.
     * Icon and text overlay will be added in Task 15 (applyIconAndText).
     *
     * @param config     the tile configuration
     * @param cellWidth  total pixel width for this tile (accounting for colSpan)
     * @param cellHeight total pixel height for this tile (accounting for rowSpan)
     */
    public TileNode(TileConfig config, double cellWidth, double cellHeight) {
        this.config = config;
        setMinWidth(cellWidth);
        setMinHeight(cellHeight);
        setPrefWidth(cellWidth);
        setPrefHeight(cellHeight);
        getStyleClass().add("tile");
        setAlignment(Pos.CENTER);
        applyBackground(config.getBackground());
    }

    /**
     * Apply background based on BackgroundConfig.
     * COLOR: set inline -fx-background-color style (user-configured hex color).
     * IMAGE: add ImageView + gradient overlay Region to StackPane.
     * null: fall back to default surface color.
     */
    protected void applyBackground(BackgroundConfig bg) {
        if (bg == null || bg.getType() == BackgroundType.COLOR) {
            String color = (bg != null && bg.getValue() != null && !bg.getValue().isBlank())
                    ? bg.getValue()
                    : "#16202e";
            setStyle("-fx-background-color: " + color + "; -fx-background-radius: 12px;");
        } else if (bg.getType() == BackgroundType.IMAGE && bg.getValue() != null) {
            applyImageBackground(bg.getValue());
        }
    }

    private void applyImageBackground(String imagePath) {
        try {
            Image image = new Image("file:" + imagePath, true);
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(getPrefWidth());
            imageView.setFitHeight(getPrefHeight());
            imageView.setPreserveRatio(false);

            // Gradient overlay: dark at bottom, transparent at top
            Region gradientOverlay = new Region();
            gradientOverlay.setStyle(
                "-fx-background-color: linear-gradient(" +
                "from 0% 100% to 0% 0%, " +
                "rgba(0,0,0,0.8) 0%, " +
                "rgba(0,0,0,0.0) 60%" +
                ");"
            );
            gradientOverlay.setMinWidth(getPrefWidth());
            gradientOverlay.setMinHeight(getPrefHeight());

            // Add image and gradient at bottom of StackPane z-order
            getChildren().addAll(imageView, gradientOverlay);
        } catch (Exception e) {
            // Fallback to default color if image fails to load
            setStyle("-fx-background-color: #16202e; -fx-background-radius: 12px;");
        }
    }

    /**
     * Return the TileConfig associated with this node.
     */
    public TileConfig getConfig() {
        return config;
    }
}
