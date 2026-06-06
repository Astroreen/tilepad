package me.astroreen.tilepad.ui.editor;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import me.astroreen.tilepad.ui.FontLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IconPickerDialog {

    private final Stage dialog;
    private String selectedName = null;

    public IconPickerDialog(Stage owner) {
        dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Select Icon");
        dialog.setMinWidth(600);
        dialog.setMinHeight(500);

        List<String> allNames = new ArrayList<>(FontLoader.getIconNames());
        allNames.sort(String::compareTo);

        FlowPane flowPane = new FlowPane();
        flowPane.setHgap(4);
        flowPane.setVgap(4);
        flowPane.setPadding(new Insets(8));
        flowPane.setStyle("-fx-background-color: #091421;");

        allNames.forEach(name -> flowPane.getChildren().add(createIconCell(name)));

        ScrollPane scroll = new ScrollPane(flowPane);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: #091421; -fx-background: #091421;");

        TextField searchField = new TextField();
        searchField.setPromptText("Search icons\u2026");
        searchField.setStyle(
                "-fx-background-color: #16202e; -fx-text-fill: #d9e3f6; " +
                "-fx-prompt-text-fill: #c2c6d6; -fx-border-color: #424754; " +
                "-fx-border-radius: 4px; -fx-background-radius: 4px;");

        searchField.textProperty().addListener((obs, o, n) -> {
            String filter = n.toLowerCase();
            flowPane.getChildren().clear();
            allNames.stream()
                    .filter(nm -> nm.contains(filter))
                    .forEach(nm -> flowPane.getChildren().add(createIconCell(nm)));
        });

        VBox root = new VBox(8, searchField, scroll);
        root.setPadding(new Insets(12));
        root.setStyle("-fx-background-color: #091421;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        Scene scene = new Scene(root, 620, 520);
        var cssUrl = IconPickerDialog.class.getResource("/me/astroreen/tilepad/styles/main.css");
        if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

        dialog.setScene(scene);
    }

    public Optional<String> showAndWait() {
        dialog.showAndWait();
        return Optional.ofNullable(selectedName);
    }

    private VBox createIconCell(String name) {
        String ch = FontLoader.codeToChar(FontLoader.getIconCodepoint(name));

        Label iconLabel = new Label(ch);
        iconLabel.setStyle(
                "-fx-font-family: 'Material Icons Outlined'; " +
                "-fx-font-size: 28px; " +
                "-fx-text-fill: #d9e3f6;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 9px; -fx-text-fill: #c2c6d6;");
        nameLabel.setMaxWidth(72);
        nameLabel.setWrapText(false);

        String baseStyle  = "-fx-background-color: #16202e; -fx-background-radius: 4px; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #212b39; -fx-background-radius: 4px; -fx-cursor: hand;";

        VBox cell = new VBox(4, iconLabel, nameLabel);
        cell.setAlignment(Pos.CENTER);
        cell.setPadding(new Insets(6));
        cell.setPrefWidth(80);
        cell.setStyle(baseStyle);

        cell.setOnMouseEntered(e -> cell.setStyle(hoverStyle));
        cell.setOnMouseExited(e -> cell.setStyle(baseStyle));
        cell.setOnMouseClicked(e -> {
            selectedName = name;
            dialog.close();
        });

        return cell;
    }
}
