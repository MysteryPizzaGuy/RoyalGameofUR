package com.smiletic.royalur;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
//        FXMLLoader loader =new FXMLLoader(getClass().getResource("Game.fxml"));
//        Parent root = loader.load();
//        primaryStage.setScene(new Scene(root));
//        Controller controller = loader.getController();
//        primaryStage.setOnHidden(e -> controller.Shutdown());
//        primaryStage.show();
        FXMLLoader loader =new FXMLLoader(getClass().getResource("StartScreen.fxml"));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
