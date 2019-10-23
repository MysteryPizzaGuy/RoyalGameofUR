package com.smiletic.royalur;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.io.File;
import java.util.*;

public class Controller {
    @FXML
    private GridPane gameGrid;

    private static final int WIDTH = 50;

    Map<String,Image> imageDict = new Hashtable<>();

    @FXML
    public void initialize() {
//        imageDict.put("dots",new Image("/res/dots.png",));
//        imageDict.put("eyes",new Image("/res/eyes.png"));
//        imageDict.put("rosette",new Image("/res/rosette.png"));
//        imageDict.put("waves",new Image("/res/waves.png"));
//        imageDict.put("dotwaves",new Image("/res/dotwaves.png"));
//        imageDict.put("dotgrids",new Image("/res/dotgrids.png"));
        imageDict.put("dots",new Image(getClass().getResource("res/dots.png").toString(), true));

        imageDict.put("eyes",new Image(getClass().getResource("res/eyes.png").toString(), true));
        imageDict.put("rosette",new Image(getClass().getResource("res/rosette.png").toString(), true));
        imageDict.put("waves",new Image(getClass().getResource("res/waves.png").toString(), true));
        imageDict.put("dotwaves",new Image(getClass().getResource("res/dotwaves.png").toString(), true));
        imageDict.put("dotgrids",new Image(getClass().getResource("res/dotgrids.png").toString(), true));
        imageDict.put("bluecounter",new Image(getClass().getResource("res/blue-counter.png").toString(), true));

        Stack<Pane> dotsStack = new Stack<>();
        Stack<Pane> eyesStack = new Stack<>();
        Stack<Pane> rosetteStack = new Stack<>();
        Stack<Pane> wavesStack = new Stack<>();
        Stack<Pane> dotwavesStack = new Stack<>();
        Stack<Pane> dotgridsStack = new Stack<>();
        fillStack(dotsStack,5,"dots");
        fillStack(dotwavesStack,2,"dotwaves");
        fillStack(eyesStack,5,"eyes");
        fillStack(rosetteStack,5,"rosette");
        fillStack(wavesStack,2,"waves");
        fillStack(dotgridsStack,1,"dotgrids");

        
        gameGrid.add(wavesStack.pop(),1,1);
        gameGrid.add(wavesStack.pop(),3,1);
        gameGrid.add(dotsStack.pop(),2,1);
        gameGrid.add(dotsStack.pop(),2,4);
        gameGrid.add(dotsStack.pop(),1,6);
        gameGrid.add(dotsStack.pop(),3,6);
        gameGrid.add(dotsStack.pop(),2,7);
        gameGrid.add(eyesStack.pop(),2,2);
        gameGrid.add(eyesStack.pop(),1,5);
        gameGrid.add(eyesStack.pop(),3,5);
        gameGrid.add(eyesStack.pop(),3,7);
        gameGrid.add(eyesStack.pop(),1,7);
        gameGrid.add(rosetteStack.pop(),1,2);
        gameGrid.add(rosetteStack.pop(),3,2);
        gameGrid.add(rosetteStack.pop(),2,5);
        gameGrid.add(rosetteStack.pop(),1,8);
        gameGrid.add(rosetteStack.pop(),3,8);
        gameGrid.add(dotwavesStack.pop(),2,3);
        gameGrid.add(dotwavesStack.pop(),2,6);
        gameGrid.add(dotgridsStack.pop(),2,8);
        gameGrid.add(getToken(),0,0);






    }
    private Pane getToken() {

        ImageView temp = new ImageView();

        temp.setImage(imageDict.get("bluecounter"));
        temp.setFitWidth(WIDTH);
        temp.setFitHeight(WIDTH);
        temp.setPreserveRatio(true);
        temp.setSmooth(true);
        temp.setCache(true);
        Pane ptemp = new Pane();
        ptemp.setOnDragDetected(event -> {
            Dragboard db = ptemp.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(temp.getImage());
            db.setContent(content);
            event.consume();
        });



        ptemp.getChildren().add(temp);
        return ptemp;
    }

    private void fillStack(Stack<Pane> xStack, int i,String x) {
        for (int j = 0; j < i ; j++) {
            ImageView temp = new ImageView();
            ImageView temp2 = new ImageView();
            temp.setImage(imageDict.get(x));
            temp.setFitWidth(WIDTH);
            temp.setFitHeight(WIDTH);
            temp.setPreserveRatio(true);
            temp.setSmooth(true);
            temp.setCache(true);
            temp2.setFitWidth(WIDTH);
            temp2.setFitHeight(WIDTH);
            temp2.setPreserveRatio(true);
            temp2.setSmooth(true);
            temp2.setCache(true);
            Pane ptemp = new Pane();
            ptemp.setOnDragDetected(event -> {
                Dragboard db = ptemp.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putImage(temp2.getImage());
                db.setContent(content);
                event.consume();
            });
            ptemp.setOnDragOver(event -> {
                if (event.getGestureSource() != ptemp &&
                        event.getDragboard().hasImage()) {
                    event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                }
                event.consume();

            });
            ptemp.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();

                if (db.hasImage()) {
                    temp2.setImage(db.getImage());

                    if(event.getGestureTarget()!=null){
                        event.setDropCompleted(true);
                    }else{
                        event.setDropCompleted(false);
                    }

                }
                event.consume();
            });
            ptemp.setOnDragDone(event -> {
                if (ptemp==event.getGestureSource() && !event.isDropCompleted()){
                    temp2.setImage(null);

                }
                event.consume();
            });

            ptemp.getChildren().add(temp);
            ptemp.getChildren().add(temp2);
            xStack.add(ptemp);
        }
    }


}
