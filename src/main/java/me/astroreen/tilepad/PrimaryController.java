package me.astroreen.tilepad;

import java.io.IOException;
import javafx.fxml.FXML;

public class PrimaryController {

    @FXML
    private void switchToSecondary() throws IOException {
        Tilepad.setRoot("secondary");
    }
}
