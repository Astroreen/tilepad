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
                    showErrorAlert("Action Failed",
                            "Action type not configured. Edit the tile and set an action type.");
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

    private String[] buildShellCommand(String command, boolean keepOpen) {
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            return new String[]{"cmd.exe", keepOpen ? "/k" : "/c", command};
        }
        String shell = System.getenv("SHELL");
        if (shell == null || shell.isBlank()) shell = "/bin/sh";
        return keepOpen
                ? new String[]{shell, "-i", "-c", command}
                : new String[]{shell, "-c", command};
    }

    private void executeCommand(String command) throws IOException {
        new ProcessBuilder(buildShellCommand(command, false)).start();
    }

    private void executeCommandInTerminal(String command) throws IOException {
        String[] shellCmd = buildShellCommand(command, true);
        String os = System.getProperty("os.name", "").toLowerCase();

        String[][] candidates = os.contains("win")
                ? new String[][] {
                        concat(new String[]{"wt"}, shellCmd),
                        {"powershell.exe", "-NoExit", "-Command", command},
                        shellCmd,
                  }
                : new String[][] {
                        concat(new String[]{"kitty"}, shellCmd),
                        concat(new String[]{"alacritty", "-e"}, shellCmd),
                        concat(new String[]{"foot"}, shellCmd),
                        concat(new String[]{"xterm", "-e"}, shellCmd),
                        concat(new String[]{"gnome-terminal", "--"}, shellCmd),
                        concat(new String[]{"konsole", "-e"}, shellCmd),
                  };

        for (String[] cmd : candidates) {
            try {
                new ProcessBuilder(cmd).start();
                return;
            } catch (IOException e) {
                LOG.throwing(ActionService.class.getName(), "executeCommandInTerminal", e);
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

    private void executeUrl(String url) throws NullPointerException, IOException, UnsupportedOperationException,
            URISyntaxException, IndexOutOfBoundsException {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI(url));
        } else {
            new ProcessBuilder("xdg-open", url).start();
        }
    }

    private void executeApp(String path) throws UnsupportedOperationException, IOException, NullPointerException,
            IndexOutOfBoundsException, IllegalArgumentException {
        File file = new File(path);
        String os = System.getProperty("os.name", "").toLowerCase();
        if (os.contains("win")) {
            new ProcessBuilder(path).start();
        } else if (file.exists() && file.canExecute()) {
            new ProcessBuilder(path).start();
        } else if (file.exists()) {
            new ProcessBuilder("xdg-open", path).start();
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
