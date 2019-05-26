package ovh.asetniew.controller;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
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
    TextField textGamma;

    @FXML
    ChoiceBox mode;
    @FXML
    TextField textM;
    @FXML
    TextField textM0;
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
        textM0.setTextFormatter(
                new TextFormatter<Integer>(new IntegerStringConverter(), 0, integerFilter));

        TextFormatter<Double> textFormatter = new TextFormatter<>(converter, 0.0, doubleFilter);

        textGamma.setTextFormatter(textFormatter);


        mode.getItems().add("BAListLinear");
        mode.getItems().add("BAList");
        mode.getItems().add("BAMatrix");
        mode.setValue(mode.getItems().get(0));

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
        verticalAxis.setLabel("X");
        ValueAxis horizontalAxis = new LogarithmicNumberAxis();

        horizontalAxis.setLabel("Y");
        scatterPlot = new ScatterChart<Number, Number>(verticalAxis, horizontalAxis);
        scatterPlot.setTitle("The title");


        paneView.getChildren().add(scatterPlot);

        ValueAxis verticalAxis2 = new NumberAxis();
        verticalAxis.setLabel("X");
        ValueAxis horizontalAxis2 = new LogarithmicNumberAxis();

        horizontalAxis2.setLabel("Y");

        scatterPlot2 = new ScatterChart<Number, Number>(verticalAxis2, horizontalAxis2);

        scatterPlot2.setTitle("The title");


        paneView2.getChildren().add(scatterPlot2);
    }

    public void adjustM() {
        this.m = Integer.valueOf(textM.getText().toString());
    }

    public void adjustM_0() {
        this.m0 = Integer.valueOf(textM0.getText().toString());

    }
    public void adjustGamma() {
        this.gamma = Double.valueOf(textGamma.getText().toString());

    }


    public void onExit() {
        Platform.exit();
    }

    public void onStart() {

        try {
            // ba = new BASimulationUsingMatrix(5, 1, 1000);
            // bal = new BASimulationUsingLists(5, 1, 1000, scatterPlot);
            System.out.println("M->" + m + " M_0->" + m0 + " gamma->" + gamma);

            if(theMode == 0 ){
                ba = new BASimulationNonLinearUsingLists(m0, m, 1000, gamma, scatterPlot,scatterPlot2);
                t1 = new Thread(ba);
                progressBar.progressProperty().bind(ba.progressProperty());
            }else if(theMode == 1){
                ba = new BASimulationUsingLists(m0, m, 1000, scatterPlot,scatterPlot2);
                t1 = new Thread(ba);
                progressBar.progressProperty().bind(ba.progressProperty());
            }else{
                baMatrix = new BASimulationUsingMatrix(m0, m, 1000);
                t1 = new Thread(baMatrix);

                progressBar.progressProperty().bind(baMatrix.progressProperty());
            }



            t1.setDaemon(true);
            t1.start();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
