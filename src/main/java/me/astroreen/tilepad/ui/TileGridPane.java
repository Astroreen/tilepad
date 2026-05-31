package me.astroreen.tilepad.ui;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import me.astroreen.tilepad.model.ProfileConfig;
import me.astroreen.tilepad.model.TileConfig;
import me.astroreen.tilepad.service.ActionService;
import me.astroreen.tilepad.service.IconService;

import java.util.HashSet;
import java.util.Set;

public class TileGridPane extends GridPane {

    private static final double CELL_SIZE = 120;
    private static final double GAP = 12;

    private final IconService iconService;

    public TileGridPane() {
        this.iconService = new IconService();
        getStyleClass().add("tile-grid");
        setHgap(GAP);
        setVgap(GAP);
    }

    /**
     * Load a profile into the grid.
     * - Clamps colSpan so tiles don't overflow gridColumns.
     * - Skips tiles with duplicate (col, row) positions (first wins).
     */
    public void loadProfile(ProfileConfig profile, ActionService actionService) {
        getChildren().clear();
        getColumnConstraints().clear();
        getRowConstraints().clear();

        if (profile == null) return;

        int gridColumns = profile.getGridColumns();
        if (gridColumns < 1) gridColumns = 6;

        for (int i = 0; i < gridColumns; i++) {
            ColumnConstraints cc = new ColumnConstraints(CELL_SIZE);
            getColumnConstraints().add(cc);
        }

        if (profile.getTiles() == null) return;

        Set<String> occupied = new HashSet<>();
        final int cols = gridColumns;

        for (TileConfig tile : profile.getTiles()) {
            String posKey = tile.getCol() + "," + tile.getRow();
            if (occupied.contains(posKey)) {
                continue;
            }
            occupied.add(posKey);

            int colSpan = Math.max(1, tile.getColSpan());
            if (tile.getCol() + colSpan > cols) {
                colSpan = cols - tile.getCol();
            }
            if (colSpan < 1) colSpan = 1;

            int rowSpan = Math.max(1, tile.getRowSpan());

            double tileWidth  = colSpan * (CELL_SIZE + GAP) - GAP;
            double tileHeight = rowSpan * (CELL_SIZE + GAP) - GAP;

            TileNode tileNode = new TileNode(tile, tileWidth, tileHeight, iconService);

            if (actionService != null && tile.getAction() != null) {
                tileNode.setOnMouseClicked(e -> actionService.execute(tile.getAction()));
            }

            add(tileNode, tile.getCol(), tile.getRow(), colSpan, rowSpan);
        }
    }
}
