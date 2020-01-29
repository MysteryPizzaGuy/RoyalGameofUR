package com.smiletic.royalur.RMI;

import com.smiletic.royalur.Model.User;
import javafx.scene.paint.Color;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Vector;

public class Server extends UnicastRemoteObject implements ServerInterface {


    private Vector<ClientData> clients;
    private static  final long serialVersionUID = 1L;
    private static int teamcount = 0;

    protected Server() throws RemoteException {
        super();
        clients= new Vector<ClientData>();
    }


    public static void startRMIRegistry(){
        try{
            LocateRegistry.createRegistry(1099);
            System.out.println("RMI Server started");
        }catch(RemoteException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        startRMIRegistry();
        String hostName = "localhost";
        String serviceName = "ChatRMI";
        try {
            ServerInterface stub = new Server();
            Naming.rebind("rmi://" + hostName + "/" + serviceName,stub);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }



    @Override
    public void broadcastMessage(User user, String message) throws RemoteException {
        for (ClientData c: clients
             ) {
            c.clientInterface.messageFromServer(message,user);
        }
    }

    @Override
    public void registerClient(User user, String clientServiceName, String hostName) {
        ClientInterface client = null;
        try {
            client = (ClientInterface) Naming.lookup("rmi://" + hostName+"/"+clientServiceName);
            int teamID = AssignTeam(client);
            clients.add(new ClientData(user,client,teamID));
            client.messageFromServer("You've joined" + user.getUserName() + " welcome!", new User("SERVER", Color.RED));


        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeClient() throws RemoteException {
        broadcastMessage(new User("SERVER", Color.RED),"Breaking Connection");
        clients.removeAllElements();
        teamcount=0;
    }

    @Override
    public void nextTurnBroadcast(int activeTeam) throws RemoteException {
        for (ClientData c:clients){
            c.clientInterface.nextTurnRecieve(activeTeam);
            if(c.teamID != activeTeam){
                c.clientInterface.blockMovement();
                System.out.println("ACTIVE TEAM IS NOW" + c.teamID);
            }
        }
    }

    @Override
    public int AssignTeam(ClientInterface c) throws RemoteException {
        c.AssignTeam(++teamcount);
        return teamcount;
    }


}
