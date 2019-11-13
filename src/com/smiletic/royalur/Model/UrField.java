package com.smiletic.royalur.Model;

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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public final class UrField extends Pane implements Serializable {
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

    public UrField(Image urFieldImage, int x, int y ) {
        super();

        UrFieldImage=new ImageView();
        ImageViewSetup(UrFieldImage);
        TokenImage =new ImageView();
        ImageViewSetup(TokenImage);
        this.getChildren().add(UrFieldImage);
        this.getChildren().add(TokenImage);
        this.x = x;
        this.y = y;
        UrFieldImage.setImage(urFieldImage);
        this.setOnDragDetected(event -> {
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(TokenImage.getImage());
            db.setContent(content);
            event.consume();
        });
        this.setOnDragOver(event -> {
            if (event.getGestureSource() != this &&
                    event.getDragboard().hasImage()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();

        });
        this.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();

            if (db.hasImage()) {
                TokenImage.setImage(db.getImage());

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

            }
            event.consume();
        });
    }

    private static final int WIDTH = 50;




    private void SetFieldImage(Image img){
        if (UrFieldImage != null){
            UrFieldImage.setImage(img);
        }

    }

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
        if (TokenImage.getImage() !=null){
            aOutputStream.writeInt(1);
            BufferedImage image= SwingFXUtils.fromFXImage(TokenImage.getImage(),null);
            ImageIO.write(image, "png", aOutputStream);

        }else{
            aOutputStream.writeInt(0);

        }

        BufferedImage image2= SwingFXUtils.fromFXImage(UrFieldImage.getImage(),null);

        ImageIO.write(image2, "png", aOutputStream);

    }
    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
    {
        x = aInputStream.readInt();
        y = aInputStream.readInt();
        UrFieldImage=new ImageView();
        ImageViewSetup(UrFieldImage);
        TokenImage =new ImageView();
        ImageViewSetup(TokenImage);
        this.getChildren().add(UrFieldImage);
        this.getChildren().add(TokenImage);

        int temp = aInputStream.readInt();
        if(temp==1){
            BufferedImage bf = ImageIO.read(aInputStream);
            TokenImage.setImage(SwingFXUtils.toFXImage(bf,null));

        }
        BufferedImage bf2 = ImageIO.read(aInputStream);

        UrFieldImage.setImage(SwingFXUtils.toFXImage(bf2,null));

    }

}