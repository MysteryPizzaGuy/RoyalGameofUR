package com.smiletic.royalur.Model;

import com.smiletic.royalur.SOCKET.UrSocketClient;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import jdk.nashorn.internal.parser.Token;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class UrField extends Pane implements Serializable,Component {
    int x;
    int y;
    private static final long serialVersionUID= 7829136421241571165L;
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    ImageView UrFieldImage;
    ImageView TokenImage;
    UrToken urToken;
    static public UrSocketClient socketClient = null;

    public UrField(Image urFieldImage, int x, int y ) {
        super();
        urToken=null;
        UrFieldImage=new ImageView();
        ImageViewSetup(UrFieldImage);
        TokenImage =new ImageView();
        ImageViewSetup(TokenImage);
        this.getChildren().add(UrFieldImage);
        this.getChildren().add(TokenImage);
        this.x = x;
        this.y = y;
        UrFieldImage.setImage(urFieldImage);
        SetListeners();


    }

    public void setUrToken(UrToken urToken) {
        if(urToken!=null){
            UrToken newToken = new UrToken(urToken.getX(),urToken.getY(),urToken.TokenImage.getImage(),urToken.getTeam());
            this.urToken = newToken;
            this.urToken.TokenImage.setImage(newToken.TokenImage.getImage());
            this.TokenImage.setImage(newToken.TokenImage.getImage());
//            ImageViewSetup(this.TokenImage);

            urToken.setX(this.getX());
            urToken.setY(this.getY());
        }
    }
    public void RemoveUrToken(){
        TokenImage.setImage(null);

        UrToken token = urToken;
        this.urToken=null;

    }

    public UrToken getUrToken() {
        return urToken;
    }

    private void SetListeners(){
    this.setOnDragDetected(event -> {
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(UrToken.urTokenFormat,urToken);
//            content.putImage(TokenImage.getImage());
            db.setContent(content);
            event.consume();
        });
        this.setOnDragOver(event -> {
            if (event.getGestureSource() != this &&
//                    event.getDragboard().hasImage()) {
                        event.getDragboard().hasContent(UrToken.urTokenFormat)) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();

        });
        this.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

//            if (db.hasImage()) {
            if (db.hasContent(UrToken.urTokenFormat)) {

//                TokenImage.setImage(db.getImage());
                Object content=  db.getContent(UrToken.urTokenFormat);
                urToken = (UrToken) content;
                urToken.setX(x);
                urToken.setY(y);
                Object object =  event.getGestureSource();

                Component source =null;
                if(UrField.class.isInstance(object)){
                    source= (UrField) object;
                }else{
                    source= (UrToken) object;
                }


                if(socketClient!=null ){
                    if(event.getGestureSource() instanceof UrField){
                        socketClient.Moved(urToken.getX(),urToken.getY(),source.getX(),source.getY(),urToken.getTeam());
                    }else{
                        socketClient.Created(urToken.getX(),urToken.getY(),source.getX(),source.getY(),urToken.getTeam());

                    };
                }
                System.out.println("X:"+urToken.getX()+"Y:"+urToken.getY());
                TokenImage.setImage(urToken.TokenImage.getImage());
                if(event.getGestureTarget()!=null){
                    event.setDropCompleted(true);
                }else{
                    event.setDropCompleted(false);
                }

            }
            event.consume();
        });
        this.setOnDragDone(event -> {
            if (this==event.getGestureSource() && !event.isDropCompleted()){
                TokenImage.setImage(null);
                urToken=null;

            }
            event.consume();
        });
    }

    private static final int WIDTH = 50;




//    private void SetFieldImage(Image img){
//        if (UrFieldImage != null){
//            UrFieldImage.setImage(img);
//        }
//
//    }

    private void ImageViewSetup(ImageView iv){
        iv.setFitHeight(WIDTH);
        iv.setFitWidth(WIDTH);
        iv.setPreserveRatio(true);
        iv.setSmooth(true);
        iv.setCache(true);
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException
    {
        aOutputStream.writeInt(x);
        aOutputStream.writeInt(y);
        if (urToken !=null){
            aOutputStream.writeObject(urToken);
        }else{
            aOutputStream.writeObject(null);
        }
//        if (TokenImage.getImage() !=null){
//            ByteArrayOutputStream b = new ByteArrayOutputStream();
//            BufferedImage image= SwingFXUtils.fromFXImage(TokenImage.getImage(),null);
//            ImageIO.write(image, "png", b);
//            aOutputStream.writeInt(b.size());
//            b.writeTo(aOutputStream);
//
//        }else{
//            aOutputStream.writeInt(0);
//
//        }
        if(UrFieldImage.getImage()!=null){
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            BufferedImage image= SwingFXUtils.fromFXImage(UrFieldImage.getImage(),null);
            ImageIO.write(image, "png", b);
            aOutputStream.writeInt(b.size());
            b.writeTo(aOutputStream);
        }else{
            aOutputStream.writeInt(0);

        }


    }
    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
    {
        x = aInputStream.readInt();
        y = aInputStream.readInt();
        UrFieldImage=new ImageView();
        TokenImage=new ImageView();

        ImageViewSetup(UrFieldImage);

        urToken = (UrToken) aInputStream.readObject();
        if(urToken!=null){
            TokenImage=urToken.TokenImage;
            urToken.setX(this.getX());
            urToken.setY(this.getY());
        }
        ImageViewSetup(TokenImage);

        this.getChildren().add(UrFieldImage);
        this.getChildren().add(TokenImage);

//        int length = aInputStream.readInt();
//        if(length>0){
//            byte[] bytes = new byte[length];
//            aInputStream.readFully(bytes);
//            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
//            TokenImage.setImage(SwingFXUtils.toFXImage(image,null));
//
//        }
        int length = aInputStream.readInt();

        if(length>0){
            byte[] bytes = new byte[length];
            aInputStream.readFully(bytes);
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            UrFieldImage.setImage(SwingFXUtils.toFXImage(image,null));
        
        }
        SetListeners();


        
    }

}
