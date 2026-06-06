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

### JavaFX version split (important)

- **Maven artifacts** (`pom.xml`): `javafx-controls`, `javafx-fxml`, `javafx-swing` are all `21.0.2`
- **Native runtime**: devenv provides `pkgs.javaPackages.openjfx25` via `LD_LIBRARY_PATH` and `JAVA_MODULE_PATH`
- These two must stay compatible. Do not bump Maven JavaFX artifacts to 25 without also updating the devenv native path.

---

## Commands

| Action | Command |
|---|---|
| Run app | `mvn clean javafx:run` |
| Run headless tests | `mvn test` |
| Run UI tests (need display) | `mvn test -Dtest=IconServiceTest,TileGridPaneTest` |
| Compile only | `mvn compile` |

**Test split:**
- Excluded from default `mvn test` (require a display): `IconServiceTest`, `TileGridPaneTest`
- Run headlessly in default `mvn test`: `ConfigServiceTest`, `ModelTest`, `ActionServiceTest`, `EditorControllerTest`, `IntegrationTest`
- Timeout: 30s per forked test (`forkedProcessTimeoutInSeconds`)

---

## Architecture

```
me.astroreen.tilepad/
  Tilepad.java              # Entry point — extends Application; seeds config, wires FXML + services
  model/
    AppConfig.java          # Root config: profiles list, active profile, window bounds, configPath
    ProfileConfig.java      # Named profile: gridColumns (default 6), tiles list
    TileConfig.java         # Single tile: id, title, col/row, colSpan/rowSpan, icon, action, background
    IconConfig.java         # Icon: type (MATERIAL|IMAGE), name/path, size, position
    ActionConfig.java       # Action: type (APP|COMMAND|URL), value string
    BackgroundConfig.java   # Background: type (COLOR|IMAGE), value string
    ActionType.java         # Enum: APP, COMMAND, URL
    IconType.java           # Enum: MATERIAL, IMAGE
    BackgroundType.java     # Enum: COLOR, IMAGE
    IconPosition.java       # Enum: icon placement on tile
    TextPosition.java       # Enum: text placement on tile
  service/
    ConfigService.java      # Gson JSON load/save; fallback to AppConfig.createDefault()
    ActionService.java      # Executes tile actions (APP/COMMAND/URL)
    IconService.java        # Builds JavaFX Node from IconConfig
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
- `AppConfig.createDefault()` produces one `"default"` profile with `gridColumns=6`.

---

## Icons

- Type `MATERIAL`: resolved via `FontLoader.getIconCodepoint(name)` → `icon-codepoints.json` → rendered as `Label` with font `Material Icons Outlined`.
- Type `IMAGE`: loaded from absolute file path as `ImageView`.
- Fallback: `help_outline` material icon (grey).

---

## Conventions

- **Java 25** — compiler source/target/release all set to 25. Do not downgrade.
- **Lombok** declared as `provided`; annotation processor configured in `maven-compiler-plugin`. Use `@Getter`/`@Setter`/`@NoArgsConstructor` where existing models use it.
- **Logging:** `java.util.logging` via `Tilepad.LOG` static field (`Logger.getLogger("TILEPAD")`). No SLF4J/Logback.
- **Error dialogs:** `ConfigService.showErrorAlert()` pattern — wraps JavaFX `Alert` with `Platform.runLater` guard and catches `IllegalStateException` for headless/test contexts. Follow this pattern in all services.
- No `module-info.java` — project does not use the Java module system explicitly.

---

## NixOS / VSCode Notes

- VSCode Java runtime is pinned to `JavaSE-25` as default in `.vscode/settings.json` (Nix store path). **These paths change on NixOS rebuilds** — update `.vscode/settings.json` if the JDK store path rotates.
- VSCode launch config `"Launch JavaFX (Auto-Path)"` sets `JDK_JAVA_OPTIONS` with `--module-path $JAVAFX_HOME/lib --add-modules javafx.controls,javafx.fxml --enable-native-access=javafx.graphics`.
- If `JAVAFX_HOME` is not set (outside devenv shell), the VSCode launch will fail.
- `.devenv/`, `.direnv/`, `.opencode/`, `.omo/` are gitignored.
