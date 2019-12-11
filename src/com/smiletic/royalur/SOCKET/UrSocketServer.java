package com.smiletic.royalur.SOCKET;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class UrSocketServer {
    public static void main(String[] args) throws IOException {
        try(ServerSocket listener = new ServerSocket(58901)){
            ExecutorService pool = Executors.newFixedThreadPool(20);
            Game game  =new Game();
            while(true){
                pool.submit(game.new Player(listener.accept()));
            }
        }
    }
}


class Game {
Player player1 =null;
Player player2 =null;
Object lock =new Object();
    public class Player implements Runnable {

        Socket socket;

        Player(Socket socket) {
            this.socket = socket;
            if(player1!=null){
                player2=this;

            }else{
                player1=this;
            }

        }

        ObjectOutputStream output;
        ObjectInputStream input;

        @Override
        public void run() {
            System.out.println("Running NEW THREAD");
            try{
                synchronized (lock){
                    //Use lock
                    if(player2 ==null) {
                        try {
                            player1.input= new ObjectInputStream(player1.socket.getInputStream());
                            player1.output = new ObjectOutputStream(player1.socket.getOutputStream());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            player1.output.writeObject(new String("Waiting for Player 2 to connect"));
                            player1.output.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }else{
                        try {
                            player2.input = new ObjectInputStream(player2.socket.getInputStream());
                            player2.output=new ObjectOutputStream(player2.socket.getOutputStream());

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            player2.output.writeObject("Connecting to Player1");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                while (socket.isConnected()) {
                    DataCarrier data = null;
                    try {
                        data = (DataCarrier) input.readObject();
                        System.out.println(data);
                    } catch (EOFException e){
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    if (data != null && output !=null)  {
                        try {
                            if(player1==this){
                                player2.output.writeObject(data);
                                player2.output.flush();
                            }else{
                                player1.output.writeObject(data);
                                player1.output.flush();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    }
            }finally {
                try {
                    try{input.close();
                        output.close();}finally {
                        socket.close();
                        System.out.println("THREAD CUT");
                    }

                    if(player1==this){
                        player1=null;
                    }else{
                        player2=null;
                    }

                    ;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}