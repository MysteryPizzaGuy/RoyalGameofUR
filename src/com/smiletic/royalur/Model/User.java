package com.smiletic.royalur.Model;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class User implements Serializable {
    String usercolor = "0x0000FF";
    String userName = "Player";

    public User(String userName,Color usercolor) {
        setUsercolor(usercolor);
        this.userName = userName;
    }

    public User() {
    }

    public Color getUsercolor() {
        return Color.web(usercolor);
    }

    public void setUsercolor(Color usercolor) {
        String webFormat = String.format("#%02x%02x%02x",
                (int) (255 * usercolor.getRed()),
                (int) (255 * usercolor.getGreen()),
                (int) (255 * usercolor.getBlue()));
        this.usercolor = webFormat;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
