package game.controller.pruebas;

import game.SceneStageSetter;
import game.controller.MainMenuController;
import game.controller.pruebas.MultiplayerSalasController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import statVars.Packets;
import statVars.Strings;
import transformmer.Transformer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

public class MultiplayerMenuController extends SceneStageSetter {
    private DatagramSocket socket;
    private DatagramPacket packet;
    private InetAddress ipServer;

    Alert alert;

    @FXML TextField et_ipServer;

    public void backToMainMenu(ActionEvent event) throws IOException {
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
    public void connectToServer(ActionEvent event) throws IOException {
        socket = new DatagramSocket();

        try{
            String ip = et_ipServer.getText().split(":")[0];//192.168.253.215:5568
            int port = Integer.parseInt(et_ipServer.getText().split(":")[1]);//5568

            System.out.println("IP: " + ip + "\nPORT: " + port);

            ipServer = InetAddress.getByName(ip);

            packet = new DatagramPacket("Connect".getBytes(), "Connect".getBytes().length, ipServer, port);

            socket.send(packet);

            socket.setSoTimeout(500);

            packet = new DatagramPacket(new byte[Packets.PACKET_LENGHT], Packets.PACKET_LENGHT);
            socket.receive(packet);

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/multiplayerSalas.fxml"));
            Parent root = loader.load();

            scene = new Scene(root, stage.getWidth(), stage.getHeight());

            MultiplayerSalasController multiplayerSalasController = loader.getController();
            multiplayerSalasController.setScene(scene);
            multiplayerSalasController.setStage(stage);
            multiplayerSalasController.setPacket(packet);

            stage.setScene(scene);
            stage.show();

        }catch (SocketTimeoutException e){
            //MOSTRAR UN DIALOG DICIENDO QUE EL SERVIDOR NO RESPONDE
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Server not found");
            alert.setContentText("Server not found, IP or Port are incorrect.");
            alert.showAndWait();

        }catch (ArrayIndexOutOfBoundsException e){
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ERROR");
            alert.setHeaderText("Format Error");
            alert.setContentText("This Ip format is Incorrect, example: 123.456.7.89:1234");
            alert.showAndWait();

        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
