package game.controller;

import javafx.fxml.Initializable;
import javafx.scene.image.Image;
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
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MultiplayerLobbyController extends SceneStageSetter implements Initializable {

    public ImageView img_playerNave1, img_playerNave2, img_playerNave3, img_playerNave4;
    public Text playerName1, playerName2, playerName3, playerName4;

    private ImageView[] imagesNave;
    private Text[] textsNave;

    private DatagramPacket packet;

    private int idNave;

    private Executor executor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imagesNave = new ImageView[]{img_playerNave1, img_playerNave2, img_playerNave3, img_playerNave4};
        textsNave = new Text[]{playerName1, playerName2, playerName3, playerName4};

        /*executor = Executors.newFixedThreadPool(4);

        executor.execute(() -> {
            String señalServer = "";
            do{

                try {
                    DatagramSocket socket = new DatagramSocket(packet.getPort(), packet.getAddress());

                    socket.receive(packet);

                    señalServer = Transformer.packetDataToString(packet);

                    showNaves(packet);

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            }while (!señalServer.equals("Start"));

        });*/
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

    private void showNaves(DatagramPacket packet) {
        try{
            idNave = Integer.parseInt(Transformer.packetDataToString(packet));
            for (int i = 0; i < idNave ; i++) {
                if(i != idNave-1) {
                    textsNave[i].setText("Player " + i+1);
                    imagesNave[i].setImage(new Image("game/res/img/naves/navePlayer_" + (i+1) + ".png"));
                }else {
                    textsNave[i].setText("You");
                    imagesNave[i].setImage(new Image("game/res/img/naves/navePlayer_" + (i+1) + ".png"));
                }
            }
        }catch (NumberFormatException e){
            e.printStackTrace();
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

}
