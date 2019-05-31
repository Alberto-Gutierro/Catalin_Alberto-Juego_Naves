package game.controller;

import game.SceneStageSetter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import statVars.Packets;
import transformmer.Transformer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MultiplayerLobbyController extends SceneStageSetter implements Initializable {

    private boolean startedGame;
    private boolean exitSala;

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
        exitSala = false;
        imagesNave = new ImageView[]{null, img_playerNave1, img_playerNave2, img_playerNave3, img_playerNave4};
        textsNave = new Text[]{null, playerName1, playerName2, playerName3, playerName4};
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
            try {
                do{
                    packetWait = new DatagramPacket(message.getBytes(),
                            message.getBytes().length,
                            packet.getAddress(),
                            packet.getPort());
                    socket.send(packetWait);

                    socket.setSoTimeout(1000);

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
                }while (!señalServer.equals("Start") && !startedGame && !exitSala);
            } catch (SocketTimeoutException e) {
                Platform.runLater(()->{
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/multiplayerMenu.fxml"));
                        Parent root = loader.load();

                        scene = new Scene(root, stage.getWidth(), stage.getHeight());

                        MultiplayerMenuController multiplayerMenuController = loader.getController();
                        multiplayerMenuController.setScene(scene);
                        multiplayerMenuController.setStage(stage);

                        stage.setScene(scene);
                        stage.show();

                    } catch (IOException ex){
                        ex.printStackTrace();
                    }

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setHeaderText("Connection Time Out");
                    alert.showAndWait();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void showNaves(DatagramPacket packet) {
        try {
            boolean[] connectedPersons = Transformer.jsonToBooleanArray(Transformer.packetDataToString(packet));

            for (int i = 1; i < connectedPersons.length; i++) {
                if(connectedPersons[i] && i == idNave){
                    textsNave[i].setText("You");
                    imagesNave[i].setImage(new Image("game/res/img/naves/navePlayer_" + i + ".png"));
                }else if(connectedPersons[i]){
                    textsNave[i].setText("Player " + i);
                    imagesNave[i].setImage(new Image("game/res/img/naves/navePlayer_" + i + ".png"));
                }else {
                    textsNave[i].setText("Waiting...");
                    imagesNave[i].setImage(new Image("game/res/img/naves/navePlayerWaiting.png"));
                }
            }

//            for (int i = 0; i < Integer.parseInt(Transformer.packetDataToString(packet)); i++) {
//                if (i + 1 != idNave) {
//                    textsNave[i].setText("Player " + (i + 1));
//                    imagesNave[i].setImage(new Image("game/res/img/naves/navePlayer_" + (i + 1) + ".png"));
//                } else {
//                    textsNave[i].setText("You");
//                    imagesNave[i].setImage(new Image("game/res/img/naves/navePlayer_" + (i + 1) + ".png"));
//                }
//            }
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void exitSala(){
        exitSala = true;
        DatagramSocket socket = null;
        DatagramPacket packetExitSala;


        String message = "Exit:" + idSala;

        try {
            socket = new DatagramSocket();

            packetExitSala = new DatagramPacket(message.getBytes(),
                    message.getBytes().length,
                    packet.getAddress(),
                    packet.getPort());
            socket.send(packetExitSala);

            packetExitSala = new DatagramPacket(new byte[Packets.PACKET_LENGHT], Packets.PACKET_LENGHT);

            socket.receive(packetExitSala);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/multiplayerSalas.fxml"));
            Parent root = loader.load();

            scene = new Scene(root, stage.getWidth(), stage.getHeight());

            MultiplayerSalasController multiplayerSalasController = loader.getController();
            multiplayerSalasController.setScene(scene);
            multiplayerSalasController.setStage(stage);
            multiplayerSalasController.setPacket(packet);

            stage.setScene(scene);
            stage.show();

        } catch (SocketException e) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/mainMenu.fxml"));
                Parent root = loader.load();

                scene = new Scene(root, stage.getWidth(), stage.getHeight());

                MainMenuController mainMenuController = loader.getController();
                mainMenuController.setScene(scene);
                mainMenuController.setStage(stage);

                stage.setScene(scene);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
