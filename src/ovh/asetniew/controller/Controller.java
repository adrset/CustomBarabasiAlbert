package ovh.asetniew.controller;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ovh.asetniew.ba.BASimulation;
import ovh.asetniew.ba.BASimulationUsingLists;

public class Controller {
    BASimulation ba;
    BASimulationUsingLists bal;
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
            ba = new BASimulation(5, 1, 1000);
            bal = new BASimulationUsingLists(5, 1, 100000);
            bal.start();
            // Obtain Graphics Contexts
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.GREEN);
            gc.fillOval(50,50,20,20);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
