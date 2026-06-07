package me.astroreen.tilepad.ui.editor;

import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import me.astroreen.tilepad.model.ActionConfig;
import me.astroreen.tilepad.model.ActionType;
import me.astroreen.tilepad.model.BackgroundConfig;
import me.astroreen.tilepad.model.BackgroundType;
import me.astroreen.tilepad.model.IconConfig;
import me.astroreen.tilepad.model.IconPosition;
import me.astroreen.tilepad.model.IconType;
import me.astroreen.tilepad.model.TextPosition;
import me.astroreen.tilepad.model.TileConfig;

import java.io.File;

public class TilePropertiesPanel extends VBox {

    private TileConfig currentTile;
    private Runnable onChange;
    private Stage ownerStage;
    private boolean loading = false;

    private final TextField titleField                 = new TextField();
    private final Spinner<Integer> colSpinner          = new Spinner<>();
    private final Spinner<Integer> rowSpinner          = new Spinner<>();
    private final Spinner<Integer> colSpanSpinner      = new Spinner<>();
    private final Spinner<Integer> rowSpanSpinner      = new Spinner<>();
    private final ComboBox<TextPosition> textPosCombo  = new ComboBox<>();
    private final ComboBox<IconType> iconTypeCombo     = new ComboBox<>();
    private final Button iconBrowseButton              = new Button("(none)");
    private String currentIconValue                    = "";
    private final ComboBox<IconPosition> iconPosCombo  = new ComboBox<>();
    private final ComboBox<BackgroundType> bgTypeCombo = new ComboBox<>();
    private final TextField bgValueField               = new TextField();
    private final Button bgBrowseButton                = new Button("Browse\u2026");
    private final Popup bgColorPopup                   = new Popup();
    private final ColorPicker bgColorPicker            = new ColorPicker();
    private boolean updatingColor                      = false;
    private final ComboBox<ActionType> actionTypeCombo = new ComboBox<>();
    private final TextField actionValueField           = new TextField();
    private final Button actionBrowseButton            = new Button("Browse\u2026");

    public TilePropertiesPanel() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().addAll("properties-panel", "dark-panel");

        colSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 0));
        rowSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 99, 0));
        colSpanSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        rowSpanSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        colSpinner.setEditable(true);
        rowSpinner.setEditable(true);
        colSpanSpinner.setEditable(true);
        rowSpanSpinner.setEditable(true);

        textPosCombo.getItems().setAll(TextPosition.values());
        iconTypeCombo.getItems().setAll(IconType.values());
        iconPosCombo.getItems().setAll(IconPosition.values());
        bgTypeCombo.getItems().setAll(BackgroundType.values());
        actionTypeCombo.getItems().setAll(ActionType.values());

        bgValueField.setPromptText("e.g. #1a2b3c or /path/to/bg.jpg");
        actionValueField.setPromptText("command, URL, or file path");

        iconBrowseButton.setMaxWidth(Double.MAX_VALUE);

        bgColorPopup.setAutoHide(true);
        bgColorPopup.getContent().add(bgColorPicker);

        bgBrowseButton.setMaxWidth(Double.MAX_VALUE);
        bgBrowseButton.setVisible(false);
        bgBrowseButton.setManaged(false);

        actionBrowseButton.setVisible(false);
        actionBrowseButton.setManaged(false);

        HBox actionRow = new HBox(4, actionValueField, actionBrowseButton);
        HBox.setHgrow(actionValueField, Priority.ALWAYS);
        actionRow.setAlignment(Pos.CENTER_LEFT);

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        ColumnConstraints col0 = new ColumnConstraints();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col0, col1);

        int r = 0;
        grid.add(new Label("Title:"), 0, r);        grid.add(titleField, 1, r++);
        grid.add(new Label("Col:"), 0, r);           grid.add(colSpinner, 1, r++);
        grid.add(new Label("Row:"), 0, r);           grid.add(rowSpinner, 1, r++);
        grid.add(new Label("Col Span:"), 0, r);      grid.add(colSpanSpinner, 1, r++);
        grid.add(new Label("Row Span:"), 0, r);      grid.add(rowSpanSpinner, 1, r++);
        grid.add(new Label("Text Position:"), 0, r); grid.add(textPosCombo, 1, r++);
        grid.add(new Label("Icon Type:"), 0, r);     grid.add(iconTypeCombo, 1, r++);
        grid.add(new Label("Icon Value:"), 0, r);    grid.add(iconBrowseButton, 1, r++);
        grid.add(new Label("Icon Position:"), 0, r); grid.add(iconPosCombo, 1, r++);
        HBox bgValueCell = new HBox();
        bgValueCell.getChildren().addAll(bgValueField, bgBrowseButton);
        HBox.setHgrow(bgValueField, Priority.ALWAYS);
        HBox.setHgrow(bgBrowseButton, Priority.ALWAYS);
        bgValueField.setMaxWidth(Double.MAX_VALUE);

        grid.add(new Label("BG Type:"), 0, r);       grid.add(bgTypeCombo, 1, r++);
        grid.add(new Label("BG Value:"), 0, r);      grid.add(bgValueCell, 1, r++);
        grid.add(new Label("Action Type:"), 0, r);   grid.add(actionTypeCombo, 1, r++);
        grid.add(new Label("Action Value:"), 0, r);  grid.add(actionRow, 1, r++);

        Label header = new Label("Tile Properties");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        getChildren().addAll(header, new Separator(), grid);

        setDisable(true);
        registerListeners();
    }

    public void setOwnerStage(Stage stage) {
        this.ownerStage = stage;
    }

    public void setOnChange(Runnable callback) {
        this.onChange = callback;
    }

    public void loadTile(TileConfig tile) {
        loading = true;
        this.currentTile = tile;
        setDisable(false);

        titleField.setText(tile.getTitle() != null ? tile.getTitle() : "");
        colSpinner.getValueFactory().setValue(tile.getCol());
        rowSpinner.getValueFactory().setValue(tile.getRow());
        colSpanSpinner.getValueFactory().setValue(Math.max(1, tile.getColSpan()));
        rowSpanSpinner.getValueFactory().setValue(Math.max(1, tile.getRowSpan()));
        textPosCombo.setValue(tile.getTextPosition() != null
                ? tile.getTextPosition() : TextPosition.UNDER_ICON);

        if (tile.getIcon() != null) {
            iconTypeCombo.setValue(tile.getIcon().getType() != null
                    ? tile.getIcon().getType() : IconType.MATERIAL);
            currentIconValue = tile.getIcon().getValue() != null ? tile.getIcon().getValue() : "";
            iconBrowseButton.setText(truncate(currentIconValue));
            iconPosCombo.setValue(tile.getIcon().getPosition() != null
                    ? tile.getIcon().getPosition() : IconPosition.CENTER);
        } else {
            iconTypeCombo.setValue(IconType.MATERIAL);
            currentIconValue = "";
            iconBrowseButton.setText("(none)");
            iconPosCombo.setValue(IconPosition.CENTER);
        }

        if (tile.getBackground() != null) {
            bgTypeCombo.setValue(tile.getBackground().getType() != null
                    ? tile.getBackground().getType() : BackgroundType.COLOR);
            bgValueField.setText(tile.getBackground().getValue() != null
                    ? tile.getBackground().getValue() : "#16202e");
        } else {
            bgTypeCombo.setValue(BackgroundType.COLOR);
            bgValueField.setText("#16202e");
        }

        if (tile.getAction() != null) {
            actionTypeCombo.setValue(tile.getAction().getType() != null
                    ? tile.getAction().getType() : ActionType.COMMAND);
            actionValueField.setText(tile.getAction().getValue() != null
                    ? tile.getAction().getValue() : "");
        } else {
            actionTypeCombo.setValue(ActionType.COMMAND);
            actionValueField.setText("");
        }

        updateActionBrowseVisibility(actionTypeCombo.getValue());
        updateBgTypeControls(bgTypeCombo.getValue());
        loading = false;
    }

    public void clear() {
        this.currentTile = null;
        setDisable(true);
        titleField.clear();
        currentIconValue = "";
        iconBrowseButton.setText("(none)");
        bgValueField.clear();
        actionValueField.clear();
        bgColorPopup.hide();
        updateBgTypeControls(BackgroundType.COLOR);
    }

    public TileConfig getCurrentTile() {
        return currentTile;
    }

    private void registerListeners() {
        titleField.textProperty().addListener((obs, o, n) -> {
            if (currentTile != null) { currentTile.setTitle(n); notifyChange(); }
        });
        colSpinner.valueProperty().addListener((obs, o, n) -> {
            if (currentTile != null && n != null) { currentTile.setCol(n); notifyChange(); }
        });
        rowSpinner.valueProperty().addListener((obs, o, n) -> {
            if (currentTile != null && n != null) { currentTile.setRow(n); notifyChange(); }
        });
        colSpanSpinner.valueProperty().addListener((obs, o, n) -> {
            if (currentTile != null && n != null) { currentTile.setColSpan(n); notifyChange(); }
        });
        rowSpanSpinner.valueProperty().addListener((obs, o, n) -> {
            if (currentTile != null && n != null) { currentTile.setRowSpan(n); notifyChange(); }
        });
        textPosCombo.valueProperty().addListener((obs, o, n) -> {
            if (currentTile != null && n != null) { currentTile.setTextPosition(n); notifyChange(); }
        });
        iconTypeCombo.valueProperty().addListener((obs, o, n) -> {
            if (currentTile != null && n != null) {
                ensureIcon();
                currentTile.getIcon().setType(n);
                currentIconValue = "";
                currentTile.getIcon().setValue("");
                iconBrowseButton.setText("(none)");
                notifyChange();
            }
        });
        iconPosCombo.valueProperty().addListener((obs, o, n) -> {
            if (currentTile != null && n != null) { ensureIcon(); currentTile.getIcon().setPosition(n); notifyChange(); }
        });
        bgTypeCombo.valueProperty().addListener((obs, o, n) -> {
            if (currentTile != null && n != null) { ensureBg(); currentTile.getBackground().setType(n); notifyChange(); }
            updateBgTypeControls(n);
        });
        bgValueField.textProperty().addListener((obs, o, n) -> {
            if (currentTile != null) { ensureBg(); currentTile.getBackground().setValue(n); notifyChange(); }
            if (!updatingColor && bgColorPopup.isShowing()) {
                updatingColor = true;
                try { bgColorPicker.setValue(Color.web(n)); } catch (Exception ex) { /* invalid hex, skip */ }
                updatingColor = false;
            }
        });
        bgColorPicker.valueProperty().addListener((obs, o, n) -> {
            if (!updatingColor && n != null) {
                updatingColor = true;
                bgValueField.setText(colorToHex(n));
                updatingColor = false;
            }
        });
        bgValueField.focusedProperty().addListener((obs, o, focused) -> {
            if (focused && bgTypeCombo.getValue() == BackgroundType.COLOR) {
                try { bgColorPicker.setValue(Color.web(bgValueField.getText())); }
                catch (Exception ex) { bgColorPicker.setValue(Color.web("#16202e")); }
                Bounds bounds = bgValueField.localToScreen(bgValueField.getBoundsInLocal());
                if (bounds != null) {
                    bgColorPopup.show(bgValueField, bounds.getMinX(), bounds.getMaxY() + 2);
                }
            }
        });
        actionTypeCombo.valueProperty().addListener((obs, o, n) -> {
            if (currentTile != null && n != null) { ensureAction(); currentTile.getAction().setType(n); notifyChange(); }
            updateActionBrowseVisibility(n);
        });
        actionValueField.textProperty().addListener((obs, o, n) -> {
            if (currentTile != null) {
                ensureAction();
                if (currentTile.getAction().getType() == null) {
                    currentTile.getAction().setType(actionTypeCombo.getValue());
                }
                currentTile.getAction().setValue(n);
                notifyChange();
            }
        });

        iconBrowseButton.setOnAction(e -> {
            if (ownerStage == null) return;
            IconType type = iconTypeCombo.getValue();
            if (type == IconType.MATERIAL) {
                IconPickerDialog dialog = new IconPickerDialog(ownerStage);
                dialog.showAndWait().ifPresent(name -> {
                    currentIconValue = name;
                    iconBrowseButton.setText(truncate(name));
                    if (currentTile != null) { ensureIcon(); currentTile.getIcon().setValue(name); notifyChange(); }
                });
            } else {
                FileChooser chooser = new FileChooser();
                chooser.setTitle("Select Icon Image");
                chooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.svg", "*.gif"));
                File file = chooser.showOpenDialog(ownerStage);
                if (file != null) {
                    String path = file.getAbsolutePath();
                    currentIconValue = path;
                    iconBrowseButton.setText(truncate(path));
                    if (currentTile != null) { ensureIcon(); currentTile.getIcon().setValue(path); notifyChange(); }
                }
            }
        });

        actionBrowseButton.setOnAction(e -> {
            if (ownerStage == null) return;
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Application");
            File file = chooser.showOpenDialog(ownerStage);
            if (file != null) {
                actionValueField.setText(file.getAbsolutePath());
            }
        });

        bgBrowseButton.setOnAction(e -> {
            if (ownerStage == null) return;
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Background Image");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
            File file = chooser.showOpenDialog(ownerStage);
            if (file != null) {
                String path = file.getAbsolutePath();
                bgValueField.setText(path);
                if (currentTile != null) { ensureBg(); currentTile.getBackground().setValue(path); notifyChange(); }
            }
        });
    }

    private void updateActionBrowseVisibility(ActionType type) {
        boolean show = type == ActionType.APP;
        actionBrowseButton.setVisible(show);
        actionBrowseButton.setManaged(show);
    }

    private void updateBgTypeControls(BackgroundType type) {
        boolean isImage = type == BackgroundType.IMAGE;
        bgValueField.setVisible(!isImage);
        bgValueField.setManaged(!isImage);
        bgBrowseButton.setVisible(isImage);
        bgBrowseButton.setManaged(isImage);
        if (isImage && bgColorPopup.isShowing()) {
            bgColorPopup.hide();
        }
    }

    private String truncate(String s) {
        if (s == null || s.isEmpty()) return "(none)";
        return s.length() > 20 ? s.substring(0, 17) + "..." : s;
    }

    private String colorToHex(Color c) {
        return String.format("#%02x%02x%02x",
                (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue() * 255));
    }

    private void ensureIcon() {
        if (currentTile.getIcon() == null) currentTile.setIcon(new IconConfig());
    }

    private void ensureBg() {
        if (currentTile.getBackground() == null) currentTile.setBackground(new BackgroundConfig());
    }

    private void ensureAction() {
        if (currentTile.getAction() == null) currentTile.setAction(new ActionConfig());
    }

    private void notifyChange() {
        if (!loading && onChange != null) onChange.run();
    }
}
