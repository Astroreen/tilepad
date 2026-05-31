package me.astroreen.tilepad.ui.editor;

import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import me.astroreen.tilepad.model.ActionConfig;
import me.astroreen.tilepad.model.ActionType;
import me.astroreen.tilepad.model.BackgroundConfig;
import me.astroreen.tilepad.model.BackgroundType;
import me.astroreen.tilepad.model.IconConfig;
import me.astroreen.tilepad.model.IconPosition;
import me.astroreen.tilepad.model.IconType;
import me.astroreen.tilepad.model.TextPosition;
import me.astroreen.tilepad.model.TileConfig;

public class TilePropertiesPanel extends VBox {

    private TileConfig currentTile;
    private Runnable onChange;

    private final TextField titleField               = new TextField();
    private final Spinner<Integer> colSpinner        = new Spinner<>();
    private final Spinner<Integer> rowSpinner        = new Spinner<>();
    private final Spinner<Integer> colSpanSpinner    = new Spinner<>();
    private final Spinner<Integer> rowSpanSpinner    = new Spinner<>();
    private final ComboBox<TextPosition> textPosCombo = new ComboBox<>();
    private final ComboBox<IconType> iconTypeCombo   = new ComboBox<>();
    private final TextField iconValueField           = new TextField();
    private final ComboBox<IconPosition> iconPosCombo = new ComboBox<>();
    private final ComboBox<BackgroundType> bgTypeCombo = new ComboBox<>();
    private final TextField bgValueField             = new TextField();
    private final ComboBox<ActionType> actionTypeCombo = new ComboBox<>();
    private final TextField actionValueField         = new TextField();

    public TilePropertiesPanel() {
        setSpacing(8);
        setPadding(new Insets(12));
        getStyleClass().add("properties-panel");

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

        iconValueField.setPromptText("e.g. home or /path/to/icon.png");
        bgValueField.setPromptText("e.g. #1a2b3c or /path/to/bg.jpg");
        actionValueField.setPromptText("command, URL, or file path");

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);

        int r = 0;
        grid.add(new Label("Title:"), 0, r);        grid.add(titleField, 1, r++);
        grid.add(new Label("Col:"), 0, r);           grid.add(colSpinner, 1, r++);
        grid.add(new Label("Row:"), 0, r);           grid.add(rowSpinner, 1, r++);
        grid.add(new Label("Col Span:"), 0, r);      grid.add(colSpanSpinner, 1, r++);
        grid.add(new Label("Row Span:"), 0, r);      grid.add(rowSpanSpinner, 1, r++);
        grid.add(new Label("Text Position:"), 0, r); grid.add(textPosCombo, 1, r++);
        grid.add(new Label("Icon Type:"), 0, r);     grid.add(iconTypeCombo, 1, r++);
        grid.add(new Label("Icon Value:"), 0, r);    grid.add(iconValueField, 1, r++);
        grid.add(new Label("Icon Position:"), 0, r); grid.add(iconPosCombo, 1, r++);
        grid.add(new Label("BG Type:"), 0, r);       grid.add(bgTypeCombo, 1, r++);
        grid.add(new Label("BG Value:"), 0, r);      grid.add(bgValueField, 1, r++);
        grid.add(new Label("Action Type:"), 0, r);   grid.add(actionTypeCombo, 1, r++);
        grid.add(new Label("Action Value:"), 0, r);  grid.add(actionValueField, 1, r++);

        Label header = new Label("Tile Properties");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        getChildren().addAll(header, new Separator(), grid);

        setDisable(true);
        registerListeners();
    }

    public void setOnChange(Runnable callback) {
        this.onChange = callback;
    }

    public void loadTile(TileConfig tile) {
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
            iconValueField.setText(tile.getIcon().getValue() != null ? tile.getIcon().getValue() : "");
            iconPosCombo.setValue(tile.getIcon().getPosition() != null
                    ? tile.getIcon().getPosition() : IconPosition.CENTER);
        } else {
            iconTypeCombo.setValue(IconType.MATERIAL);
            iconValueField.setText("");
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
    }

    public void clear() {
        this.currentTile = null;
        setDisable(true);
        titleField.clear();
        iconValueField.clear();
        bgValueField.clear();
        actionValueField.clear();
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
            if (currentTile != null && n != null) { ensureIcon(); currentTile.getIcon().setType(n); notifyChange(); }
        });
        iconValueField.textProperty().addListener((obs, o, n) -> {
            if (currentTile != null) { ensureIcon(); currentTile.getIcon().setValue(n); notifyChange(); }
        });
        iconPosCombo.valueProperty().addListener((obs, o, n) -> {
            if (currentTile != null && n != null) { ensureIcon(); currentTile.getIcon().setPosition(n); notifyChange(); }
        });
        bgTypeCombo.valueProperty().addListener((obs, o, n) -> {
            if (currentTile != null && n != null) { ensureBg(); currentTile.getBackground().setType(n); notifyChange(); }
        });
        bgValueField.textProperty().addListener((obs, o, n) -> {
            if (currentTile != null) { ensureBg(); currentTile.getBackground().setValue(n); notifyChange(); }
        });
        actionTypeCombo.valueProperty().addListener((obs, o, n) -> {
            if (currentTile != null && n != null) { ensureAction(); currentTile.getAction().setType(n); notifyChange(); }
        });
        actionValueField.textProperty().addListener((obs, o, n) -> {
            if (currentTile != null) { ensureAction(); currentTile.getAction().setValue(n); notifyChange(); }
        });
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
        if (onChange != null) onChange.run();
    }
}
