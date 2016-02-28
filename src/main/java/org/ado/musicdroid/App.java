package org.ado.musicdroid;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import se.vidstige.jadb.JadbException;

import java.io.IOException;

/**
 * Hello world!
 */
public class App extends Application {

    public static void main(String[] args) {
        if (StringUtils.isEmpty(System.getenv("MUSIC_HOME"))) {
            System.out.println("Missing MUSIC_HOME environment variable!");
            System.exit(1);
        }
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException, JadbException {
        Parent root = FXMLLoader.load(getClass().getResource("fxml/MainTreeAndCover.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Music Droid");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
