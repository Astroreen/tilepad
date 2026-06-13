# Tilepad

A tile-based shortcut launcher for the desktop, built with JavaFX.

Tilepad lets you organize your most-used apps, commands, and URLs into a customizable grid of tiles. Each tile can launch anything — open a terminal app, run a shell command, or visit a URL — with a single click.

This is my first ever program. I built it because I love Java, and I wanted to create something genuinely useful for my own workflow. The entire project was made with AI assistance.

---

## Features

- **Tile grid layout** — arrange tiles in a configurable column grid with variable sizes (colSpan/rowSpan)
- **Multiple profiles** — switch between different tile layouts for different contexts
- **Three action types** — launch applications, run shell commands, or open URLs
- **Material icons** — built-in Material Icons Outlined font with hundreds of icons to choose from
- **Custom image icons** — use your own images as tile icons
- **Tile editor** — visual editor with live preview for creating and modifying tiles
- **Theme support** — customizable color themes
- **Persistent config** — all settings saved to a JSON file, easy to back up or share

---

## Requirements

- Java 25
- Maven
- NixOS users: must be inside the `devenv` shell (see below)

---

## Running

### Standard

```sh
mvn clean javafx:run
```

### NixOS / devenv

The project uses `devenv` to provide native JavaFX libraries. Without it, `mvn javafx:run` will fail.

```sh
devenv shell
mvn clean javafx:run
```

---

## Running Tests

```sh
# Headless tests (no display required)
mvn test

# UI tests (require a display)
mvn test -Dtest=IconServiceTest,TileGridPaneTest
```

---

## Project Structure

```
src/main/java/me/astroreen/tilepad/
  Tilepad.java              # Entry point
  model/                    # Config data models (AppConfig, ProfileConfig, TileConfig, ...)
  service/                  # Business logic (ConfigService, ActionService, IconService, ThemeService)
  ui/                       # JavaFX controllers and custom components
    editor/                 # Tile editor (EditorController, EditorCanvas, TilePropertiesPanel)
```

Config is stored at `<project-root>/tilepad-config.json` when running via Maven.

---

## Tech Stack

- **Java 25**
- **JavaFX 21** (controls, FXML)
- **Gson 2.10.1** — JSON config serialization
- **Lombok** — boilerplate reduction
- **JUnit Jupiter 5** — tests
- **Maven** — build system

---

## License

This project is free to use for personal and non-commercial purposes. See [LICENSE](LICENSE) for details.
