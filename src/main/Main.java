/**
 * @author Doug Van Zee
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

/**
 * Main program
 */
public class Main extends Application {

    /**
     * Launches main form window
     * @param primaryStage
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("MainForm.fxml"));
        primaryStage.setTitle("Inventory Management System");
        primaryStage.setMinHeight(500);
        primaryStage.setMinWidth(800);
        primaryStage.setScene(new Scene(root, 1280, 720));
        primaryStage.show();


    }

    /**
     * Main function
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    }
}
