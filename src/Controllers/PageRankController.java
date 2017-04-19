package Controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.awt.*;
import javafx.event.ActionEvent;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

import Controllers.Algorithms;

import static Controllers.Algorithms.pageRank;

/**
 * Created by greg on 4/18/2017.
 */
public class PageRankController {
    @FXML
    protected GridPane pageRankTab;

    @FXML
    protected TextField dampeningFactor;

    @FXML
    protected Label fileName;

    @FXML
    protected Button runButton;

    @FXML
    protected TextArea output;

    protected File file;

    int pageAdjacencyMatrix[][];
    // dampening factor D
    double d;

    @FXML protected void validateDampeningFactor(KeyEvent event) {
        String dFactor = dampeningFactor.getText();
        if(dFactor.matches("\\d+\\.\\d+") && Double.parseDouble(dFactor) >= 0.0 && Double.parseDouble(dFactor) <= 1.0) {
            runButton.setDisable(false);
            this.d = Double.parseDouble((dFactor));
        } else {
            runButton.setDisable(true);
        }
    }

    protected void getDataFromFile() {
        FileReader reader;
        java.util.List<CSVRecord> records = null;
        //Iterable<CSVRecord> records = null;
        try {
            reader = new FileReader(this.file);
            //records = CSVFormat.
            //  recrords = CSVFormat.DEFAULT.parser
            records = CSVFormat.DEFAULT.parse(reader).getRecords();
            //records = CSVParser.par
        } catch (Exception e) {
            System.out.println("Error reading .csv file");
        }

        // set data

        int columnCount = records.get(0).size();
        // todo use hashmap key: html file, value: rank of this page in relation to others, which is updated during PageRank algo
        //this.htmlFilesToRank = new String[records.size()];
        this.pageAdjacencyMatrix = new int[records.size()][columnCount];
        for(int i = 0; i < records.size(); i++){

            for(int j = 0; j < columnCount; j++){
               this.pageAdjacencyMatrix[i][j] = Integer.parseInt(records.get(i).get(j).trim());
               System.out.print(pageAdjacencyMatrix[i][j] + " ");
            }
            System.out.println();
            System.out.println(pageAdjacencyMatrix.length + " " + pageAdjacencyMatrix[0].length);
            //this.htmlFilesToRank[i] = records.get(i).get(0);
        }
    }


    @FXML protected void getFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        this.file = fileChooser.showOpenDialog(pageRankTab.getScene().getWindow());
        if(this.file != null) {
            fileName.setText(this.file.getName());
        }

        getDataFromFile();
    }



    @FXML protected void run(ActionEvent event) {
        // Do some calculations here
        // Though we should probably set up a model for this bad boy

        /*
        try {
            Jsoup.connect("http://google.com").get().html();
        } catch (IOException e) {
            System.out.println("Couldn't connect to one of the websites!");
            e.printStackTrace();
        } */

        double[] pagerank = pageRank(pageAdjacencyMatrix, this.d);

        String outputText = "";



        // Pretty obvious to see what the highest ranked page is, but you can change this if you want Richard
        for(int i = 0; i < pagerank.length; i++){
            outputText += "Row " + (i+1) + " has pagerank " + pagerank[i] + "\n";
        }



        output.setText(outputText);
    }

}
