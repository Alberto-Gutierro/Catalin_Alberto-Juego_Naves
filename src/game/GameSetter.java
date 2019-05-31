package game;

import game.model.Ship;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import statVars.Enums;
import transformmer.Transformer;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.InetAddress;

public abstract class GameSetter extends SceneStageSetter {

    protected GraphicsContext graphicsContext;

    protected boolean isMultiplayer;

    //Si se ha pulsado alguna.
    protected BooleanBinding anyPressed;

    //Teclas a pulsar
    protected BooleanProperty leftPressed, rightPressed, upPressed, downPressed;

    protected Ship ship;

    protected InetAddress ipServer;

    protected int portServer;

    protected DatagramPacket packet;

    protected boolean runningGame;

    protected String idSala;

    public void beforeStartGame(Stage stage, Scene scene, int idShip, String idSala, Pane pane, DatagramPacket packet) {
        this.idSala = idSala;

        this.packet = packet;


        /*int idShip = 1;
        if(packet != null) {
            try {
                idShip = Integer.parseInt(Transformer.packetDataToString(packet));
                System.out.println(idShip);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }*/

        setScene(scene);

        setStage(stage);

        initControlPress();

        setShip(new Ship(graphicsContext, pane,idShip, new ImageView("game/res/img/ships/shipPlayer_" + idShip + ".png"), this.upPressed, this.downPressed, this.rightPressed, this.leftPressed, this.anyPressed));

        setControls();

        if(packet != null) {
            setIpServer(packet);
        }
    }

    private void initControlPress(){
        rightPressed = new SimpleBooleanProperty();
        leftPressed = new SimpleBooleanProperty();
        upPressed = new SimpleBooleanProperty();
        downPressed = new SimpleBooleanProperty();
        anyPressed = upPressed.or(downPressed).or(leftPressed).or(rightPressed);
    }

    private void setShip(Ship ship){
        this.ship = ship;
        ship.setImagenRotada(ship.getImgShip().getImage());
    }

    private void setIpServer(DatagramPacket packet) {
        this.ipServer = packet.getAddress();
        this.portServer = packet.getPort();
    }

    private void setControls() {
        scene.setOnMouseReleased(event->{
            if(runningGame && ship.getState() != Enums.ShipState.DYING) { // La ship no puede disparar si se esta muriendo
                ship.shoot(event.getX(), event.getY());
            }
        });

        scene.setOnMouseDragged(event->{
            if(runningGame) {
                ship.setOrientation(event.getX(), event.getY());
            }
        });
        scene.setOnMouseMoved(event->{
            if(runningGame) {
                ship.setOrientation(event.getX(), event.getY());
            }
        });
        scene.setOnKeyPressed(key -> {
            if (key.getCode() == KeyCode.UP || key.getCode() == KeyCode.W) {
                upPressed.set(true);
            }
            if (key.getCode() == KeyCode.DOWN || key.getCode() == KeyCode.S) {
                downPressed.set(true);
            }
            if (key.getCode() == KeyCode.LEFT || key.getCode() == KeyCode.A) {
                leftPressed.set(true);
            }
            if (key.getCode() == KeyCode.RIGHT || key.getCode() == KeyCode.D) {
                rightPressed.set(true);
            }
        });

        scene.setOnKeyReleased(key -> {
            if (key.getCode() == KeyCode.UP || key.getCode() == KeyCode.W) {
                upPressed.set(false);
            }
            if (key.getCode() == KeyCode.DOWN || key.getCode() == KeyCode.S) {
                downPressed.set(false);
            }
            if (key.getCode() == KeyCode.LEFT || key.getCode() == KeyCode.A) {
                leftPressed.set(false);
            }
            if (key.getCode() == KeyCode.RIGHT || key.getCode() == KeyCode.D) {
                rightPressed.set(false);
            }
        });
    }
}
