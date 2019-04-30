package game.controller;

import javafx.scene.text.Font;
import statVars.Strings;
import game.SceneStageSetter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class GameOverController extends SceneStageSetter {

    @FXML Text tv_score;
    @FXML Text tv_gameOver;

    void setScore(String string){
        tv_score.setText("Score " + string);

        try {

            tv_gameOver.setFont(Font.loadFont(new FileInputStream(new File("src/game/res/fonts/arcadeClassic.TTF")), 80));

            tv_score.setFont(Font.loadFont(new FileInputStream(new File("src/game/res/fonts/arcadeClassic.TTF")), 40));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    public void exitGame(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/mainMenu.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/game.fxml"));
            Parent root = loader.load();

            scene = new Scene(root, stage.getWidth(), stage.getHeight());

            GameController gameController = loader.getController();
            gameController.beforeStartGame(stage,scene, null, gameController.getPane());
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
