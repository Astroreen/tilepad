package me.astroreen.tilepad;

import javafx.scene.Node;
import javafx.scene.control.Label;
import me.astroreen.tilepad.model.IconConfig;
import me.astroreen.tilepad.model.IconPosition;
import me.astroreen.tilepad.model.IconType;
import me.astroreen.tilepad.service.IconService;
import me.astroreen.tilepad.ui.FontLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assumptions;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class IconServiceTest {

    // FontLoader tests need no JavaFX toolkit — pure static logic

    @Test
    void fontLoader_codepoint_knownIcon() {
        String cp = FontLoader.getIconCodepoint("home");
        assertNotNull(cp);
        assertFalse(cp.isBlank());
    }

    @Test
    void fontLoader_codepoint_unknown_returnsFallback() {
        String cp = FontLoader.getIconCodepoint("__nonexistent_icon__");
        assertEquals("e887", cp);
    }

    @Test
    void fontLoader_codeToChar() {
        String ch = FontLoader.codeToChar("e887");
        assertNotNull(ch);
        assertEquals(1, ch.length());
    }

    // createIcon tests require FX thread — skipped if toolkit unavailable

    private boolean runOnFXThread(Runnable task) {
        if (java.awt.GraphicsEnvironment.isHeadless()) return false;
        try {
            try { javafx.application.Platform.startup(() -> {}); }
            catch (IllegalStateException ignored) {}
            CountDownLatch latch = new CountDownLatch(1);
            javafx.application.Platform.runLater(() -> {
                try { task.run(); } finally { latch.countDown(); }
            });
            return latch.await(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    void createIcon_null_returnsLabel() {
        final Node[] result = new Node[1];
        boolean ran = runOnFXThread(() ->
                result[0] = new IconService().createIcon(null, 24));
        Assumptions.assumeTrue(ran, "JavaFX FX thread not available");
        assertInstanceOf(Label.class, result[0]);
    }

    @Test
    void createIcon_material_returnsLabel() {
        final Node[] result = new Node[1];
        boolean ran = runOnFXThread(() -> {
            IconConfig config = new IconConfig(IconType.MATERIAL, "home", IconPosition.CENTER);
            result[0] = new IconService().createIcon(config, 32);
        });
        Assumptions.assumeTrue(ran, "JavaFX FX thread not available");
        assertInstanceOf(Label.class, result[0]);
    }

    @Test
    void createIcon_missingImage_returnsFallbackLabel() {
        final Node[] result = new Node[1];
        boolean ran = runOnFXThread(() -> {
            IconConfig config = new IconConfig(IconType.IMAGE, "/nonexistent/image.png", IconPosition.CENTER);
            result[0] = new IconService().createIcon(config, 32);
        });
        Assumptions.assumeTrue(ran, "JavaFX FX thread not available");
        assertInstanceOf(Label.class, result[0], "Missing image should return fallback Label");
    }
}
