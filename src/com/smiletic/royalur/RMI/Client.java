package com.smiletic.royalur.RMI;

import com.smiletic.royalur.Controller;
import com.smiletic.royalur.Model.User;
import javafx.application.Platform;
import javafx.scene.paint.Color;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements ClientInterface {
    private static final long serialVersionUID = 7468891722773409712L;
    public ServerInterface server;
    private User user;
    private Controller ctrl;
    private static final String hostName = "localhost";
    private static final String serviceName = "ChatRMI";
    private String clientServiceName;

    public Client(User user, Controller ctrl) throws RemoteException {
        super();
        this.user=user;
        this.ctrl=ctrl;
        this.clientServiceName= "ClientListener_"+user.getUserName();
    }

    public void startClient() throws RemoteException {
        boolean conerror =false;
        try {
            Naming.rebind("rmi://" + hostName + "/" + clientServiceName, this);
            server= (ServerInterface) Naming.lookup("rmi://" + hostName + "/" + serviceName);
        } catch (RemoteException e) {
            conerror =true;
            e.printStackTrace();
        } catch (MalformedURLException e) {
            conerror =true;

            e.printStackTrace();
        } catch (NotBoundException e) {
            conerror =true;

            e.printStackTrace();
        }
        if(!conerror){
            registerWithServer();
        }
    }
    @Override
    public void messageFromServer(String message, User user) throws RemoteException{
        Platform.runLater(() ->  ctrl.AppendToConsole(message,user));
    }
    @Override
    public void registerWithServer() throws RemoteException {
        server.registerClient(user,clientServiceName,hostName);


    }


}
