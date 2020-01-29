package com.smiletic.royalur;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static j2html.TagCreator.*;

public class StartScreen {

    @FXML
    private Button btnExit;

    @FXML
    private Button btnGenerateDoc;

    @FXML
    private void HandleButtonExit(ActionEvent event){
        Stage stage = (Stage) btnExit.getScene().getWindow();
    stage.close();
    }
    @FXML
    private void HandleButtonSingleplayer(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        FXMLLoader loader =new FXMLLoader(getClass().getResource("Game.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
    }
    @FXML
    private void HandleButtonMultiplayer(ActionEvent event) throws IOException {
        Stage stage = (Stage) btnExit.getScene().getWindow();
        FXMLLoader loader =new FXMLLoader(getClass().getResource("MultiplayerSetup.fxml"));
        Parent root = loader.load();
        stage.setScene(new Scene(root));
        stage.show();
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


}
