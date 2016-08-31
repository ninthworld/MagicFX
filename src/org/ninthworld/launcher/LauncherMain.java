package org.ninthworld.launcher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by NinthWorld on 4/14/2016.
 */
public class LauncherMain extends Application {

    private Scene scene;
    private TextField serverIpTextField, usernameTextField, passwordTextField;
    private Button connectBtn, hostServerBtn;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/org/ninthworld/launcher/launcher.fxml"));
        primaryStage.setTitle("MagicFX Launcher");
        double windowWidth = 400;
        double windowHeight = 240;
        primaryStage.setScene(new Scene(root, windowWidth, windowHeight));
        primaryStage.show();
        scene = primaryStage.getScene();

        serverIpTextField = (TextField) scene.lookup("#serverIpTextField");
        usernameTextField = (TextField) scene.lookup("#usernameTextField");
        passwordTextField = (TextField) scene.lookup("#passwordTextField");
        connectBtn = (Button) scene.lookup("#connectBtn");
        hostServerBtn = (Button) scene.lookup("#hostServerBtn");

        connectBtn.setOnAction(e -> {
            if(!serverIpTextField.getText().equals("") && !usernameTextField.getText().equals("") && !passwordTextField.getText().equals("")){
                try {
                    Process proc = Runtime.getRuntime().exec("java -jar MagicFX.jar --server=" + serverIpTextField.getText() + " --user=\"" + usernameTextField.getText() + "\" --pass=\"" + passwordTextField.getText().hashCode() + "\"");
                    //System.exit(0);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });

        hostServerBtn.setOnAction(e -> {
            try {
                // Runtime.getRuntime().exec(new String[] { "cmd.exe", "/c", "startserver.bat" } );
                File file = new File("startserver.bat");
                if(!file.exists()){
                    PrintWriter writer = new PrintWriter("startserver.bat", "UTF-8");
                    writer.print("java -jar MagicFXServer.jar");
                    writer.close();
                }
                Desktop.getDesktop().open(file);
                //System.exit(0);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
