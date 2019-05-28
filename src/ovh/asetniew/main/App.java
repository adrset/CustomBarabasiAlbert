package ovh.asetniew.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {


        Parent root = FXMLLoader.load(getClass().getResource("ui.fxml"));

        primaryStage.setTitle("Hello World");
        Scene s = new Scene(root, 1050, 600);
        s.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        primaryStage.setScene(s);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
