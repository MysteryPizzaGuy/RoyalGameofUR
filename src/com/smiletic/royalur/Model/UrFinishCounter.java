/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.smiletic.royalur.Model;

import com.smiletic.royalur.Controller;
import static com.smiletic.royalur.Model.UrField.socketClient;
import com.smiletic.royalur.SOCKET.UrSocketClient;
import com.smiletic.royalur.XML.Action;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import javax.imageio.ImageIO;

/**
 *
 * @author Korisnik
 */
public class UrFinishCounter extends Pane implements Serializable,Component {
    int x;
    int y;
    private static final long serialVersionUID= 7829136421241671112L;
    static public UrSocketClient socketClient = null;

    int count;
    
    private Label lblCount;
    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    public UrFinishCounter(int x, int y, int count) {
        this.x = x;
        this.y = y;
        this.count = count;
        lblCount=new Label();
        this.getChildren().add(lblCount);
        
        lblCount.setText(Integer.toString(count));
        SetListeners();
    }
    
    void SetListeners(){
         this.setOnDragOver(event -> {
            if (event.getDragboard().hasContent(UrToken.urTokenFormat)) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();

        });
        this.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            Object object =  event.getGestureSource();

                Component source =null;
                if(UrField.class.isInstance(object)){
                    source= (UrField) object;
                }else{
                    source= (UrToken) object;
                }

            if (db.hasContent(UrToken.urTokenFormat)) {
                if(socketClient!=null){
//                   socketClient.CounterUP(this.getX(),this.getY());

                }else{
                    lblCount.setText(Integer.toString(++count));

                }
            }
            event.consume();
            });
        this.setOnDragDone(event -> {
                event.consume();
            });
        }
    
    private void writeObject(ObjectOutputStream aOutputStream) throws IOException
    {
        aOutputStream.writeInt(x);
        aOutputStream.writeInt(y);
        aOutputStream.writeInt(count);


    }
    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
    {
        x = aInputStream.readInt();
        y = aInputStream.readInt();
        count=aInputStream.readInt();
        lblCount=new Label();
        this.getChildren().add(lblCount);
        lblCount.setText(Integer.toString(count));
        
        SetListeners();


        
    }
    
}
