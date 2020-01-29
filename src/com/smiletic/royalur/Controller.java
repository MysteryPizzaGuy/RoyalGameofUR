package com.smiletic.royalur;

import com.smiletic.royalur.Model.UrField;
import com.smiletic.royalur.Model.UrToken;
import com.smiletic.royalur.Model.User;
import com.smiletic.royalur.RMI.Client;
import com.smiletic.royalur.SOCKET.UrSocketClient;
import com.smiletic.royalur.XML.Action;
import com.smiletic.royalur.XML.ActionList;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.awt.print.Book;
import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static j2html.TagCreator.*;
import static java.util.concurrent.Executors.newFixedThreadPool;

public class Controller {
    @FXML
    private GridPane gameGrid;
    Random rand = new Random();

    @FXML
    private Label lblRollResult;
    @FXML
    private Label lblRollResult2;
    @FXML
    private Label lblRollResult3;

    @FXML
    private Label lblRollResult4;

    List<Label> LabelList = new ArrayList<>();


    @FXML
    TextFlow txtflwChatOutput;

    @FXML
    TextField txtfldInput;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnRoll;





    UrSocketClient socketClient= null;
    ExecutorService pool;
    private static int MPClientTeam = 0;

    public void Shutdown(){
        if(socketClient!=null){
            socketClient.Shutdown();
        }
        pool.shutdown();

        try{
            pool.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Child threads Killed");
        if(client!=null){
            try {
                client.unregisterFromServer();
                UnicastRemoteObject.unexportObject(client,true);
            } catch (NoSuchObjectException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    public void ConnectToServer(String ip, User user){
        this.user = user;
        try {
            client= new Client(user,this);
            client.startClient();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        try {
            socketClient = new UrSocketClient(ip,this);
            socketClient =socketClient;
            pool= Executors.newFixedThreadPool(5);
            pool.execute(socketClient);
            UrField.socketClient=socketClient;
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    private void HandlebtnNextTurn(ActionEvent event) throws RemoteException {
        int activeteam=0;
        if (client==null) {
            if(CurrentlyActiveTeam ==BLUETEAMINT){
                MakeTeamActive(REDTEAMINT);
            }else{
                MakeTeamActive(BLUETEAMINT);

            }
        }else{
            if(CurrentlyActiveTeam ==BLUETEAMINT){
                client.server.nextTurnBroadcast(REDTEAMINT);
                activeteam =REDTEAMINT;


            }else{
                client.server.nextTurnBroadcast(BLUETEAMINT);
                activeteam =BLUETEAMINT;

            }
        }
        if(client!=null) {
            client.server.broadcastMessage(user, "It's now " + (activeteam == BLUETEAMINT ? "BLUE's" : "RED's") + " turn!");
        }
    }

    private static final String ACTIONS_XML = "actionlist.xml";
    @FXML
    private void HandlebtnGenerateXMLReplay(ActionEvent event) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(ActionList.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
        ActionList a = new ActionList();
        a.setActionList(actionList);
        m.marshal(a,new File(ACTIONS_XML));
    }

    @FXML
    private void HandlebtnReplay(ActionEvent event) throws JAXBException, FileNotFoundException, InterruptedException {
        JAXBContext context = JAXBContext.newInstance(ActionList.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,Boolean.TRUE);
        Unmarshaller um = context.createUnmarshaller();
        ActionList temp = (ActionList) um.unmarshal(new FileReader(ACTIONS_XML));
        ArrayList<Action> actionstoreproduce = temp.getActionList();

        Task<Void> task = new Task<Void>() {
            @Override
            public Void call() throws Exception {
                for (Action action:actionstoreproduce
                ) {
                    if(action.getType()==Action.Types.CREATED){
                        Platform.runLater(()-> CreateToken(action.getTox(),action.getToy(),action.getTeam()));
                    }else{
                        Platform.runLater(()-> MoveToken(action.getFromx(),action.getFromy(),action.getTox(),action.getToy()));
                    }
                    Thread.sleep(1000);

                }
                return null ;
            }

        };
        new Thread(task).start();

//        for (Action action:actionstoreproduce
//             ) {
//            if(action.getType()==Action.Types.CREATED){
//                CreateToken(action.getTox(),action.getToy(),action.getTeam());
//            }else{
//                MoveToken(action.getFromx(),action.getFromy(),action.getTox(),action.getToy());
//            }
//
//        }

    }

    @FXML
    private void HandlebtnRollAction(ActionEvent event) throws RemoteException {
        int result= 0;
        for (Label item:LabelList
        ) {
            int i=rand.nextInt(2);
            item.setText(i==0 ? "X" : "O");
            result+=i;
        }
        SendSystemMessage(user.getUserName() + "has rolled " + result +"!");
    }
    @FXML
    private void HandlebtnSaveAction(ActionEvent event){
        try {
//            int counter = 0;
//            for (UrField field:fields) {
//                FileOutputStream fileOut = new FileOutputStream("Field" +counter+".ser");
//                ObjectOutputStream out = new ObjectOutputStream(fileOut);
//                out.writeObject(field);
//                out.close();
//                fileOut.close();
//                counter++;
//            }
            FileOutputStream fileOut = new FileOutputStream("savedata.ser");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(fields);
            out.close();
            fileOut.close();
        }
        catch (IOException i)
        {
            i.printStackTrace();
        }
    }

    private static ArrayList<Action> actionList = new ArrayList<Action>();
    private static final int WIDTH = 50;
    private static final boolean MULTIPLAYER =true;
    private static final int BLUETEAMINT = 1;
    private static final int REDTEAMINT = 2;
    private static Client client;
    private static User user;
    UrToken BlueTokenSpawn;
    UrToken RedTokenSpawn;
    private static int CurrentlyActiveTeam;
    Map<String,Image> imageDict = new Hashtable<>();
    Stage mainstage;
    List<UrField> fields;


    @FXML
    public void initialize() throws RemoteException {

        imageDict.put("dots",new Image(getClass().getResource("res/dots.png").toString(), true));

        imageDict.put("eyes",new Image(getClass().getResource("res/eyes.png").toString(), true));
        imageDict.put("rosette",new Image(getClass().getResource("res/rosette.png").toString(), true));
        imageDict.put("waves",new Image(getClass().getResource("res/waves.png").toString(), true));
        imageDict.put("dotwaves",new Image(getClass().getResource("res/dotwaves.png").toString(), true));
        imageDict.put("dotgrids",new Image(getClass().getResource("res/dotgrids.png").toString(), true));
        imageDict.put("bluecounter",new Image(getClass().getResource("res/blue-counter.png").toString(), true));
        imageDict.put("redcounter",new Image(getClass().getResource("res/red-counter.png").toString(), true));
        LabelList.addAll(Arrays.asList(lblRollResult,lblRollResult2,lblRollResult3,lblRollResult4));
        user = new User();
        BlueTokenSpawn = new UrToken(0,0,imageDict.get("bluecounter"),BLUETEAMINT);
        RedTokenSpawn = new UrToken(4,0,imageDict.get("redcounter"),REDTEAMINT);

        try {
            fields=new ArrayList<>();
            for (int i =0;i<20;i++){
                FileInputStream fileIn = new FileInputStream("savedata.ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                fields= (ArrayList<UrField>)in.readObject();
//                fields.add((UrField)in.readObject());
                in.close();
                fileIn.close();
            }
            for (UrField field:fields) {
                gameGrid.add(field,field.getX(),field.getY());
            }
            UrField temp= fields.stream().filter(x-> x.getUrToken()!=null).findFirst().get();

            if(temp.getUrToken().isCurrentlyActive()){
                MakeTeamActive(temp.getUrToken().getTeam());

            }else{
                MakeTeamActive(temp.getUrToken().getTeam() == BLUETEAMINT ? REDTEAMINT : BLUETEAMINT);

            }
        }catch (FileNotFoundException ex){
            fields = CreateField();
            for (UrField field:fields) {
                gameGrid.add(field,field.getX(),field.getY());
            }
            MakeTeamActive(BLUETEAMINT);
        }catch (IOException i){
            i.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }


        gameGrid.add(BlueTokenSpawn,BlueTokenSpawn.getX(),BlueTokenSpawn.getY());
        gameGrid.add(RedTokenSpawn,RedTokenSpawn.getX(),RedTokenSpawn.getY());








    }
    public void MakeTeamActive(int ActiveTeam) throws RemoteException {
        if(ActiveTeam == BLUETEAMINT){
            BlueTokenSpawn.setCurrentlyActive(true);
            RedTokenSpawn.setCurrentlyActive(false);
            for (UrField field:fields
                 ) {
                if (field.getUrToken()!=null){
                    if(field.getUrToken().getTeam()==BLUETEAMINT){
                        field.getUrToken().setCurrentlyActive(true);
                    }else{
                        field.getUrToken().setCurrentlyActive(false);
                    }
                }
            }
        }else{
            BlueTokenSpawn.setCurrentlyActive(false);
            RedTokenSpawn.setCurrentlyActive(true);
            for (UrField field:fields
            ) {
                if (field.getUrToken()!=null){
                    if(field.getUrToken().getTeam()==REDTEAMINT){
                        field.getUrToken().setCurrentlyActive(true);
                    }else{
                        field.getUrToken().setCurrentlyActive(false);
                    }
                }
            }
        }
        CurrentlyActiveTeam=ActiveTeam;

    }
    public void BlockAllMovement(){
        BlueTokenSpawn.setCurrentlyActive(false);
        RedTokenSpawn.setCurrentlyActive(false);
        for (UrField field:fields
        ) {
            if (field.getUrToken()!=null){
                field.getUrToken().setCurrentlyActive(false);

            }
        }
    }

    public static void AddActionToList(int fromx, int fromy, int tox, int toy, int team, Action.Types type){
        actionList.add(new Action(fromx,fromy,tox,toy,team,type));
    }


    public void MoveToken(int fromx, int fromy, int tox, int toy){
        Optional<UrField> fieldoptional = fields.stream().filter(urField -> fromx==urField.getX() && urField.getY()==fromy).findAny();
        if(fieldoptional.isPresent()){
            UrField field = fieldoptional.get();
            UrToken token = field.getUrToken();
            if (token!=null){
                Optional<UrField> targetfieldoptional = fields.stream().parallel().filter(urField -> tox==urField.getX() && urField.getY()==toy).findAny();
                if(targetfieldoptional.isPresent()){
                    UrField targetfield = targetfieldoptional.get();
                    targetfield.setUrToken(token);
                    field.RemoveUrToken();
                    AppendToConsole("Moving on this window",new User("System",Color.RED));

                }

            }



        }
    }
    public void CreateToken(int tox, int toy,int team){
         Optional<UrField> targetfieldoptional = fields.stream().parallel().filter(urField -> tox==urField.getX() && urField.getY()==toy).findAny();
         if(targetfieldoptional.isPresent()){
             UrField targetfield = targetfieldoptional.get();
             UrToken token=null;
             if(team ==BLUETEAMINT){
                 token=BlueTokenSpawn;
             }else{
                 token=RedTokenSpawn;
             }
             targetfield.setUrToken(token);
             AppendToConsole("Moving on this window",new User("System",Color.RED));




        }
    }

    @FXML
    private void HandletxtInputSubmitMessage(ActionEvent events) throws RemoteException {
        if(client !=null){
            client.server.broadcastMessage(user,txtfldInput.getText());
        }else{
            AppendToConsole("Establish a connection first",new User("SYSTEM", Color.RED));
        }
        txtfldInput.clear();

    }
    private void SendSystemMessage(String message) throws RemoteException {
        User system = new User("SYSTEM", Color.RED);
        if(client !=null){
            client.server.broadcastMessage(system,message);
        }else{
            AppendToConsole(message,system);
        }
    }
    private List<UrField> CreateField(){
        List<UrField> rtnList = new ArrayList<>();
        rtnList.add(new UrField(imageDict.get("waves"),1,1));
        rtnList.add(new UrField(imageDict.get("waves"),3,1));
        rtnList.add(new UrField(imageDict.get("dots"),2,1));
        rtnList.add(new UrField(imageDict.get("dots"),2,4));
        rtnList.add(new UrField(imageDict.get("dots"),1,6));
        rtnList.add(new UrField(imageDict.get("dots"),3,6));
        rtnList.add(new UrField(imageDict.get("dots"),2,7));
        rtnList.add(new UrField(imageDict.get("eyes"),2,2));
        rtnList.add(new UrField(imageDict.get("eyes"),1,5));
        rtnList.add(new UrField(imageDict.get("eyes"),3,5));
        rtnList.add(new UrField(imageDict.get("eyes"),3,7));
        rtnList.add(new UrField(imageDict.get("eyes"),1,7));
        rtnList.add(new UrField(imageDict.get("rosette"),1,2));
        rtnList.add(new UrField(imageDict.get("rosette"),3,2));
        rtnList.add(new UrField(imageDict.get("rosette"),2,5));
        rtnList.add(new UrField(imageDict.get("rosette"),1,8));
        rtnList.add(new UrField(imageDict.get("rosette"),3,8));
        rtnList.add(new UrField(imageDict.get("dotwaves"),2,3));
        rtnList.add(new UrField(imageDict.get("dotwaves"),2,6));
        rtnList.add(new UrField(imageDict.get("dotgrids"),2,8));

        return rtnList;
    }

    public void AppendToConsole(String text, User user) {
        Text t1 = new Text();
        t1.setFill(user.getUsercolor());
        t1.setText(user.getUserName()+": "+ text + '\n');



        txtflwChatOutput.getChildren().add(t1);
    }


}
