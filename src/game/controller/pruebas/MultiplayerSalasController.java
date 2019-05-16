package game.controller.pruebas;

import game.SceneStageSetter;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.shape.Rectangle;
import server.model.Sala;
import server.model.SalaToSend;
import statVars.Packets;
import transformmer.Transformer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MultiplayerSalasController extends SceneStageSetter implements Initializable {


    public ScrollPane scrollPaneSalas;
    private DatagramPacket packet;

    private ThreadPoolExecutor executor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    void setPacket(DatagramPacket p) {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        executor.execute(() -> {
            DatagramSocket socket = null;
            DatagramPacket packetWait;
            byte[] messageLength = new byte[Packets.PACKET_LENGHT];
            try {
                System.out.println(Transformer.packetDataToString(p));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            showSalas(p);
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

                    showSalas(packetWait);
                }while (true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        this.packet = p;
    }

    private void showSalas(DatagramPacket packetWait) {
        try {
            int height = 0;
            Map<String, SalaToSend> salas = Transformer.jsonToMapSalas(Transformer.packetDataToString(packet));

            for (SalaToSend sala:salas.values()) {
                scrollPaneSalas.getChildrenUnmodifiable().add(new Rectangle(0, height,scrollPaneSalas.getMaxWidth(),50));
                height+=50;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void createRoom(ActionEvent actionEvent) {
        executor.shutdown();
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
