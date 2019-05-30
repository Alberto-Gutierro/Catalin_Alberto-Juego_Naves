package game.controller;

import game.GameSetter;
import game.model.Bala;
import game.model.CollisionRectangle;
import game.model.toSend.DataToSend;
import game.services.MeteorService;
import game.services.NavesRecivedService;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import statVars.Enums;
import statVars.Packets;
import statVars.Resoluciones;
import transformmer.Transformer;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Map;
import java.util.ResourceBundle;

public class GameController extends GameSetter implements Initializable {
    @FXML private Pane pane;

    //Datos que se mandan al servidor
    private DataToSend dataToSend;
    private NavesRecivedService navesRecivedService;
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

    private void startSigle(){
        runningGame = true;

        meteorService = new MeteorService(scene.getWidth(),scene.getHeight(),graphicsContext);

        score_p1.setText("0");

        new AnimationTimer() {
            public void handle(long currentNanoTime)
            {
                // Si la nave esta muerta acaba la partida
                if (nave.getState().equals(Enums.NaveState.DEATH)) runningGame=false;

                double timing = (currentNanoTime-anteriorCurrentNanoTime)*Math.pow(10, -9);
                if(anteriorCurrentNanoTime == 0){
                    anteriorCurrentNanoTime = currentNanoTime;
                }
                timingMeteor += timing;
                anteriorCurrentNanoTime = currentNanoTime;

                if( timingMeteor*(dificulty/6+1) >= 1) {
                    meteorService.create(nave.getPosX()+(nave.getImagenRotada().getWidth())/2, nave.getPosY()+(nave.getImagenRotada().getHeight())/2, 2+(dificulty));
                    timingMeteor = 0;
                }

                nave.update(timing);
                meteorService.update();

                checkCollisions();

                graphicsContext.clearRect(0,0, stage.getWidth(), stage.getHeight());

                nave.render();
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
        }.start();
    }

    //NAVE AZUL: #3c42d8
    //NAVE VERDE: #3cd846
    //NAVE ROJA: #d83c3c
    //NAVE ROSA: #d43cd8
    private void startMultiplayer() throws SocketException {
        runningGame = true;

        DatagramSocket socket = new DatagramSocket();
        navesRecivedService = new NavesRecivedService(graphicsContext, nave.getId(), score_p1, score_p2, score_p3, score_p4);

        //POR AQUI: AL COMENZAR EL JUEGO EN LINEA QUE HAGA ALL LO QUE TENGA QUE HACER
        new AnimationTimer() {
            public void handle(long currentNanoTime)
            {
                double timing = (currentNanoTime-anteriorCurrentNanoTime)*Math.pow(10, -9);
                if(anteriorCurrentNanoTime == 0){
                    anteriorCurrentNanoTime = currentNanoTime;
                }
                anteriorCurrentNanoTime = currentNanoTime;

                graphicsContext.clearRect(0,0, stage.getWidth(), stage.getHeight());

                dataToSend.setData(nave, timing, idSala);
                //dataToSend.getNaveArmaBalas().forEach(balaToSend -> System.out.println(balaToSend.getAngle()));

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

                    navesRecivedService.setNavesRecived(Transformer.jsonToArrayListNaves(Transformer.packetDataToString(packet)));

                    navesRecivedService.renderNavesRecibidas();

                    nave.setState(navesRecivedService.getMyState());
                    nave.setLifes(navesRecivedService.getMyLifes());
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
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                if(nave.getState() != Enums.NaveState.DEATH) {
                    nave.update(timing);

                    checkCollisions();

                    nave.render();
                }else {
                    nave.render();
                    runningGame = false;
                    multiplayerSpectatorMode(navesRecivedService, socket);
                    this.stop();
                }


            }
        }.start();
    }

    private void multiplayerSpectatorMode(NavesRecivedService navesRecivedService, DatagramSocket socket){
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

                    graphicsContext.clearRect(0,0, stage.getWidth(), stage.getHeight());
                    navesRecivedService.setNavesRecived(Transformer.jsonToArrayListNaves(Transformer.packetDataToString(packet)));


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
                navesRecivedService.renderNavesRecibidas();

            }
        }.start();
    }

    private void checkCollisions() {
        checkNaveInScreen();
        checkCollisionBala();
        if (!isMultiplayer) {
            checkCollisionMeteor();
        }
    }

    private void checkCollisionMeteor() {
        //Se puede juntar el contenido de este método y el de checkCollisionBala
        meteorService.getMeteoritos().forEach(meteor -> {
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
                    (int) meteor.getImgMeteoritoRotada().getWidth(),
                    (int) meteor.getImgMeteoritoRotada().getHeight()
            );


            for (Bala bala:nave.getArma().getBalas()) {
                areaObject2.setCollisions(
                        (int)bala.getPosX(),
                        (int)bala.getPosY(),
                        (int)bala.getImagenRotada().getWidth(),
                        (int)bala.getImagenRotada().getHeight()
                );

                if(areaObject1.intersects(areaObject2) && meteor.getState().equals(Enums.MeteorState.MOVING)){
                    bala.remove();
                    meteor.remove();
                    score_p1.setText(String.valueOf(Integer.parseInt(score_p1.getText()) + 50));
                    if(Integer.parseInt(score_p1.getText())%500 == 0 && nave.getLifes() != 5){
                        nave.addLive();
                    }
                    dificulty += 0.5;
                }
            }
            areaObject2.setCollisions(
                    (int)nave.getPosX(),
                    (int)nave.getPosY(),
                    (int)nave.getImagenRotada().getWidth(),
                    (int)nave.getImagenRotada().getHeight()
            );
            if(areaObject1.intersects(areaObject2) && meteor.getState().equals(Enums.MeteorState.MOVING)){
                meteor.remove();
                nave.subsLive();
                if(nave.getLifes() <= 0){
                    nave.setState(Enums.NaveState.DYING);

                }
            }
        });
    }

    private void checkCollisionBala() {
        dataToSend.clearIdNaveTocada();
        nave.getArma().getBalas().forEach(bala -> {
            if(bala.getPosX() < 0){
                bala.remove();
            }else if(bala.getPosX() > stage.getWidth()){
                bala.remove();
            }
            if(bala.getPosY() < 0){
                bala.remove();
            }else if(bala.getPosY() > stage.getHeight()){
                bala.remove();
            }
        });

        if(isMultiplayer) {
            Image[] imagenRotadaOtrasNaves = navesRecivedService.getImagenRotadaOtrasNaves();
            navesRecivedService.getNavesRecived().forEach(naveRecivedService -> {
                if (naveRecivedService.getIdNave() != nave.getId()) {
                    areaObject1.setCollisions(
                            (int) naveRecivedService.getNavePosX(),
                            (int) naveRecivedService.getNavePosY(),
                            (int) imagenRotadaOtrasNaves[naveRecivedService.getIdNave()].getWidth(),
                            (int) imagenRotadaOtrasNaves[naveRecivedService.getIdNave()].getHeight()
                    );

                    nave.getArma().getBalas().forEach(bala -> {
                        areaObject2.setCollisions(
                                (int) bala.getPosX(),
                                (int) bala.getPosY(),
                                (int) bala.getImagenRotada().getWidth(),
                                (int) bala.getImagenRotada().getHeight()
                        );
                        if (areaObject1.intersects(areaObject2)) {
                            bala.remove();
                            nave.addScore(50);
                            dataToSend.addIdNaveTocada(naveRecivedService.getIdNave());
                        }
                    });
                }
            });
        }
    }
//HACER QUE SE GUARDE EL ID DE LA NAVE QUE HA SIDO TOCADA EN LOS DATOS QUE VAMOS A MANDAR AL SERVIDOR.

    // Añadimos la las id de las naves que han sido tocadas por tus balas
    private void checkNaveInScreen() {
        if(nave.getPosX() < 0){
            nave.setPosX(0);
        }else if(nave.getPosX() + nave.getImgNave().getImage().getWidth() + Resoluciones.AJUSTAR_PANTALLA_X > stage.getWidth()){
            nave.setPosX(stage.getWidth() - nave.getImgNave().getImage().getWidth() - Resoluciones.AJUSTAR_PANTALLA_X);
        }
        if(nave.getPosY() < 0){
            nave.setPosY(0);
        }else if(nave.getPosY() + nave.getImgNave().getImage().getHeight() + Resoluciones.AJUSTAR_PANTALLA_Y > stage.getHeight()){
            nave.setPosY(stage.getHeight() - nave.getImgNave().getImage().getHeight() - Resoluciones.AJUSTAR_PANTALLA_Y);
        }
    }
}