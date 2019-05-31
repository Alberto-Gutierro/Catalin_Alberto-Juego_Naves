package game;

import game.controller.MainMenuController;
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

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/mainMenu.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());
            MainMenuController controller = loader.getController();
            controller.setStage(primaryStage);
            controller.setScene(scene);

            //CUANDO SE CIERRE LA ULTIMA VENTANA ABIERTA CERRARÁ EL SUBPROCESO DE LA APP DE JAVAFX
            Platform.setImplicitExit(true);
            primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {

                    //EN EL CASO DE QUE QUEDEN VENTANAS ABIERTAS POR ALGUÚN CASUAL AL HACER EL exit()
                    //SE CERRARÁ LA APP DE JAVAFX
                    Platform.exit();

                    //CIERRA LA MAQUINA VIRTUAL DE JAVA QUE ESTÁ CORRIENDO ACTUALMENTE.
                    System.exit(0);
                }
            });

            primaryStage.setTitle(Strings.NOMBRE_JUEGO);
            primaryStage.getIcons().add(new Image("game/res/img/ships/shipPlayerOriginal.png"));
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
