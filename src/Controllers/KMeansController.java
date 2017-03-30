package Controllers;

import Models.Cluster;
import Models.KPoint;
import Controllers.Algorithms;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.*;
import javafx.util.converter.IntegerStringConverter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class KMeansController {
    @FXML
    protected GridPane kMeansTab;

    @FXML
    protected TextField k;

    @FXML
    protected TextField iterations;

    @FXML
    protected Button runButton;

    @FXML
    protected Label fileName;

    @FXML
    protected TextArea output;

    protected File file;

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
        this.file = fileChooser.showOpenDialog(kMeansTab.getScene().getWindow());
        fileName.setText(this.file.getName());
    }

    @FXML protected void download(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        this.file = fileChooser.showSaveDialog(kMeansTab.getScene().getWindow());
        try {
            FileWriter fileWriter = null;

            fileWriter = new FileWriter(file);
            fileWriter.write(output.toString());
            fileWriter.close();
        } catch (Exception e) {

        }
    }

    @FXML protected void run(ActionEvent event) {

        ArrayList<KPoint> dataTable = new ArrayList<KPoint>();
        FileReader reader;
        Iterable<CSVRecord> records = null;
        try {
            reader = new FileReader(this.file);
            records = CSVFormat.DEFAULT.parse(reader);
        } catch(Exception e) {

        }

        for(CSVRecord record : records) {
            ArrayList<Double> data = new ArrayList<Double>();
            for(String col : record) {
                data.add(Double.valueOf(col));
            }
            dataTable.add(new KPoint(data));
        }

        ArrayList<Cluster> clusters = Algorithms.KMeans(dataTable, Integer.valueOf(k.getText()), Integer.valueOf(iterations.getText()));
        String outputString = "";
        for(Cluster c : clusters) {
            outputString += c.toString();
        }

        this.output.setText(outputString);
    }
}
