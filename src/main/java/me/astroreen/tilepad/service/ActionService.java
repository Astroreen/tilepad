package me.astroreen.tilepad.service;

import static me.astroreen.tilepad.Tilepad.LOG;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import me.astroreen.tilepad.model.ActionConfig;

public class ActionService {

    /**
     * Check if an action can be executed.
     * Returns false if action is null or its value is null/blank.
     */
    public boolean canExecute(ActionConfig action) {
        if (action == null)
            return false;
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
        if (!canExecute(action))
            return;

        new Thread(() -> {
            try {
                if (action.getType() == null) {
                    showErrorAlert("Action Failed", "Action type not configured. Edit the tile and set an action type.");
                    return;
                }
                switch (action.getType()) {
                    case COMMAND -> executeCommandInTerminal(action.getValue());
                    case URL -> executeUrl(action.getValue());
                    case APP -> executeApp(action.getValue());
                }
            } catch (Exception e) {
                showErrorAlert("Action Failed",
                        "Failed to execute action: " + action.getValue() + "\n" + e.getMessage());
                LOG.throwing(ActionService.class.getName(), "execute", e);
            }
        }, "action-executor").start();
    }

    private void executeCommandInTerminal(String command) throws IOException {
        String shell = System.getenv("SHELL");
        if (shell == null || shell.isBlank()) shell = "zsh";
        String[] shellCmd = {shell, "-i", "-c", command};

        String[][][] candidates = {
            {{"kitty"},          shellCmd},
            {{"alacritty"},      concat(new String[]{"-e"}, shellCmd)},
            {{"foot"},           shellCmd},
            {{"xterm"},          concat(new String[]{"-e"}, shellCmd)},
            {{"gnome-terminal"}, concat(new String[]{"--"}, shellCmd)},
            {{"konsole"},        concat(new String[]{"-e"}, shellCmd)},
        };

        for (String[][] candidate : candidates) {
            String terminal = candidate[0][0];
            String[] args = candidate[1];
            String[] fullCmd = new String[1 + args.length];
            fullCmd[0] = terminal;
            System.arraycopy(args, 0, fullCmd, 1, args.length);
            try {
                new ProcessBuilder(fullCmd).start();
                return;
            } catch (IOException ignored) {
            }
        }

        LOG.warning("No terminal emulator found, executing command directly: " + command);
        executeCommand(command);
    }

    private static String[] concat(String[] prefix, String[] suffix) {
        String[] result = new String[prefix.length + suffix.length];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(suffix, 0, result, prefix.length, suffix.length);
        return result;
    }

    private void executeCommand(String command)
            throws UnsupportedOperationException, IOException, NullPointerException, IndexOutOfBoundsException {
        new ProcessBuilder("zsh", "-i", "-c", command)
                .inheritIO()
                .start();
    }

    private void executeUrl(String url) throws NullPointerException, IOException, UnsupportedOperationException,
            URISyntaxException, IndexOutOfBoundsException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(url));
        } else {
            // Fallback for environments without Desktop support (Linux)
            new ProcessBuilder("xdg-open", url).inheritIO().start();
        }
    }

    private void executeApp(String path) throws UnsupportedOperationException, IOException, NullPointerException,
            IndexOutOfBoundsException, IllegalArgumentException {
        File file = new File(path);
        if (file.exists()) {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(file);
            } else {
                // Fallback: xdg-open for Linux
                new ProcessBuilder("xdg-open", path).inheritIO().start();
            }
        } else {
            executeCommand(path);
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
            final String msg = "ActionService error: " + header + " — " + content;
            LOG.log(Level.WARNING, msg, e);
        }
    }
}
