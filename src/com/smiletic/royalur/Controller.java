package com.smiletic.royalur;

import com.smiletic.royalur.Model.UrField;
import com.smiletic.royalur.Model.UrToken;
import com.smiletic.royalur.Model.User;
import com.smiletic.royalur.RMI.Client;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static j2html.TagCreator.*;

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
    Button btnConnectToChat;

    @FXML
    TextFlow txtflwChatOutput;

    @FXML
    TextField txtfldInput;

    @FXML
    private Button btnSave;
    @FXML
    private Button btnRoll;

    @FXML
    private Button btnGenerateDoc;

    @FXML
    private void HandleBtnConnectToServer(ActionEvent event){
        try {
            client= new Client(user,this);
            client.startClient();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void HandlebtnHenerateDocAction(ActionEvent event){
        List<Class<?>> listOfClasses = getClassesInPackage("com.smiletic.royalur.Model");
        for (Class<?> currentClass: listOfClasses
             ) {
            List<Field> classfields = Arrays.asList(currentClass.getDeclaredFields());
            List<Method> classMethods = Arrays.asList(currentClass.getDeclaredMethods());
            List<Parameter> methodParameters = new ArrayList<>();

           try(FileWriter writer = new FileWriter(currentClass.getName()+".html")){
               html(
                       head(
                               title(currentClass.getName())
                       ),
                       body(
                               h1(currentClass.getName()),
                               h2("FIELDS"),
                               each(classfields,field ->
                                       div(attrs(".field"),
                                               b(field.getType()+ " "),
                                               i(field.getName())
                                               )
                                       ),
                                h2("METHODS"),
                               each(classMethods,method ->
                                       div(attrs(".methods"),
                                               b(method.getReturnType().getName() +" "),
                                               text(method.getName()+" "),
                                               each(Arrays.asList(method.getParameters()),parameter ->
                                                       i(parameter.toString())
                                                       )

                                               )
                                       )
                       )

               ).render(writer);
           } catch (IOException e) {
               e.printStackTrace();
           }

        }
    }

    @FXML
    private void HandlebtnRollAction(ActionEvent event){
        LabelList.forEach(x-> {
            int i=rand.nextInt(2);
            x.setText(i==0 ? "X" : "O");

        } );
    }
    @FXML
    private void HandlebtnSaveAction(ActionEvent event){
        try {
            int counter = 0;
            for (UrField field:fields) {
                FileOutputStream fileOut = new FileOutputStream("Field" +counter+".ser");
                ObjectOutputStream out = new ObjectOutputStream(fileOut);
                out.writeObject(field);
                out.close();
                fileOut.close();
                counter++;
            }
        }
        catch (IOException i)
        {
            i.printStackTrace();
        }
    }


    private static final int WIDTH = 50;

    private static final int BLUETEAMINT = 1;
    private static final int REDTEAMINT = 2;
    private static Client client;
    private static User user;

    Map<String,Image> imageDict = new Hashtable<>();
    Stage mainstage;
    List<UrField> fields;

    @FXML
    public void initialize() {

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



        try {
            fields=new ArrayList<>();
            for (int i =0;i<20;i++){
                FileInputStream fileIn = new FileInputStream("Field"+i+".ser");
                ObjectInputStream in = new ObjectInputStream(fileIn);
                fields.add((UrField)in.readObject());
                in.close();
                fileIn.close();
            }
            for (UrField field:fields) {
                gameGrid.add(field,field.getX(),field.getY());
            }
        }catch (FileNotFoundException ex){
            fields = CreateField();
            for (UrField field:fields) {
                gameGrid.add(field,field.getX(),field.getY());
            }
        }catch (IOException i){
            i.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }


        UrToken BlueTokenSpawn = new UrToken(0,0,imageDict.get("bluecounter"),BLUETEAMINT);
        UrToken RedTokenSpawn = new UrToken(4,0,imageDict.get("redcounter"),REDTEAMINT);
        gameGrid.add(BlueTokenSpawn,BlueTokenSpawn.getX(),BlueTokenSpawn.getY());
        gameGrid.add(RedTokenSpawn,RedTokenSpawn.getX(),RedTokenSpawn.getY());






    }

    @FXML
    private void HandletxtInputSubmitMessage(ActionEvent events) throws RemoteException {
        if(client !=null){
            client.server.broadcastMessage(user,txtfldInput.getText());
        }else{
            AppendToConsole("Establish a connection first",new User("SERVER", Color.RED));
        }
        txtfldInput.clear();

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

    public static final List<Class<?>> getClassesInPackage(String packageName) {
        String path = packageName.replace(".", File.separator);
        List<Class<?>> classes = new ArrayList<>();
        String[] classPathEntries = System.getProperty("java.class.path").split(
                System.getProperty("path.separator")
        );

        String name;
        for (String classpathEntry : classPathEntries) {
            if (classpathEntry.endsWith(".jar")) {
                File jar = new File(classpathEntry);
                try {
                    JarInputStream is = new JarInputStream(new FileInputStream(jar));
                    JarEntry entry;
                    while((entry = is.getNextJarEntry()) != null) {
                        name = entry.getName();
                        if (name.endsWith(".class")) {
                            if (name.contains(path) && name.endsWith(".class")) {
                                String classPath = name.substring(0, entry.getName().length() - 6);
                                classPath = classPath.replaceAll("[\\|/]", ".");
                                classes.add(Class.forName(classPath));
                            }
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                }
            } else {
                try {
                    File base = new File(classpathEntry + File.separatorChar + path);
                    for (File file : base.listFiles()) {
                        name = file.getName();
                        if (name.endsWith(".class")) {
                            name = name.substring(0, name.length() - 6);
                            classes.add(Class.forName(packageName + "." + name));
                        }
                    }
                } catch (Exception ex) {
                    // Silence is gold
                }
            }
        }

        return classes;
    }
    public void AppendToConsole(String text, User user) {
        Text t1 = new Text();
        t1.setFill(user.getUsercolor());
        t1.setText(user.getUserName()+": "+ text + '\n');



        txtflwChatOutput.getChildren().add(t1);
    }


}
