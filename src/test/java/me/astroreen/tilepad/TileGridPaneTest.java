package me.astroreen.tilepad;

import javafx.application.Platform;
import me.astroreen.tilepad.model.ProfileConfig;
import me.astroreen.tilepad.model.TileConfig;
import me.astroreen.tilepad.model.BackgroundConfig;
import me.astroreen.tilepad.model.BackgroundType;
import me.astroreen.tilepad.ui.TileGridPane;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

class TileGridPaneTest {

    private static boolean jfxAvailable = false;

    @BeforeAll
    static void initJavaFX() {
        try {
            new javafx.embed.swing.JFXPanel();
            jfxAvailable = true;
        } catch (Exception e) {
            jfxAvailable = false;
        }
    }

    private ProfileConfig makeProfile(int cols, List<TileConfig> tiles) {
        ProfileConfig p = new ProfileConfig("test", cols, tiles);
        return p;
    }

    private TileConfig makeTile(int col, int row, int colSpan, int rowSpan) {
        TileConfig t = new TileConfig();
        t.setCol(col); t.setRow(row);
        t.setColSpan(colSpan); t.setRowSpan(rowSpan);
        t.setTitle("tile-" + col + "-" + row);
        t.setBackground(new BackgroundConfig(BackgroundType.COLOR, "#16202e"));
        return t;
    }

    @Test
    void loadProfile_correctNodeCount() throws Exception {
        Assumptions.assumeTrue(jfxAvailable, "JavaFX not available");
        CountDownLatch latch = new CountDownLatch(1);
        final int[] count = {0};
        Platform.runLater(() -> {
            try {
                List<TileConfig> tiles = new ArrayList<>();
                tiles.add(makeTile(0, 0, 1, 1));
                tiles.add(makeTile(1, 0, 1, 1));
                tiles.add(makeTile(2, 0, 1, 1));
                TileGridPane grid = new TileGridPane();
                grid.loadProfile(makeProfile(6, tiles), null);
                count[0] = grid.getChildren().size();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        assertEquals(3, count[0]);
    }

    @Test
    void clamping_colSpanExceedsGrid() throws Exception {
        Assumptions.assumeTrue(jfxAvailable, "JavaFX not available");
        CountDownLatch latch = new CountDownLatch(1);
        final int[] count = {0};
        Platform.runLater(() -> {
            try {
                List<TileConfig> tiles = new ArrayList<>();
                // colSpan=10 in 6-col grid at col=0 → clamped to 6
                tiles.add(makeTile(0, 0, 10, 1));
                TileGridPane grid = new TileGridPane();
                grid.loadProfile(makeProfile(6, tiles), null);
                count[0] = grid.getChildren().size();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        assertEquals(1, count[0], "Clamped tile should still render as 1 node");
    }

    @Test
    void duplicatePosition_firstWins() throws Exception {
        Assumptions.assumeTrue(jfxAvailable, "JavaFX not available");
        CountDownLatch latch = new CountDownLatch(1);
        final int[] count = {0};
        Platform.runLater(() -> {
            try {
                List<TileConfig> tiles = new ArrayList<>();
                tiles.add(makeTile(0, 0, 1, 1));
                tiles.add(makeTile(0, 0, 1, 1)); // duplicate
                TileGridPane grid = new TileGridPane();
                grid.loadProfile(makeProfile(6, tiles), null);
                count[0] = grid.getChildren().size();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        assertEquals(1, count[0], "Duplicate position: only first tile should render");
    }

    @Test
    void loadProfile_null_clearsGrid() throws Exception {
        Assumptions.assumeTrue(jfxAvailable, "JavaFX not available");
        CountDownLatch latch = new CountDownLatch(1);
        final int[] count = {0};
        Platform.runLater(() -> {
            try {
                TileGridPane grid = new TileGridPane();
                List<TileConfig> tiles = List.of(makeTile(0, 0, 1, 1));
                grid.loadProfile(makeProfile(6, tiles), null);
                grid.loadProfile(null, null);
                count[0] = grid.getChildren().size();
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        assertEquals(0, count[0], "Loading null profile should clear grid");
    }

    @Test
    void tilePosition_matchesGridConstraints() throws Exception {
        Assumptions.assumeTrue(jfxAvailable, "JavaFX not available");
        CountDownLatch latch = new CountDownLatch(1);
        final Integer[] colIdx = {null};
        Platform.runLater(() -> {
            try {
                List<TileConfig> tiles = new ArrayList<>();
                tiles.add(makeTile(3, 1, 1, 1));
                TileGridPane grid = new TileGridPane();
                grid.loadProfile(makeProfile(6, tiles), null);
                if (!grid.getChildren().isEmpty()) {
                    colIdx[0] = TileGridPane.getColumnIndex(grid.getChildren().get(0));
                }
            } finally {
                latch.countDown();
            }
        });
        latch.await();
        assertNotNull(colIdx[0]);
        assertEquals(3, colIdx[0], "Tile at col=3 should have GridPane column index 3");
    }
}
