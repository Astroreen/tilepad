#!/usr/bin/env bash
# Build a self-contained Tilepad distribution via jpackage.
# Usage: build-dist [TYPE]
#   TYPE: app-image | deb | rpm | dmg | pkg | exe | msi | all  (default: app-image)
#   'all' builds every type supported on the current OS.
#
# Requirements:
#   - JDK 25+ with jpackage (bundled)
#   - JAVAFX_HOME set to the JavaFX SDK root (e.g. /path/to/javafx-sdk-25)
#   Linux:   dpkg (for deb), rpm (for rpm)
#   macOS:   Xcode command line tools (for dmg/pkg)
#   Windows: WiX Toolset 3.x (for exe/msi), run in Git Bash or WSL
set -e

APP_NAME="Tilepad"
APP_VERSION="1.0"
MAIN_JAR="tilepad-1.0-SNAPSHOT.jar"
MAIN_CLASS="me.astroreen.tilepad.Launcher"

PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

OS_NAME="$(uname -s)"
case "$OS_NAME" in
    Linux*)
        ALL_TYPES="app-image deb rpm"
        ;;
    Darwin*)
        ALL_TYPES="app-image dmg pkg"
        ;;
    MINGW*|CYGWIN*|MSYS*)
        ALL_TYPES="app-image exe msi"
        ;;
    *)
        echo "ERROR: Unsupported OS: $OS_NAME" >&2
        exit 1
        ;;
esac

REQUESTED="${1:-app-image}"
if [ "$REQUESTED" = "all" ]; then
    TYPES_TO_BUILD="$ALL_TYPES"
else
    TYPES_TO_BUILD="$REQUESTED"
fi

if [ -z "${JAVAFX_HOME:-}" ]; then
    echo "ERROR: JAVAFX_HOME is not set." >&2
    echo "       Download the JavaFX SDK: https://gluonhq.com/products/javafx/" >&2
    echo "       Then: export JAVAFX_HOME=/path/to/javafx-sdk-25" >&2
    exit 1
fi
JAVAFX_MODULE_PATH="$JAVAFX_HOME/lib"

echo "==> Tilepad distribution builder"
echo "    OS:                 $OS_NAME"
echo "    Requested:          $REQUESTED"
echo "    Will build:         $TYPES_TO_BUILD"
echo "    JavaFX module path: $JAVAFX_MODULE_PATH"
echo ""

# --- Step 1: Build ---
echo "==> [1/3] mvn clean package (tests skipped)"
mvn clean package -DskipTests -q

# --- Step 2: Copy main JAR into lib ---
echo "==> [2/3] Copying main JAR to target/lib/"
if [ ! -f "target/${MAIN_JAR}" ]; then
    echo "ERROR: target/${MAIN_JAR} not found." >&2
    exit 1
fi
cp "target/${MAIN_JAR}" "target/lib/${MAIN_JAR}"
rm -f target/lib/javafx-*.jar

mkdir -p dist/

# --- Step 3: jpackage for each type ---
STEP=1
TOTAL=$(echo "$TYPES_TO_BUILD" | wc -w)

for TYPE in $TYPES_TO_BUILD; do
    echo "==> [3/$((TOTAL + 2))] jpackage --type $TYPE  ($STEP/$TOTAL)"
    # app-image outputs a directory; installers output a file — avoid collisions
    TYPE_DEST="dist/${TYPE}"
    rm -rf "$TYPE_DEST"
    mkdir -p "$TYPE_DEST"

    jpackage \
        --type "$TYPE" \
        --input target/lib \
        --main-jar "$MAIN_JAR" \
        --main-class "$MAIN_CLASS" \
        --module-path "$JAVAFX_MODULE_PATH" \
        --add-modules javafx.controls,javafx.fxml,java.logging,java.desktop,java.prefs,java.xml \
        --java-options "--add-modules=javafx.controls,javafx.fxml,java.logging,java.desktop,java.prefs,java.xml" \
        --java-options "--enable-native-access=javafx.graphics" \
        --name "$APP_NAME" \
        --app-version "$APP_VERSION" \
        --dest "$TYPE_DEST"

    if [ "$TYPE" = "app-image" ]; then
        cp "$JAVAFX_HOME/lib/"*.so "$TYPE_DEST/$APP_NAME/lib/runtime/lib/"
    fi

    echo "    -> written to $TYPE_DEST/"
    STEP=$((STEP + 1))
done

echo ""
echo "==> Done. Output:"
ls -la dist/
