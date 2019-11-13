package com.smiletic.royalur.Model;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class UrToken extends Pane implements Serializable {
    private int x;
    private int y;
    private static final long serialVersionUID= 1829336425241573335L;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getTeam() {
        return team;
    }

    private static final int WIDTH = 50;
    ImageView TokenImage;
    private int team;

    public UrToken(int x, int y, Image tokenImage,int teamint) {
        super();
        this.x = x;
        this.y = y;
        TokenImage = new ImageView();
        TokenImage.setImage(tokenImage);
        ImageViewSetup(TokenImage);

        this.setOnDragDetected(event -> {
            Dragboard db = this.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putImage(TokenImage.getImage());
            db.setContent(content);
            event.consume();
        });
        this.getChildren().add(TokenImage);
        team=teamint;
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
        aOutputStream.writeInt(team);
        BufferedImage image= SwingFXUtils.fromFXImage(TokenImage.getImage(),null);
        ImageIO.write(image, "png", aOutputStream);
    }
    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
    {
        x = aInputStream.readInt();
        y = aInputStream.readInt();
        team = aInputStream.readInt();
        BufferedImage bf = ImageIO.read(aInputStream);
        TokenImage.setImage(SwingFXUtils.toFXImage(bf,null));
    }
}
