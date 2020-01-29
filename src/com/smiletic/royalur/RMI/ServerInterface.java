package com.smiletic.royalur.RMI;

import com.smiletic.royalur.Model.User;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerInterface extends Remote {
    void broadcastMessage(User user, String message) throws RemoteException;
    void registerClient(User user, String clientServiceName, String hostName) throws RemoteException;
    void removeClient() throws RemoteException;
    void nextTurnBroadcast(int activeTeam) throws RemoteException;

    int AssignTeam(ClientInterface c) throws  RemoteException;
}
