package game.controller.pruebas;

import game.SceneStageSetter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import server.model.SalaToSend;
import statVars.Packets;
import transformmer.Transformer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MultiplayerSalasController extends SceneStageSetter implements Initializable {

    Map<String, SalaToSend> salas;

    boolean entraSala;

    @FXML  private ScrollPane scrollPaneSalas;

    private DatagramPacket packet;

    //HBox salasScrollBox;

    @FXML Pane paneSalas;

    private Executor executor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        entraSala = false;

        salas = new HashMap<>();

        /*observableSalas = FXCollections.observableMap(salas);
        observableSalas.addListener((MapChangeListener<String, SalaToSend>) change -> {
            int height = 0;
            //salas = Transformer.jsonToMapSalas(Transformer.packetDataToString(packet));
            System.out.println("PEPEPPEPEPPE");
            //////////////////ESTO SE TIENE QUE METER EN UN OBSERVABLE QUE CUANDO SE MODIFIQUE LA VARIABLE SALAS HAGA ESO:
            for (SalaToSend sala:change.getMap().values()) {
                salasScrollBox.getChildren().add(new Rectangle(0, height,scrollPaneSalas.getMaxWidth(),50));
                height+=50;
            }
        });*/

    }

    void setPacket(DatagramPacket p) {
        executor = Executors.newFixedThreadPool(4);
        executor.execute(() -> {
            DatagramSocket socket = null;
            DatagramPacket packetWait;
            byte[] messageLength = new byte[Packets.PACKET_LENGHT];

            Platform.runLater(()->showSalas(p));

            try {
                socket = new DatagramSocket();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            try {
                do{
                    packetWait = new DatagramPacket("Rooms".getBytes(),
                            "Rooms".getBytes().length,
                            packet.getAddress(),
                            packet.getPort());
                    socket.send(packetWait);

                    packetWait = new DatagramPacket(messageLength, Packets.PACKET_LENGHT);
                    socket.receive(packetWait);
                    try {
                        Thread.sleep(750);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    final DatagramPacket packetToSend = packetWait;
                    Platform.runLater(() -> showSalas(packetToSend));
                }while (!entraSala);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.packet = p;
    }

    private void showSalas(DatagramPacket packetWait) {

        try {
            salas = Transformer.jsonToMapSalas(Transformer.packetDataToString(packetWait));
            //////////////////ESTO SE TIENE QUE METER EN UN OBSERVABLE QUE CUANDO SE MODIFIQUE LA VARIABLE SALAS HAGA ESO:

            paneSalas.getChildren().clear();
            int i = 0;
            for (SalaToSend sala:salas.values()) {
                Button button = new Button();
                button.setPrefWidth(scrollPaneSalas.getWidth()-2);
                button.setPrefHeight(50);
                button.setLayoutX(0);
                button.setLayoutY(50*i);
                button.setId(sala.getIdSala());
                button.setOnAction(event -> {
                    enterRoom(event);
                });
                button.setText("Sala " + (i+1));

                paneSalas.getChildren().add(button);

                i++;
            }
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    private void enterRoom(ActionEvent actionEvent){
        entraSala = true;
        DatagramSocket socket = null;
        DatagramPacket packetEnter;
        try {
            socket = new DatagramSocket();

            packetEnter = new DatagramPacket(("Room:" + ((Button)actionEvent.getSource()).getId()).getBytes(),
                    ("Room:" + ((Button)actionEvent.getSource()).getId()).getBytes().length,
                    packet.getAddress(),
                    packet.getPort());
            socket.send(packetEnter);

            packetEnter = new DatagramPacket(new byte[Packets.PACKET_LENGHT], Packets.PACKET_LENGHT);

            socket.receive(packetEnter);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/multiplayerLobby.fxml"));
            Parent root = loader.load();

            scene = new Scene(root, stage.getWidth(), stage.getHeight());

            MultiplayerLobbyController multiplayerLobbyController = loader.getController();
            multiplayerLobbyController.setScene(scene);
            multiplayerLobbyController.setStage(stage);
            multiplayerLobbyController.setPacket(packetEnter, false);

            stage.setScene(scene);
            stage.show();
        } catch (SocketException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void createRoom(ActionEvent actionEvent) {
        entraSala = true;

        DatagramSocket socket = null;
        DatagramPacket packetCreate;
        try {
            socket = new DatagramSocket();

            packetCreate = new DatagramPacket("Create".getBytes(), "Create".getBytes().length, packet.getAddress(), packet.getPort());

            socket.send(packetCreate);

            packetCreate = new DatagramPacket(new byte[Packets.PACKET_LENGHT], Packets.PACKET_LENGHT);

            socket.receive(packetCreate);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/multiplayerLobby.fxml"));
            Parent root = loader.load();

            scene = new Scene(root, stage.getWidth(), stage.getHeight());

            MultiplayerLobbyController multiplayerLobbyController = loader.getController();
            multiplayerLobbyController.setScene(scene);
            multiplayerLobbyController.setStage(stage);
            multiplayerLobbyController.setPacket(packetCreate, true);

            stage.setScene(scene);
            stage.show();

        } catch (SocketException e) {
            e.printStackTrace();
        }catch (IOException e) {
            e.printStackTrace();
        }


    }
}
