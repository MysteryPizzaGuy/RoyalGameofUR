package com.smiletic.royalur;

import com.smiletic.royalur.Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class MultiplayerSetup {

    @FXML
    private TextField tfIPAdress;

    @FXML
    private TextField tfUserName;
    @FXML
    private ColorPicker cpUserColor;

    @FXML
    private void HandleButtonBack(ActionEvent event) throws IOException {
        Stage stage = (Stage) tfIPAdress.getScene().getWindow();
        FXMLLoader loader =new FXMLLoader(getClass().getResource("StartScreen.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
//        Controller controller = loader.getController();
//        stage.setOnHidden(e -> controller.Shutdown());
//        controller.ConnectToServer();
        stage.show();
    }

    @FXML
    private void HandleButtonPlay(ActionEvent event) throws IOException {
        if(tfIPAdress.getText()!=null){
            String ip = tfIPAdress.getText();
            Color color = cpUserColor.getValue();
            String username = tfUserName.getText();
            Stage stage = (Stage) tfIPAdress.getScene().getWindow();
            FXMLLoader loader =new FXMLLoader(getClass().getResource("Game.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            Controller controller = loader.getController();
            stage.setOnHidden(e -> controller.Shutdown());
            controller.ConnectToServer(ip,new User(username,color));
            stage.show();
        }else{
            return;
        }
    }
    @FXML
    private void HandleButtonLocalHost(ActionEvent event) throws IOException {
        tfIPAdress.setText("localhost");
    }

}
