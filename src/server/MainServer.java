package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import statVars.Strings;

import java.io.IOException;

public class MainServer extends Application {
    public static void main(String[] args) {

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.setImplicitExit(true);
        primaryStage.setOnCloseRequest(event -> {

            //EN EL CASO DE QUE QUEDEN VENTANAS ABIERTAS POR ALGUÚN CASUAL AL HACER EL exit()
            //SE CERRARÁ LA APP DE JAVAFX
            Platform.exit();

            //CIERRA LA MAQUINA VIRTUAL DE JAVA QUE ESTÁ CORRIENDO ACTUALMENTE.
            System.exit(0);
        });
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/serverGame.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
        ServerGameController serverGame = loader.getController();
        try {
            serverGame.init(5568);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //CUANDO SE CIERRE LA ULTIMA VENTANA ABIERTA CERRARÁ EL SUBPROCESO DE LA APP DE JAVAFX
        Platform.setImplicitExit(true);
        primaryStage.setOnCloseRequest(event -> {

            //EN EL CASO DE QUE QUEDEN VENTANAS ABIERTAS POR ALGUÚN CASUAL AL HACER EL exit()
            //SE CERRARÁ LA APP DE JAVAFX
            Platform.exit();

            //CIERRA LA MAQUINA VIRTUAL DE JAVA QUE ESTÁ CORRIENDO ACTUALMENTE.
            System.exit(0);
        });

        primaryStage.setTitle(Strings.NOMBRE_JUEGO + " SERVER");
        primaryStage.getIcons().add(new Image("game/res/img/naves/navePlayerOriginal.png"));
        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
