package ovh.asetniew.controller;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.TextFormatter.Change;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;
import ovh.asetniew.ba.BASimulation;
import ovh.asetniew.ba.BASimulationNonLinearUsingLists;
import ovh.asetniew.ba.BASimulationUsingMatrix;
import ovh.asetniew.ba.BASimulationUsingLists;
import ovh.asetniew.ba.nodes.LogarithmicNumberAxis;

import javax.xml.soap.Text;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class Controller {
    BASimulationUsingLists ba;
    BASimulationUsingMatrix baMatrix;
    private int theMode = 0;
    /*
        0 -> BAListLinear
        1 -> BAList
        2 -> BAMatrix
     */
    private int m = 1;
    private int m0 = 5;

    @FXML
    Button start;
    @FXML
    Button clear;
    @FXML
    TextField textGamma;

    @FXML
    ChoiceBox mode;
    @FXML
    TextField textM;
    @FXML
    TextField steps;
    @FXML
    TextField textM0;
    private int maxSteps;
    Thread t1;
    ScatterChart<Number, Number> scatterPlot;
    ScatterChart<Number, Number> scatterPlot2;
    @FXML
    private Pane paneView;
    @FXML
    private Pane paneView2;

    private double gamma;
    public Controller() {

    }

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Canvas canvas;

    public void initialize() {
        loadData();

        UnaryOperator<Change> integerFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("([1-9][0-9]*)?")) {
                return change;
            }
            return null;
        };



        UnaryOperator<Change> doubleFilter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("(([1-9][0-9]*)|0)?(\\.[0-9]*)?")) {
                return change;
            }
            return null;
        };

        StringConverter<Double> converter = new StringConverter<Double>() {

            @Override
            public Double fromString(String s) {
                if (s.isEmpty() || "-".equals(s) || ".".equals(s) || "-.".equals(s) || s.contains("-")) {
                    return 0.0 ;
                } else {
                    return Double.valueOf(s);
                }
            }


            @Override
            public String toString(Double d) {
                return d.toString();
            }
        };

        textM.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
        steps.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));
        textM0.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));

        TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, doubleFilter);

        textGamma.setTextFormatter(textFormatter);


        mode.getItems().add("BAListNonLinear");
        mode.getItems().add("BAList");
        mode.getItems().add("BAMatrix");
        mode.setValue(mode.getItems().get(0));

        steps.setText("1000");
        maxSteps = 1000;
        textM0.setText("4");
        m0 = 4;
        textM.setText("1");
        m = 1;
        textGamma.setText("1");
        gamma = 1;

    }

    public void changeMode() throws Exception{
        ObservableList<String> modes = mode.getItems();

        if(modes.contains(mode.getValue())){
            theMode = modes.indexOf(mode.getValue());
        }else{
            throw new Exception("Selected value that does not exist!");
        }
    }

    private void loadData() {
        paneView.getChildren().clear();
        ValueAxis verticalAxis = new LogarithmicNumberAxis();
        verticalAxis.setLabel("P(k)");
        ValueAxis horizontalAxis = new LogarithmicNumberAxis();

        horizontalAxis.setLabel("k");
        scatterPlot = new ScatterChart<Number, Number>(verticalAxis, horizontalAxis);
        scatterPlot.setTitle("Rozkład P(k) w skali log-log");


        paneView.getChildren().add(scatterPlot);

        ValueAxis verticalAxis2 = new NumberAxis();
        verticalAxis2.setLabel("P(k)");
        ValueAxis horizontalAxis2 = new LogarithmicNumberAxis();

        horizontalAxis2.setLabel("k");

        scatterPlot2 = new ScatterChart<Number, Number>(verticalAxis2, horizontalAxis2);

        scatterPlot2.setTitle("Rozkład P(k) w skali none-log");


        paneView2.getChildren().add(scatterPlot2);
    }

    public void adjustM() {
        this.m = Integer.valueOf(textM.getText().toString());
    }

    public void adjustM_0() {
        this.m0 = Integer.valueOf(textM0.getText().toString());

    }
    public void adjustSteps() {
        this.maxSteps = Integer.valueOf(steps.getText().toString());

    }
    public void adjustGamma() {
        this.gamma = Double.valueOf(textGamma.getText().toString());

    }


    public void onExit() {
        Platform.exit();
    }
    public void onClear() {
        scatterPlot.getData().clear();
        scatterPlot2.getData().clear();
    }
    public void onStart() {
        double progress = progressBar.progressProperty().get();

        if((ba != null && ba.stateProperty().getValue() == Worker.State.RUNNING || (baMatrix != null && baMatrix.stateProperty().getValue() == Worker.State.RUNNING))){
            return;
        }

        try {
            // ba = new BASimulationUsingMatrix(5, 1, 1000);
            // bal = new BASimulationUsingLists(5, 1, 1000, scatterPlot);
            System.out.println("M->" + m + " M_0->" + m0 + " gamma->" + gamma);

            if(theMode == 0 ){
                ba = new BASimulationNonLinearUsingLists(m0, m, maxSteps, gamma, scatterPlot,scatterPlot2);
                t1 = new Thread(ba);
                progressBar.progressProperty().bind(ba.progressProperty());
                ba.stateProperty().addListener((argv,oldVal,newVal) -> {

                    if(newVal == Worker.State.RUNNING){
                        Platform.runLater(() -> {
                            start.setDisable(true);
                        });
                    }else if( newVal == Worker.State.FAILED || newVal == Worker.State.SUCCEEDED){
                        start.setDisable(false);
                    }
                });
            }else if(theMode == 1){
                ba = new BASimulationUsingLists(m0, m, maxSteps, scatterPlot,scatterPlot2);
                t1 = new Thread(ba);
                progressBar.progressProperty().bind(ba.progressProperty());
                ba.stateProperty().addListener((argv,oldVal,newVal) -> {

                    if(newVal == Worker.State.RUNNING){
                        Platform.runLater(() -> {
                            start.setDisable(true);
                        });
                    }else if( newVal == Worker.State.FAILED || newVal == Worker.State.SUCCEEDED){
                        start.setDisable(false);
                    }
                });
            }else{
                baMatrix = new BASimulationUsingMatrix(m0, m, maxSteps);
                t1 = new Thread(baMatrix);
                baMatrix.stateProperty().addListener((argv,oldVal,newVal) -> {

                    if(newVal == Worker.State.RUNNING){
                        Platform.runLater(() -> {
                            start.setDisable(true);
                        });
                    }else if( newVal == Worker.State.FAILED || newVal == Worker.State.SUCCEEDED){
                        start.setDisable(false);
                    }
                });

                progressBar.progressProperty().bind(baMatrix.progressProperty());
            }



            t1.setDaemon(true);
            t1.start();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
