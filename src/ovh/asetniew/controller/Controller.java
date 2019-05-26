package ovh.asetniew.controller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ovh.asetniew.ba.BASimulationNonLinearUsingLists;
import ovh.asetniew.ba.BASimulationUsingMatrix;
import ovh.asetniew.ba.BASimulationUsingLists;

public class Controller {
    BASimulationUsingMatrix ba;
    BASimulationUsingLists bal;
    BASimulationNonLinearUsingLists ba2;
    Thread t1;
    public Controller(){

    }
    @FXML
    private Canvas canvas;

    public void initialize() {
        GraphicsContext g = canvas.getGraphicsContext2D();

    }

    public void onExit() {
        Platform.exit();
    }

    public void onStart(){

        try {
            ba = new BASimulationUsingMatrix(5, 1, 1000);
            bal = new BASimulationUsingLists(5, 1, 1000);
            ba2 = new BASimulationNonLinearUsingLists(5, 1, 1000, 0.75);
            t1 = new Thread(ba2);
            t1.start();
            // Obtain Graphics Contexts
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.GREEN);
            gc.fillOval(50,50,20,20);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
