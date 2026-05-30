package me.astroreen.tilepad.service;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import me.astroreen.tilepad.model.ActionConfig;
import me.astroreen.tilepad.model.ActionType;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

public class ActionService {

    /**
     * Check if an action can be executed.
     * Returns false if action is null or its value is null/blank.
     */
    public boolean canExecute(ActionConfig action) {
        if (action == null) return false;
        String value = action.getValue();
        return value != null && !value.isBlank();
    }

    /**
     * Execute a tile action asynchronously on a background thread.
     * COMMAND and APP actions run on a new thread to avoid blocking the UI.
     * URL actions also run on a background thread.
     * Errors are shown via JavaFX Alert on the FX application thread.
     */
    public void execute(ActionConfig action) {
        if (!canExecute(action)) return;

        new Thread(() -> {
            try {
                switch (action.getType()) {
                    case COMMAND -> executeCommand(action.getValue());
                    case URL    -> executeUrl(action.getValue());
                    case APP    -> executeApp(action.getValue());
                }
            } catch (Exception e) {
                showErrorAlert("Action Failed",
                    "Failed to execute action: " + action.getValue() + "\n" + e.getMessage());
            }
        }, "action-executor").start();
    }

    private void executeCommand(String command) throws Exception {
        String[] parts = command.split("\\s+");
        new ProcessBuilder(parts)
            .inheritIO()
            .start();
    }

    private void executeUrl(String url) throws Exception {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(url));
        } else {
            // Fallback for environments without Desktop support (Linux/NixOS)
            new ProcessBuilder("xdg-open", url).inheritIO().start();
        }
    }

    private void executeApp(String path) throws Exception {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            Desktop.getDesktop().open(new File(path));
        } else {
            // Fallback: xdg-open for Linux
            new ProcessBuilder("xdg-open", path).inheritIO().start();
        }
    }

    private void showErrorAlert(String header, String content) {
        try {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Action Error");
                alert.setHeaderText(header);
                alert.setContentText(content);
                alert.showAndWait();
            });
        } catch (IllegalStateException e) {
            System.err.println("ActionService error: " + header + " — " + content);
        }
    }
}
