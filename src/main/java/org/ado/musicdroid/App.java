package org.ado.musicdroid;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import se.vidstige.jadb.JadbException;

import java.io.IOException;

/**
 * Hello world!
 */
public class App extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, JadbException {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/MainTreeAndCover.fxml"));
      /*  Parent root = (Parent)loader.load();
        MyController controller = (MyController)loader.getController();
        controller.setStageAndSetupListeners(stage); // or what you want to do*/


        Scene scene = new Scene(root);

        primaryStage.setTitle("MusicDroid");
        primaryStage.setScene(scene);

        primaryStage.show();
    }
}
