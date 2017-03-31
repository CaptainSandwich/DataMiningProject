package Controllers;

import Models.KPoint;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by greg on 3/30/2017.
 */
public class BayesController {
    @FXML
    protected GridPane bayesTab;

    @FXML
    protected ComboBox<String> comboBox;
   // @FXML
   // protected TextField k;

    @FXML
    protected Label schemaTip;

    @FXML
    protected TextField inputQuery;

    @FXML
    protected Label classTip;

    @FXML
    protected Button runButton;

    @FXML
    protected Label fileName;

    @FXML
    protected TextArea output;

    protected File file;


    // stuff that needs to be moved to the model
    String tableHeaders[];
    String tableClasses[][];
    int[][] tableData;


    @FXML protected void validateInputQuery(KeyEvent event) {
        String inputQueryText = inputQuery.getText();
        // do this later: a regex that matches digits followed by commas/whitespace or a question mark followed by the same
/*        if(inputQueryText.matches("\\d+,") && Integer.parseInt(inputQueryText) > 0) {
            runButton.setDisable(false);
        } else {
            runButton.setDisable(true);
        }
        */
    }


    protected void getDataFromFile() {
        FileReader reader;
        List<CSVRecord> records = null;
        //Iterable<CSVRecord> records = null;
        try {
            reader = new FileReader(this.file);
            //records = CSVFormat.
          //  recrords = CSVFormat.DEFAULT.parser
            records = CSVFormat.DEFAULT.parse(reader).getRecords();
            //records = CSVParser.par
        } catch(Exception e) {

        }


        // Set headers
        int columnCount = records.get(0).size();
        String headers[] = new String[columnCount];
        for(int i = 0; i < columnCount; i++){
            headers[i] = records.get(0).get(i).toString();
        }
        this.tableHeaders = headers;

        for(String header : headers){
         //   System.out.println(header + " ");
        }

        // Set classes

        // Max class length is our second parameter to the array
        int classCount = 0;
        for(int i = 0; i < columnCount; i++) {
            // find a better regex
            //String trimmedRecord = records.get(1).get(i).trim();
            //trimmedRecord = trimmedRecord.substring(1, trimmedRecord.length() - 1);

            //System.out.println("trimmedRecord: " + trimmedRecord);
            String[] temp = records.get(1).get(i).split("\\$");
            /*for(int j = 0; j < temp.length; j++){
                System.out.println(temp[j]);
            } */
            //System.out.println(trimmedRecord + "Length: " + classCount);
            if(temp.length > classCount){
                classCount = temp.length;
            }

            // debug
            //System.out.println(records.get(1).get(i));
        }

        String[][] classes = new String[columnCount][classCount];

        // LUL more duplicate code
        for(int i = 0; i < columnCount; i++) {
            // find a better regex
            String trimmedRecord = records.get(1).get(i).trim();
            trimmedRecord = trimmedRecord.substring(1, trimmedRecord.length() - 1);

            //System.out.println("trimmedRecord: " + trimmedRecord);
            String[] temp = trimmedRecord.split("\\$");
            for(int j = 0; j < temp.length; j++){
                classes[i][j] = temp[j];

                // debug
             //   System.out.println(classes[i][j]);
            }
        }

        this.tableClasses = classes;

        // set data
        this.tableData = new int[records.size() - 2][this.tableHeaders.length];
        ArrayList<Integer> data = new ArrayList<Integer>();
        for(int i = 2; i < records.size(); i++) {
            //ArrayList<Double> data = new ArrayList<Double>();

            //System.out.print("Record " + i + ": ");
            for(int j = 0; j < this.tableHeaders.length; j++){
                this.tableData[i - 2][j] = Integer.parseInt(records.get(i).get(j));
               // System.out.print(this.tableData[i - 2][j] + " ");
            }
            //System.out.println();
        }
    }

    @FXML protected void getFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        this.file = fileChooser.showOpenDialog(bayesTab.getScene().getWindow());
        fileName.setText(this.file.getName());
        getDataFromFile();

        String schemaHelper = "";
        // Todo table headers should be moved to the model!
        for(String header: tableHeaders){
           schemaHelper += header + " ";
        }
        schemaTip.setText(schemaHelper);

        String classHelper = "";
        for(int i = 0; i < tableHeaders.length; i++){
          classHelper += "{";

            for(int j = 0; j < tableClasses[i].length; j++){
                if(tableClasses[i][j] == null || tableClasses[i][j].isEmpty()){
                    break;
                }

                if(j == tableClasses[i].length -1 || tableClasses[i][j+1] == null || tableClasses[i][j+1].isEmpty()){
                   classHelper += tableClasses[i][j];
                }
                else {
                    classHelper += tableClasses[i][j] + ", ";
                }
            }

            if(i == tableHeaders.length - 1){
                classHelper += "}";
            }
            else {
                classHelper += "}, ";
            }
        }
        classTip.setText(classHelper);
    }


    @FXML protected void run(ActionEvent event) {

        // Parse input query
        String inputQuery = this.inputQuery.getText().trim();
        String[] inputs = inputQuery.split(",");

        for(String s: inputs) { System.out.println(s); }

        if(inputs.length != this.tableHeaders.length){
            output.setText("Your input data was wiggity wack - check it");
            return;
        }

        int headerIndexToSolveFor = -1;
        String solveForDelim = "?";

        int[] tupleToAdd = new int[this.tableHeaders.length];
        for(int i = 0; i < inputs.length; i++) {
            if(inputs[i].indexOf('?') != -1){
                headerIndexToSolveFor = i;
                tupleToAdd[i] = -1;
            }
            else {
                inputs[i] = inputs[i].trim();
                tupleToAdd[i] = Integer.parseInt(inputs[i]);
            }
        }

        String outputText = "";
        double predictions[] = Algorithms.NaiveBayesClassifyNewTuple(this.tableData, tupleToAdd, this.tableHeaders.length, this.tableClasses, headerIndexToSolveFor);
        int prediction = 0;
        double max = Double.MIN_VALUE;
        for(int i = 0; i < predictions.length; i++){
            if(predictions[i] > max){
                max = predictions[i];
                prediction = i;
            }

            outputText += "prob[" + i +"]: " + predictions[i] + "\n";
            // System.out.println("prob[" + i +"]: " + predictions[i]);
        }

        outputText += "New tuple predicted to be of class " + prediction;

        output.setText(outputText);
    }

}
