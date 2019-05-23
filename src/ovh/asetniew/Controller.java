package ovh.asetniew;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import java.io.File;
public class Controller {
    BASimulation ba;
    public Controller(){

    }
    @FXML
    private Canvas canvas;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private TextField brushSize;

    @FXML
    private CheckBox eraser;

    public void initialize() {
        GraphicsContext g = canvas.getGraphicsContext2D();

    }

    public void onSave() {
        try {
            Image snapshot = canvas.snapshot(null, null);

            ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", new File("paint.png"));
        } catch (Exception e) {
            System.out.println("Failed to save image: " + e);
        }
    }

    public void onExit() {
        Platform.exit();
    }

    public void onStart(){

        try {
            ba = new BASimulation(5, 1, 10000);
            ba.printNeightbourMatrix();

            for (int ii = ba.getCurrentStep(); ii < ba.getMaxSteps(); ii++) {
                ba.update();
                System.out.println(ba.getProbability(1));
            }



            // Obtain Graphics Contexts
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.setFill(Color.GREEN);
            gc.fillOval(50,50,20,20);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
