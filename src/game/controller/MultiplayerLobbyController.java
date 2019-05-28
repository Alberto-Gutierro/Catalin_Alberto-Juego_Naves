package game.controller;

import game.SceneStageSetter;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
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

    boolean startedGame;
    boolean toLobby;

    public ImageView img_playerNave1, img_playerNave2, img_playerNave3, img_playerNave4;
    public Text playerName1, playerName2, playerName3, playerName4;

    private ImageView[] imagesNave;
    private Text[] textsNave;

    private DatagramPacket packet;

    private int idNave;
    private String idSala;


    private Executor executor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startedGame = false;
        toLobby = false;
        imagesNave = new ImageView[]{img_playerNave1, img_playerNave2, img_playerNave3, img_playerNave4};
        textsNave = new Text[]{playerName1, playerName2, playerName3, playerName4};
    }

    public void playGameServer(ActionEvent event) {
        try {
            startedGame = true;
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/game.fxml"));
            Parent root = loader.load();

            stage.setMaximized(true);

            scene = new Scene(root, stage.getWidth(), stage.getHeight());

            GameController gameController = loader.getController();
            gameController.beforeStartGame(stage, scene, idNave, idSala, gameController.getPane(), packet);
            gameController.start(true);

            stage.setScene(scene);
            stage.show();
            //executor
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void backToMainMenu(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/multiplayerLobby.fxml"));
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

    void setPacket(DatagramPacket packet, boolean owner) {
        try {
            if(owner){
                idNave = 1;
                idSala = Transformer.packetDataToString(packet);
            }else {
                idNave = Integer.parseInt(Transformer.packetDataToString(packet).split(":")[0]);
                idSala = Transformer.packetDataToString(packet).split(":")[1];
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //showNaves(packet);
        this.packet = packet;

        executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> {
            String señalServer = "";
            DatagramSocket socket = null;
            DatagramPacket packetWait;
            String message = null;
            message = "Waiting:" + idSala;
            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }

            do{
                try {
                    packetWait = new DatagramPacket(message.getBytes(),
                            message.getBytes().length,
                            packet.getAddress(),
                            packet.getPort());
                    socket.send(packetWait);

                    socket.receive(packetWait);
                    try {
                        Thread.sleep(750);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    señalServer = Transformer.packetDataToString(packetWait);

                    if(!señalServer.equals("Start")) {
                        showNaves(packetWait);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }while (!señalServer.equals("Start") && !startedGame && !toLobby);
        });
    }

    private void showNaves(DatagramPacket packet) {
        try {
            for (int i = 0; i < Integer.parseInt(Transformer.packetDataToString(packet)); i++) {
                if (i + 1 != idNave) {
                    textsNave[i].setText("Player " + (i + 1));
                    imagesNave[i].setImage(new Image("game/res/img/naves/navePlayer_" + (i + 1) + ".png"));
                } else {
                    textsNave[i].setText("You");
                    imagesNave[i].setImage(new Image("game/res/img/naves/navePlayer_" + (i + 1) + ".png"));
                }
            }
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    private void exitSala(){
        toLobby = true;
        //DatagramPacket exitPacket = new DatagramPacket();
    }

}
