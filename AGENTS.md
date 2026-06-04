# AGENTS.md — Tilepad

JavaFX tile/shortcut launcher desktop app. Java 25, Maven, NixOS devenv.

---

## Dev Environment

**Must be inside devenv shell** for native JavaFX libs and correct JDK:

```sh
devenv shell
```

This sets `LD_LIBRARY_PATH`, `JAVAFX_HOME`, `JAVA_MODULE_PATH`, `JAVA_LIBRARY_PATH` automatically.
Without the shell, `mvn javafx:run` will fail — native JFX libs won't resolve.

`JAVAFX_HOME` points to `/home/astroreen/.local/share/javafx/javafx-sdk-25.0.2` (used by VSCode launch config).

---

## Commands

| Action | Command |
|---|---|
| Run app | `mvn clean javafx:run` |
| Run headless tests | `mvn test` |
| Run UI tests (need display) | `mvn test -Dtest=IconServiceTest,TileGridPaneTest` |
| Compile only | `mvn compile` |

**Test quirk:** `IconServiceTest` and `TileGridPaneTest` are excluded from the default `mvn test` run (require a display). Run them manually with `-Dtest=`. Timeout: 30s per forked test.

---

## Architecture

```
me.astroreen.tilepad/
  Tilepad.java              # Entry point — extends javafx.application.Application
  model/                    # Plain POJOs: AppConfig, ProfileConfig, TileConfig, IconConfig, etc.
  service/
    ConfigService.java      # Gson JSON load/save, graceful fallback to AppConfig.createDefault()
    ActionService.java      # Executes tile actions (APP / COMMAND / URL)
    IconService.java        # Builds JavaFX Node from IconConfig (Material font or file image)
  ui/
    MainController.java     # Root FXML controller wired in Tilepad.start()
    TileGridPane.java       # Grid layout of tiles
    TileNode.java           # Single tile widget
    FontLoader.java         # Loads MaterialIconsOutlined-Regular.otf + codepoint map
    SettingsDialog.java     # Window bounds save/restore
    editor/
      EditorController.java
      EditorCanvas.java
      TilePropertiesPanel.java
```

FXML files: `main.fxml`, `editor.fxml` in `src/main/resources/me/astroreen/tilepad/`.
CSS: `styles/main.css`, `styles/tiles.css`.

---

## Config File

- Location: `<user.dir>/tilepad-config.json` (project root when running via Maven).
- Seeded from `src/main/resources/.../config/default-config.json` if missing.
- The `configPath` field inside the JSON stores the absolute path to itself.
- Serialized with Gson pretty-printing; no custom type adapters.

---

## Icons

- Type `MATERIAL`: resolved via `FontLoader.getIconCodepoint(name)` → `icon-codepoints.json` → rendered as `Label` with font `Material Icons Outlined`.
- Type `IMAGE`: loaded from absolute file path as `ImageView`.
- Fallback: `help_outline` material icon (grey).

---

## Conventions

- **Java 25** — compiler source/target/release all set to 25. Do not downgrade.
- **Lombok** declared as `provided`; annotation processor configured in `maven-compiler-plugin`. Use `@Getter`/`@Setter`/etc. where existing models use it.
- **Logging:** `java.util.logging` via `Tilepad.LOG` static field. No SLF4J/Logback.
- **Error dialogs:** `ConfigService.showErrorAlert()` pattern — wraps JavaFX `Alert` with `Platform.runLater` guard and catches `IllegalStateException` for headless/test contexts. Follow this pattern in services.
- No module-info.java — project does not use the Java module system explicitly.

---

## NixOS / VSCode Notes

- VSCode Java runtime is pinned to `JavaSE-25` in `.vscode/settings.json` (Nix store path).
- VSCode launch config `"Launch JavaFX (Auto-Path)"` sets `JDK_JAVA_OPTIONS` with `--module-path $JAVAFX_HOME/lib --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics`.
- If `JAVAFX_HOME` is not set (outside devenv shell), the VSCode launch will fail.
- `.devenv/`, `.direnv/`, `.opencode/`, `.omo/` are gitignored.
