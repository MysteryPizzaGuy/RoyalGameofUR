package com.smiletic.royalur.RMI;

import com.smiletic.royalur.Model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
    public void messageFromServer(String message, User user) throws RemoteException;
    void registerWithServer() throws RemoteException;
}
