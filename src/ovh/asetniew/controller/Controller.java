package ovh.asetniew.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Pane;
import ovh.asetniew.ba.BASimulationNonLinearUsingLists;
import ovh.asetniew.ba.BASimulationUsingMatrix;
import ovh.asetniew.ba.BASimulationUsingLists;

public class Controller {
    BASimulationUsingMatrix ba;
    BASimulationUsingLists bal;
    BASimulationNonLinearUsingLists ba2;
    Thread t1;
    @FXML
    private Pane paneView;

    public Controller() {

    }

    @FXML
    private Canvas canvas;

    public void initialize() {
       loadData();

    }

    private void loadData(){
        paneView.getChildren().clear();
        NumberAxis verticalAxis = new NumberAxis(0,5,1);
        verticalAxis.setLabel("X");
        NumberAxis horizontalAxis = new NumberAxis(0,5,1);
        horizontalAxis.setLabel("Y");
        ScatterChart<Number, Number> scatterPlot = new ScatterChart<Number, Number>(verticalAxis, horizontalAxis);
        scatterPlot.setTitle("The title");
        XYChart.Series series = new XYChart.Series();
        series.setName("Title");
        series.getData().add(new XYChart.Data<>(0,0));
        series.getData().add(new XYChart.Data<>(1,1));
        series.getData().add(new XYChart.Data<>(2,2));
        scatterPlot.getData().add(series);
        scatterPlot.setMaxWidth(500);
        scatterPlot.setMaxHeight(500);
        paneView.getChildren().add(scatterPlot);
    }


    public void onExit() {
        Platform.exit();
    }

    public void onStart() {

        try {
            ba = new BASimulationUsingMatrix(5, 1, 1000);
            bal = new BASimulationUsingLists(5, 1, 1000);
            ba2 = new BASimulationNonLinearUsingLists(5, 1, 1000, 0.75);
            t1 = new Thread(ba2);
            t1.start();


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
