package Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import javafx.util.converter.IntegerStringConverter;

import javax.swing.*;
import java.io.File;

public class KMeansController {
    @FXML
    protected GridPane kMeansTab;

    @FXML
    protected TextField k;

    @FXML
    protected TextField iterations;

    @FXML
    protected Button runButton;

    @FXML protected void validateK(KeyEvent event) {
        String kText = k.getText();
        if(kText.matches("\\d+") && Integer.parseInt(kText) > 0) {
            runButton.setDisable(false);
        } else {
            runButton.setDisable(true);
        }
    }

    @FXML protected void validateIterations(KeyEvent event) {
        String kText = k.getText();
        if(kText.matches("\\d+") && Integer.parseInt(kText) > 0) {
            runButton.setDisable(false);
        } else {
            runButton.setDisable(true);
        }
    }

    @FXML protected void getFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(kMeansTab.getScene().getWindow());
    }

    @FXML protected void run(ActionEvent event) {
    }
}
