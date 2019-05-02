package game.controller;

import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import game.SceneStageSetter;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import transformmer.Transformer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.URL;
import java.util.ResourceBundle;

public class MultiplayerLobbyController extends SceneStageSetter implements Initializable {

    public ImageView img_playerNave1, img_playerNave2, img_playerNave3, img_playerNave4;
    public Text playerName1, playerName2, playerName3, playerName4;
    private DatagramPacket packet;

    private int idNave;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void playGameServer(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/game.fxml"));
            Parent root = loader.load();

            scene = new Scene(root, stage.getWidth(), stage.getHeight());

            GameController gameController = loader.getController();
            gameController.beforeStartGame(stage, scene, idNave, gameController.getPane(), packet);
            gameController.start(true);

            stage.setScene(scene);
            stage.show();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void backToMainMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/multiplayerLobby.fxml"));
            Parent root = loader.load();

            scene = new Scene(root, stage.getWidth(), stage.getHeight());

            MainMenuController mainMenuController = loader.getController();
            mainMenuController.setScene(scene);
            mainMenuController.setStage(stage);

            stage.setTitle("Apolo X");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e){
               e.printStackTrace();
        }
    }

    void setPacket(DatagramPacket packet) {
        try {
            idNave = Integer.parseInt(Transformer.packetDataToString(packet));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        showNaves(packet);
        this.packet = packet;
    }

    private void showNaves(DatagramPacket paket) {

    }

}
