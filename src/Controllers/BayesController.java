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
import java.util.*;

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
    protected Label validation;

    @FXML
    protected TextField inputQuery;

    @FXML
    protected Label classTip;

    @FXML
    protected Button runButton;

    @FXML
    protected Button validateButton;

    @FXML
    protected Label fileName;

    @FXML
    protected TextArea output;

    @FXML
    protected TextField kfolds;

    protected File file;




    // stuff that needs to be moved to the model
    String tableHeaders[];
    String tableClasses[][];
    int [][] tableData;
    List<Map<String, Integer>> classMap;


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
        classMap = new ArrayList<Map<String, Integer>>();

        for(int i = 0; i < this.tableClasses.length; i++) {
            classMap.add(i, new HashMap<String, Integer>());
            for(int j = 0; j < this.tableClasses[i].length; j++) {
                classMap.get(i).put(this.tableClasses[i][j], j);
            }
        }

        // set data
        this.tableData = new int[records.size() - 2][this.tableHeaders.length];
        ArrayList<Integer> data = new ArrayList<Integer>();
        for(int i = 2; i < records.size(); i++) {
            //ArrayList<Double> data = new ArrayList<Double>();

            //System.out.print("Record " + i + ": ");
            for(int j = 0; j < this.tableHeaders.length; j++){
                this.tableData[i - 2][j] = classMap.get(j).get(records.get(i).get(j));
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
        /*schemaTip.setText(schemaHelper);*/

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
        /*classTip.setText(classHelper);*/
    }

    @FXML protected void crossValidate(ActionEvent event) {
        int hits = 0;
        int hitAvg = 0;
        int k = Integer.parseInt(this.kfolds.getText());
        int chunkSize = this.tableData.length/k;
        for(int i = 0; i < this.tableData.length; i += chunkSize) {
            int [][] trainingData = new int[this.tableData.length - chunkSize][tableData[i].length];

            for(int index = 0; index < this.tableData.length - chunkSize; index++){
                if(index < i || index > i + chunkSize) {
                    trainingData[index] = tableData[index].clone();
                }
            }

            for(int j = i; j < i + chunkSize && j < this.tableData.length; j++) {
                int[] tupleToAdd = tableData[j].clone();

                for(int index = 0; index < this.tableData[i].length; index++) {
                    tupleToAdd[index] = -1;

                    double predictions[] = Algorithms.NaiveBayesClassifyNewTuple(this.tableData, tupleToAdd, this.tableHeaders.length, this.tableClasses, index);
                    int prediction = 0;
                    double max = Double.MIN_VALUE;
                    for(int i2 = 0; i2 < predictions.length; i2++){
                        if(predictions[i2] > max) {
                            max = predictions[i2];
                            prediction = i2;
                        }
                    }

                    if(prediction == tableData[j][index]){
                        hits++;
                    }
                }
            }
        }
        double error = (double) hits/(tableData.length*tableData[0].length);
        validation.setText(String.valueOf(error));
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
                tupleToAdd[i] = classMap.get(i).get(inputs[i]);
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

            outputText += "P(" + tableHeaders[headerIndexToSolveFor] + "=" + tableClasses[headerIndexToSolveFor][i] + ") = " + predictions[i] + "\n";
            // System.out.println("prob[" + i +"]: " + predictions[i]);
        }

        outputText += "Prediction: " + tableHeaders[headerIndexToSolveFor] + "=" + tableClasses[headerIndexToSolveFor][prediction];

        output.setText(outputText);
    }

}
