package me.astroreen.tilepad.ui.editor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import me.astroreen.tilepad.model.BackgroundConfig;
import me.astroreen.tilepad.model.BackgroundType;
import me.astroreen.tilepad.model.IconPosition;
import me.astroreen.tilepad.model.ProfileConfig;
import me.astroreen.tilepad.model.TextPosition;
import me.astroreen.tilepad.model.TileConfig;
import me.astroreen.tilepad.service.IconService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EditorCanvas extends AnchorPane {

    private final List<TilePreviewNode> previewNodes = new ArrayList<>();
    private final IconService iconService = new IconService();
    private ProfileConfig currentProfile;
    private Consumer<TileConfig> onTileSelected;
    private TilePreviewNode selectedNode;
    private static final double CELL_WIDTH = 120;
    private static final double CELL_HEIGHT = 120;
    private static final double GAP = 12;

    public EditorCanvas() {
        getStyleClass().add("editor-canvas");
        setMinSize(600, 400);
    }

    public void setOnTileSelected(Consumer<TileConfig> callback) {
        this.onTileSelected = callback;
    }

    public void loadProfile(ProfileConfig profile) {
        currentProfile = profile;
        getChildren().clear();
        previewNodes.clear();
        selectedNode = null;

        if (profile == null || profile.getTiles() == null) {
            return;
        }

        for (TileConfig tile : profile.getTiles()) {
            TilePreviewNode node = createPreviewNode(tile);
            getChildren().add(node);
            previewNodes.add(node);
        }
    }

    public void addTile(TileConfig tile) {
        if (tile == null) {
            return;
        }
        if (currentProfile == null) {
            currentProfile = new ProfileConfig();
        }
        if (currentProfile.getTiles() == null) {
            currentProfile.setTiles(new ArrayList<>());
        }

        currentProfile.getTiles().add(tile);
        TilePreviewNode node = createPreviewNode(tile);
        getChildren().add(node);
        previewNodes.add(node);
        node.select();
    }

    public void removeTile(TileConfig tile) {
        if (tile == null || currentProfile == null || currentProfile.getTiles() == null) {
            return;
        }

        currentProfile.getTiles().remove(tile);

        TilePreviewNode target = null;
        for (TilePreviewNode node : previewNodes) {
            if (node.getTile() == tile) {
                target = node;
                break;
            }
        }

        if (target != null) {
            getChildren().remove(target);
            previewNodes.remove(target);
            if (selectedNode == target) {
                selectedNode = null;
            }
        }
    }

    public void refreshTile(TileConfig tile) {
        for (TilePreviewNode node : previewNodes) {
            if (node.getTile() == tile) {
                node.refresh();
                return;
            }
        }
    }

    private TilePreviewNode createPreviewNode(TileConfig tile) {
        TilePreviewNode node = new TilePreviewNode(tile);
        double x = tile.getCol() * (CELL_WIDTH + GAP);
        double y = tile.getRow() * (CELL_HEIGHT + GAP);
        AnchorPane.setLeftAnchor(node, x);
        AnchorPane.setTopAnchor(node, y);
        return node;
    }

    private class TilePreviewNode extends StackPane {

        private final TileConfig tile;
        private final Rectangle resizeHandle;

        private double dragStartX;
        private double dragStartY;
        private double dragNodeStartX;
        private double dragNodeStartY;
        private boolean isDragging = false;

        private double resizeDragStartX;
        private double resizeDragStartY;
        private int resizeStartColSpan;
        private int resizeStartRowSpan;

        TilePreviewNode(TileConfig tile) {
            this.tile = tile;

            int colSpan = Math.max(1, tile.getColSpan());
            int rowSpan = Math.max(1, tile.getRowSpan());
            setPrefWidth(colSpan * (CELL_WIDTH + GAP) - GAP);
            setPrefHeight(rowSpan * (CELL_HEIGHT + GAP) - GAP);

            setAlignment(Pos.CENTER);
            setPadding(new Insets(10));

            resizeHandle = new Rectangle(10, 10);
            resizeHandle.setFill(Color.web("#adc6ff"));
            StackPane.setAlignment(resizeHandle, Pos.BOTTOM_RIGHT);

            buildContent();
            getChildren().add(resizeHandle);

            setStyle(getBaseStyle());
            registerTileHandlers();
            registerResizeHandlers();
        }

        private void buildContent() {
            String title = tile.getTitle() != null ? tile.getTitle() : "";
            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-text-fill: #d9e3f6; -fx-font-size: 14px; -fx-font-weight: bold;");

            if (tile.getIcon() != null) {
                Node iconNode = iconService.createIcon(tile.getIcon(), 32);
                TextPosition textPos = tile.getTextPosition() != null
                        ? tile.getTextPosition()
                        : TextPosition.UNDER_ICON;

                if (textPos == TextPosition.UNDER_ICON) {
                    VBox content = new VBox(4);
                    content.setAlignment(Pos.CENTER);
                    content.getChildren().add(iconNode);
                    if (!title.isBlank()) {
                        content.getChildren().add(titleLabel);
                    }
                    getChildren().add(content);
                } else {
                    Pos iconAlignment = resolveIconAlignment(tile.getIcon().getPosition());
                    StackPane.setAlignment(iconNode, iconAlignment);
                    getChildren().add(iconNode);
                    if (!title.isBlank()) {
                        StackPane.setAlignment(titleLabel, resolveTextAlignment(textPos));
                        StackPane.setMargin(titleLabel, new Insets(10));
                        getChildren().add(titleLabel);
                    }
                }
            } else {
                getChildren().add(titleLabel);
            }
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

        private void registerTileHandlers() {
            setOnMousePressed(e -> {
                dragStartX = e.getSceneX();
                dragStartY = e.getSceneY();
                dragNodeStartX = AnchorPane.getLeftAnchor(this) != null ? AnchorPane.getLeftAnchor(this) : 0.0;
                dragNodeStartY = AnchorPane.getTopAnchor(this) != null ? AnchorPane.getTopAnchor(this) : 0.0;
                isDragging = false;
                select();
                e.consume();
            });

            setOnMouseDragged(e -> {
                double dx = e.getSceneX() - dragStartX;
                double dy = e.getSceneY() - dragStartY;
                if (Math.abs(dx) > 3 || Math.abs(dy) > 3) {
                    isDragging = true;
                }
                if (isDragging) {
                    double newX = Math.max(0, dragNodeStartX + dx);
                    double newY = Math.max(0, dragNodeStartY + dy);
                    AnchorPane.setLeftAnchor(this, newX);
                    AnchorPane.setTopAnchor(this, newY);
                }
                e.consume();
            });

            setOnMouseReleased(e -> {
                if (isDragging) {
                    double x = AnchorPane.getLeftAnchor(this) != null ? AnchorPane.getLeftAnchor(this) : 0.0;
                    double y = AnchorPane.getTopAnchor(this) != null ? AnchorPane.getTopAnchor(this) : 0.0;

                    int newCol = (int) Math.round(x / (CELL_WIDTH + GAP));
                    int newRow = (int) Math.round(y / (CELL_HEIGHT + GAP));
                    newCol = Math.max(0, newCol);
                    newRow = Math.max(0, newRow);

                    tile.setCol(newCol);
                    tile.setRow(newRow);

                    AnchorPane.setLeftAnchor(this, newCol * (CELL_WIDTH + GAP));
                    AnchorPane.setTopAnchor(this, newRow * (CELL_HEIGHT + GAP));
                } else {
                    if (onTileSelected != null) {
                        onTileSelected.accept(tile);
                    }
                }
                e.consume();
            });
        }

        private void registerResizeHandlers() {
            resizeHandle.setOnMousePressed(e -> {
                resizeDragStartX = e.getSceneX();
                resizeDragStartY = e.getSceneY();
                resizeStartColSpan = Math.max(1, tile.getColSpan());
                resizeStartRowSpan = Math.max(1, tile.getRowSpan());
                select();
                e.consume();
            });

            resizeHandle.setOnMouseDragged(e -> {
                double dx = e.getSceneX() - resizeDragStartX;
                double dy = e.getSceneY() - resizeDragStartY;

                int newColSpan = Math.max(1, resizeStartColSpan + (int) Math.round(dx / (CELL_WIDTH + GAP)));
                int newRowSpan = Math.max(1, resizeStartRowSpan + (int) Math.round(dy / (CELL_HEIGHT + GAP)));

                tile.setColSpan(newColSpan);
                tile.setRowSpan(newRowSpan);

                setPrefWidth(newColSpan * (CELL_WIDTH + GAP) - GAP);
                setPrefHeight(newRowSpan * (CELL_HEIGHT + GAP) - GAP);
                e.consume();
            });
        }

        void refresh() {
            getChildren().removeIf(c -> c != resizeHandle);
            buildContent();
            int colSpan = Math.max(1, tile.getColSpan());
            int rowSpan = Math.max(1, tile.getRowSpan());
            setPrefWidth(colSpan * (CELL_WIDTH + GAP) - GAP);
            setPrefHeight(rowSpan * (CELL_HEIGHT + GAP) - GAP);
            AnchorPane.setLeftAnchor(this, tile.getCol() * (CELL_WIDTH + GAP));
            AnchorPane.setTopAnchor(this, tile.getRow() * (CELL_HEIGHT + GAP));
            if (selectedNode == this) {
                setStyle(getBaseStyle() + "-fx-border-color: #4f93ff; -fx-border-width: 2px;");
            } else {
                setStyle(getBaseStyle());
            }
        }

        private String getBaseStyle() {
            String background = "#16202e";
            BackgroundConfig bg = tile.getBackground();
            if (bg != null && bg.getType() == BackgroundType.COLOR
                    && bg.getValue() != null && !bg.getValue().isBlank()) {
                background = bg.getValue();
            }

            return "-fx-background-color: " + background + ";"
                    + "-fx-background-radius: 8px;"
                    + "-fx-border-color: #424754;"
                    + "-fx-border-width: 1px;"
                    + "-fx-border-radius: 8px;";
        }

        private void select() {
            previewNodes.forEach(n -> n.setStyle(n.getBaseStyle()));
            setStyle(getBaseStyle() + "-fx-border-color: #4f93ff; -fx-border-width: 2px;");
            selectedNode = this;
            if (onTileSelected != null) {
                onTileSelected.accept(tile);
            }
        }

        private TileConfig getTile() {
            return tile;
        }
    }
}
