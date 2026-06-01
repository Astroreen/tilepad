package me.astroreen.tilepad.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import me.astroreen.tilepad.model.BackgroundConfig;
import me.astroreen.tilepad.model.BackgroundType;
import me.astroreen.tilepad.model.IconPosition;
import me.astroreen.tilepad.model.TextPosition;
import me.astroreen.tilepad.model.TileConfig;
import me.astroreen.tilepad.service.IconService;

public class TileNode extends StackPane {

    private final TileConfig config;

    /**
     * Full constructor with IconService for icon+text rendering.
     */
    public TileNode(TileConfig config, double cellWidth, double cellHeight, IconService iconService) {
        this.config = config;
        setMinWidth(cellWidth);
        setMinHeight(cellHeight);
        setPrefWidth(cellWidth);
        setPrefHeight(cellHeight);
        getStyleClass().add("tile");
        setAlignment(Pos.CENTER);
        applyBackground(config.getBackground());
        if (iconService != null) {
            applyIconAndText(iconService);
        }
    }

    /**
     * Compat constructor without IconService (background only).
     */
    public TileNode(TileConfig config, double cellWidth, double cellHeight) {
        this(config, cellWidth, cellHeight, null);
    }

    /**
     * Apply background layer: COLOR (inline style) or IMAGE (ImageView + gradient overlay).
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

            getChildren().addAll(imageView, gradientOverlay);
        } catch (Exception _) {
            setStyle("-fx-background-color: #16202e; -fx-background-radius: 12px;");
        }
    }

    /**
     * Apply icon and text overlay based on TextPosition.
     * UNDER_ICON: VBox with icon above, title below.
     * Corner positions: text Label anchored to corner, icon at its configured position.
     */
    private void applyIconAndText(IconService iconService) {
        String title = config.getTitle() != null ? config.getTitle() : "";
        TextPosition textPos = config.getTextPosition() != null
                ? config.getTextPosition()
                : TextPosition.UNDER_ICON;

        Node iconNode = iconService.createIcon(config.getIcon(), 32);

        if (textPos == TextPosition.UNDER_ICON) {
            VBox content = new VBox(4);
            content.setAlignment(Pos.CENTER);
            content.getChildren().add(iconNode);
            if (!title.isBlank()) {
                Label titleLabel = makeTitleLabel(title);
                content.getChildren().add(titleLabel);
            }
            getChildren().add(content);
        } else {
            // Icon at center (or configured position), text at corner
            Pos iconAlignment = resolveIconAlignment(
                    config.getIcon() != null ? config.getIcon().getPosition() : null);
            StackPane.setAlignment(iconNode, iconAlignment);
            getChildren().add(iconNode);

            if (!title.isBlank()) {
                Label titleLabel = makeTitleLabel(title);
                StackPane.setAlignment(titleLabel, resolveTextAlignment(textPos));
                StackPane.setMargin(titleLabel, new Insets(6));
                getChildren().add(titleLabel);
            }
        }
    }

    private Label makeTitleLabel(String title) {
        Label label = new Label(title);
        label.getStyleClass().add("tile-title");
        return label;
    }

    private Pos resolveIconAlignment(IconPosition pos) {
        if (pos == null) return Pos.CENTER;
        return switch (pos) {
            case TOP_LEFT     -> Pos.TOP_LEFT;
            case TOP_RIGHT    -> Pos.TOP_RIGHT;
            case BOTTOM_LEFT  -> Pos.BOTTOM_LEFT;
            case BOTTOM_RIGHT -> Pos.BOTTOM_RIGHT;
            case CENTER       -> Pos.CENTER;
        };
    }

    private Pos resolveTextAlignment(TextPosition pos) {
        return switch (pos) {
            case TOP_LEFT     -> Pos.TOP_LEFT;
            case TOP_RIGHT    -> Pos.TOP_RIGHT;
            case BOTTOM_LEFT  -> Pos.BOTTOM_LEFT;
            case BOTTOM_RIGHT -> Pos.BOTTOM_RIGHT;
            default           -> Pos.CENTER;
        };
    }

    public TileConfig getConfig() {
        return config;
    }
}
