import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Application that launches the game with the client. Will pause the user's interaction with the server until they
 * close out the game window.
 */
public class GameRPS extends Application {

    /**
     * required main function
     *
     * @param args required inputs to run a java application
     */
   public static void main(String[] args) {
        launch(args);
   }

    /**
     * overriden start method for an application. allows the proper fxml file, and specifics to be chosen
     *
     * @param primaryStage the location in which the fxml file will be placed
     * @throws IOException exception thrown if the fxml file is missing or corrupt in some way
     */
    @Override
    public void start(Stage primaryStage)throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("GameRPS_GUI.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("SHELDON GAME");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}

