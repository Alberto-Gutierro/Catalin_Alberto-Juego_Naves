package game.controller;

import game.GameSetter;
import game.model.Bullet;
import game.model.CollisionRectangle;
import game.model.toSend.DataToSend;
import game.services.MeteorService;
import game.services.ShipsRecivedService;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import statVars.Enums;
import statVars.Packets;
import statVars.Resoluciones;
import transformmer.Transformer;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;

public class GameController extends GameSetter implements Initializable {
    @FXML private Pane pane;

    //Datos que se mandan al servidor
    private DataToSend dataToSend;
    private ShipsRecivedService shipsRecivedService;
    private byte[] recivingData;
    private MeteorService meteorService;

    @FXML Canvas canvas;
    @FXML Text score_p1, score_p2, score_p3, score_p4, tv_ammo, tv_lifes;
    @FXML AnchorPane gameOverScreen;

    private CollisionRectangle areaObject1;
    private CollisionRectangle areaObject2;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        areaObject1 = new CollisionRectangle();
        areaObject2 = new CollisionRectangle();

        tv_ammo.setTextAlignment(TextAlignment.RIGHT);

        graphicsContext = canvas.getGraphicsContext2D();
        //graphicsContext.scale(1,1);

        dataToSend = new DataToSend();

        recivingData = new byte[Packets.PACKET_LENGHT];

    }

    public Pane getPane(){
        return pane;
    }

    @Override
    public void setStage(Stage stage) {
        super.setStage(stage);
        canvas.setHeight(stage.getHeight());
        canvas.setWidth(stage.getWidth());
    }

    void start(boolean isMultiplayer){
        this.isMultiplayer = isMultiplayer;

        if(isMultiplayer){
            try {
                startMultiplayer();

            } catch (SocketException e) {
                e.printStackTrace();
            }
        }else {
            startSigle();
        }
    }

    private double anteriorCurrentNanoTime = 0;

    private double timingMeteor = 0;

    private double dificulty = 1;

    private static final double ONE_FRAME_DURATION = 0.0153; // This is for 60 fps.

    private void startSigle(){
        runningGame = true;

        meteorService = new MeteorService(scene.getWidth(),scene.getHeight(),graphicsContext);

        score_p1.setText("0");

        new AnimationTimer() {
            public void handle(long currentNanoTime)
            {
                // Si la ship esta muerta acaba la partida
                if (ship.getState().equals(Enums.ShipState.DEAD)) runningGame=false;

                double timing = (currentNanoTime-anteriorCurrentNanoTime)*Math.pow(10, -9);

                if (timing >= ONE_FRAME_DURATION) {
                    timingMeteor += timing;
                    anteriorCurrentNanoTime = currentNanoTime;

                    if( timingMeteor*(dificulty/6+1) >= 1) {
                        meteorService.create(ship.getPosX()+(ship.getImagenRotada().getWidth())/2, ship.getPosY()+(ship.getImagenRotada().getHeight())/2, 2+(dificulty));
                        timingMeteor = 0;
                    }

                    ship.update(timing);
                    meteorService.update();

                    checkCollisions();

                    graphicsContext.clearRect(0,0, stage.getWidth(), stage.getHeight());

                    ship.render();
                    meteorService.render();


                    if(!runningGame){

                        this.stop();
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/gameOver.fxml"));

                            loader.load();

                            GameOverController gameController = loader.getController();
                            gameController.setScene(scene);
                            gameController.setStage(stage);
                            gameController.setScore(score_p1.getText());

                            gameOverScreen.getChildren().add(loader.getRoot());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.start();
    }

    //NAVE AZUL: #3c42d8
    //NAVE VERDE: #3cd846
    //NAVE ROJA: #d83c3c
    //NAVE ROSA: #d43cd8
    private void startMultiplayer() throws SocketException {
        runningGame = true;

        DatagramSocket socket = new DatagramSocket();
        shipsRecivedService = new ShipsRecivedService(graphicsContext, ship.getId(), score_p1, score_p2, score_p3, score_p4);

        //POR AQUI: AL COMENZAR EL JUEGO EN LINEA QUE HAGA ALL LO QUE TENGA QUE HACER
        new AnimationTimer() {
            public void handle(long currentNanoTime)
            {
                double timing = (currentNanoTime-anteriorCurrentNanoTime)*Math.pow(10, -9);
                if (timing >= ONE_FRAME_DURATION) {
                    anteriorCurrentNanoTime = currentNanoTime;

                    graphicsContext.clearRect(0, 0, stage.getWidth(), stage.getHeight());

                    dataToSend.setData(ship, timing, idSala);
                    //dataToSend.getShipWeaponBullets().forEach(bulletToSend -> System.out.println(bulletToSend.getAngle()));

                    String sendData = Transformer.classToJson(dataToSend);
                    packet = new DatagramPacket(sendData.getBytes(),
                            sendData.getBytes().length,
                            ipServer,
                            portServer);

                    try {
                        socket.send(packet);
                        socket.setSoTimeout(1000);
                        packet = new DatagramPacket(recivingData, Packets.PACKET_LENGHT);
                        socket.receive(packet);

                        if (Transformer.packetDataToString(packet).equals("FinishGame")) {
                            this.stop();

                            //POR AQUI: Hacer que cambie de sala al acabar
                            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/multiplayerLobby.fxml"));
                            Parent root = loader.load();

                            scene = new Scene(root, stage.getWidth(), stage.getHeight());

                            MultiplayerLobbyController multiplayerLobbyController = loader.getController();
                            multiplayerLobbyController.setScene(scene);
                            multiplayerLobbyController.setStage(stage);
                            multiplayerLobbyController.setPacket(new DatagramPacket((ship.getId() + ":" + idSala).getBytes(), (ship.getId() + ":" + idSala).getBytes().length, ipServer, portServer));

                            stage.setScene(scene);
                            stage.show();
                        } else {
                            shipsRecivedService.setShipsRecived(Transformer.jsonToArrayListShips(Transformer.packetDataToString(packet)));

                            shipsRecivedService.renderShipsRecibidas();

                            ship.setState(shipsRecivedService.getMyState());

                            ship.setLifes(shipsRecivedService.getMyLifes());

                            if (ship.getState() != Enums.ShipState.DEAD) {
                                ship.update(timing);

                                checkCollisions();

                                ship.render();
                            } else {
                                ship.render();
                                runningGame = false;
                                multiplayerSpectatorMode(shipsRecivedService, socket);
                                this.stop();
                            }


                        }
                    } catch (SocketTimeoutException e) {
                        this.stop();

                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/multiplayerMenu.fxml"));
                            Parent root = loader.load();

                            scene = new Scene(root, stage.getWidth(), stage.getHeight());

                            MultiplayerMenuController multiplayerMenuController = loader.getController();
                            multiplayerMenuController.setScene(scene);
                            multiplayerMenuController.setStage(stage);

                            stage.setScene(scene);
                            stage.show();

                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("ERROR");
                            alert.setHeaderText("Connection Time Out");
                            alert.showAndWait();
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private void multiplayerSpectatorMode(ShipsRecivedService shipsRecivedService, DatagramSocket socket){
        String message = "Dead:" + idSala;
        new AnimationTimer(){

            @Override
            public void handle(long l) {
                try {
                    packet = new DatagramPacket(message.getBytes(),
                            message.getBytes().length,
                            ipServer,
                            portServer);
                    socket.send(packet);
                    socket.setSoTimeout(1000);
                    packet = new DatagramPacket(recivingData, Packets.PACKET_LENGHT);
                    socket.receive(packet);
                    if(Transformer.packetDataToString(packet).equals("FinishGame")){
                        this.stop();

                        //POR AQUI: Hacer que cambie de sala al acabar
                        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("game/fxml/multiplayerLobby.fxml"));
                        Parent root = loader.load();

                        scene = new Scene(root, stage.getWidth(), stage.getHeight());

                        MultiplayerLobbyController multiplayerLobbyController = loader.getController();
                        multiplayerLobbyController.setScene(scene);
                        multiplayerLobbyController.setStage(stage);
                        multiplayerLobbyController.setPacket(new DatagramPacket((ship.getId() + ":" + idSala).getBytes(), (ship.getId() + ":" + idSala).getBytes().length,ipServer,portServer));

                        stage.setScene(scene);
                        stage.show();
                    }else {
                        graphicsContext.clearRect(0, 0, stage.getWidth(), stage.getHeight());
                        shipsRecivedService.setShipsRecived(Transformer.jsonToArrayListShips(Transformer.packetDataToString(packet)));

                        shipsRecivedService.renderShipsRecibidas();

                    }

                } catch (SocketTimeoutException e){
                    this.stop();

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
                    Platform.runLater(()->{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("ERROR");
                        alert.setHeaderText("Connection Time Out");
                        alert.showAndWait();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void checkCollisions() {
        checkShipInScreen();
        checkCollisionBullet();
        if (!isMultiplayer) {
            checkCollisionMeteor();
        }
    }

    private void checkCollisionMeteor() {
        //Se puede juntar el contenido de este método y el de checkCollisionBullet
        meteorService.getMeteors().forEach(meteor -> {
            if(meteor.getPosX() < 0 - Resoluciones.LINEA_DESTRUCCION){
                meteor.remove();
            }else if(meteor.getPosX() > stage.getWidth() + Resoluciones.LINEA_DESTRUCCION){
                meteor.remove();
            }
            if(meteor.getPosY() < 0 - Resoluciones.LINEA_DESTRUCCION){
                meteor.remove();
            }else if(meteor.getPosY() > stage.getHeight() + Resoluciones.LINEA_DESTRUCCION){
                meteor.remove();
            }

            areaObject1.setCollisions(
                    (int) meteor.getPosX(),
                    (int) meteor.getPosY(),
                    (int) meteor.getImgMeteorRotada().getWidth(),
                    (int) meteor.getImgMeteorRotada().getHeight()
            );


            for (Bullet bullet:ship.getWeapon().getBullets()) {
                areaObject2.setCollisions(
                        (int)bullet.getPosX(),
                        (int)bullet.getPosY(),
                        (int)bullet.getImagenRotada().getWidth(),
                        (int)bullet.getImagenRotada().getHeight()
                );

                if(areaObject1.intersects(areaObject2) && meteor.getState().equals(Enums.MeteorState.MOVING)){
                    bullet.remove();
                    meteor.remove();
                    score_p1.setText(String.valueOf(Integer.parseInt(score_p1.getText()) + 50));
                    if(Integer.parseInt(score_p1.getText())%500 == 0 && ship.getLifes() != 5){
                        ship.addLive();
                    }
                    dificulty += 0.5;
                }
            }
            areaObject2.setCollisions(
                    (int)ship.getPosX(),
                    (int)ship.getPosY(),
                    (int)ship.getImagenRotada().getWidth(),
                    (int)ship.getImagenRotada().getHeight()
            );
            if(areaObject1.intersects(areaObject2) && meteor.getState().equals(Enums.MeteorState.MOVING)){
                meteor.remove();
                ship.subsLive();
                if(ship.getLifes() <= 0){
                    ship.setState(Enums.ShipState.DYING);

                }
            }
        });
    }

    private void checkCollisionBullet() {
        dataToSend.clearIdShipTocada();
        ship.getWeapon().getBullets().forEach(bullet -> {
            if(bullet.getPosX() < 0){
                bullet.remove();
            }else if(bullet.getPosX() > stage.getWidth()){
                bullet.remove();
            }
            if(bullet.getPosY() < 0){
                bullet.remove();
            }else if(bullet.getPosY() > stage.getHeight()){
                bullet.remove();
            }
        });

        if(isMultiplayer) {
            Image[] imagenRotadaOtrasShips = shipsRecivedService.getImagenRotadaOtrasShips();
            shipsRecivedService.getShipsRecived().forEach(shipRecivedService -> {
                if (shipRecivedService.getIdShip() != ship.getId() && (shipRecivedService.getState() != Enums.ShipState.DEAD && shipRecivedService.getState() != Enums.ShipState.DYING)) {
                    areaObject1.setCollisions(
                            (int) shipRecivedService.getShipPosX(),
                            (int) shipRecivedService.getShipPosY(),
                            (int) imagenRotadaOtrasShips[shipRecivedService.getIdShip()].getWidth(),
                            (int) imagenRotadaOtrasShips[shipRecivedService.getIdShip()].getHeight()
                    );

                    ship.getWeapon().getBullets().forEach(bullet -> {
                        areaObject2.setCollisions(
                                (int) bullet.getPosX(),
                                (int) bullet.getPosY(),
                                (int) bullet.getImagenRotada().getWidth(),
                                (int) bullet.getImagenRotada().getHeight()
                        );
                        if (areaObject1.intersects(areaObject2)) {
                            bullet.remove();
                            ship.addScore(50);
                            dataToSend.addIdShipTocada(shipRecivedService.getIdShip());
                        }
                    });
                }
            });
        }
    }
//HACER QUE SE GUARDE EL ID DE LA NAVE QUE HA SIDO TOCADA EN LOS DATOS QUE VAMOS A MANDAR AL SERVIDOR.

    // Añadimos la las id de las ships que han sido tocadas por tus bullets
    private void checkShipInScreen() {
        if(ship.getPosX() < 0){
            ship.setPosX(0);
        }else if(ship.getPosX() + ship.getImgShip().getImage().getWidth() + Resoluciones.AJUSTAR_PANTALLA_X > stage.getWidth()){
            ship.setPosX(stage.getWidth() - ship.getImgShip().getImage().getWidth() - Resoluciones.AJUSTAR_PANTALLA_X);
        }
        if(ship.getPosY() < 0){
            ship.setPosY(0);
        }else if(ship.getPosY() + ship.getImgShip().getImage().getHeight() + Resoluciones.AJUSTAR_PANTALLA_Y > stage.getHeight()){
            ship.setPosY(stage.getHeight() - ship.getImgShip().getImage().getHeight() - Resoluciones.AJUSTAR_PANTALLA_Y);
        }
    }
}