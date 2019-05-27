package game.controller.pruebas;

import game.SceneStageSetter;
import game.controller.MainMenuController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import statVars.Strings;

import java.io.*;
import java.net.URLDecoder;

public class GameOverController extends SceneStageSetter {

    @FXML Text tv_score;
    @FXML Text tv_gameOver;

    void setScore(String string){
        tv_score.setText("Score " + string);

        try {

            tv_gameOver.setFont(Font.loadFont(new FileInputStream(new File(URLDecoder.decode(getClass().getClassLoader().getResource("game/res/fonts/arcadeClassic.TTF").getFile(), "UTF-8"))), 80));

            tv_score.setFont(Font.loadFont(new FileInputStream(new File(URLDecoder.decode(getClass().getClassLoader().getResource("game/res/fonts/arcadeClassic.TTF").getFile(), "UTF-8"))), 40));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


    public void exitGame(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/mainMenu.fxml"));
            Parent root = loader.load();

            scene = new Scene(root, stage.getWidth(), stage.getHeight());

            MainMenuController mainMenuController = loader.getController();
            mainMenuController.setScene(scene);
            mainMenuController.setStage(stage);

            stage.setTitle(Strings.NOMBRE_JUEGO);
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restartGame(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/game.fxml"));
            Parent root = loader.load();

            scene = new Scene(root, stage.getWidth(), stage.getHeight());

            GameController gameController = loader.getController();
            gameController.beforeStartGame(stage,scene, 1, "",  gameController.getPane(), null);
            gameController.start(false);

            stage.setTitle(Strings.NOMBRE_JUEGO);
            stage.setScene(scene);
            //stage.setMaximized(true);
            stage.show();

        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
