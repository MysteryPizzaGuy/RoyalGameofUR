package com.smiletic.royalur.SOCKET;

import com.smiletic.royalur.Controller;
import com.smiletic.royalur.Model.UrField;
import com.smiletic.royalur.Model.User;
import com.smiletic.royalur.RMI.Server;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.paint.Color;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class UrSocketClient implements Runnable {
    Socket socket;
    ObjectOutputStream out;
    ObjectInputStream in;
    Controller ctrl;
    private static final int MOVE = 1;
    private static final int CREATE = 2;

    public UrSocketClient(String serverAddress,Controller ctrl)  throws IOException {
        this.socket = new Socket(serverAddress, 58901);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());
        this.ctrl=ctrl;

    }
    public void Moved(int newx, int newy,int oldx,int oldy,int team){
        try {
            out.writeObject(new DataCarrier(oldx,oldy,newx,newy,team,MOVE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void Shutdown(){
        try{
            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void run() {
        try {
            while(socket.isConnected()) {

                try{
                    final Object obj = in.readObject();

                    if(obj instanceof String && obj!=null){
                        try {
                            Platform.runLater(()->ctrl.AppendToConsole(obj.toString(),new User("SERVER",Color.RED)));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if( obj!=null){
                        DataCarrier dataCarrier= (DataCarrier) obj;
                        if(dataCarrier.type==MOVE){
                            Platform.runLater(() -> ctrl.MoveToken(dataCarrier.oldx,dataCarrier.oldy,dataCarrier.newx,dataCarrier.newy));
                        }else if(dataCarrier.type==CREATE) {
                            Platform.runLater(() -> ctrl.CreateToken(dataCarrier.newx,dataCarrier.newy,dataCarrier.team));

                        }

                    }
                } catch (SocketException s){
                    break;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        } finally{
            Shutdown();
            System.out.println("THREAD CUT");
    }
}

    public void Created(int newx, int newy,int oldx,int oldy,int team){
        try {
            out.writeObject(new DataCarrier(oldx,oldy,newx,newy,team,CREATE));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
