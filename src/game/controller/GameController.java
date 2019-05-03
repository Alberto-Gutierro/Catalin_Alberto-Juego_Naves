package game.controller;

import game.services.NavesRecivedService;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.layout.Pane;
import statVars.Packets;
import statVars.Resoluciones;

import game.GameSetter;
import game.model.Bala;
import game.model.toSend.NaveToSend;
import game.services.MeteorService;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import transformmer.Transformer;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.*;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GameController extends GameSetter implements Initializable {
    @FXML private Pane pane;

    //Datos que se mandan al servidor
    private NaveToSend dataToSend;
    private NavesRecivedService navesRecivedService;
    private byte[] recivingData;
    private MeteorService meteorService;

    @FXML Canvas canvas;
    @FXML Text score_p1, tv_ammo, tv_lives;
    @FXML AnchorPane gameOverScreen;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            score_p1.setFont(Font.loadFont(new FileInputStream(new File("src/game/res/fonts/arcadeClassic.TTF")), 40));
            tv_ammo.setFont(Font.loadFont(new FileInputStream(new File("src/game/res/fonts/arcadeClassic.TTF")), 28));
            tv_ammo.setTextAlignment(TextAlignment.RIGHT);
            tv_lives.setFont(Font.loadFont(new FileInputStream(new File("src/game/res/fonts/arcadeClassic.TTF")), 28));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        graphicsContext = canvas.getGraphicsContext2D();
        graphicsContext.scale(1,1);

        dataToSend = new NaveToSend();

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
        final Executor executor = Executors.newFixedThreadPool(4);

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
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/gameOver.fxml"));

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
        navesRecivedService = new NavesRecivedService(graphicsContext, nave.getId());

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

                dataToSend.setData(nave, timing);
                //dataToSend.getNaveArmaBalas().forEach(balaToSend -> System.out.println(balaToSend.getAngle()));

                String sendData = Transformer.classToJson(dataToSend);
                packet = new DatagramPacket(sendData.getBytes(),
                        sendData.getBytes().length,
                        ipServer,
                        portServer);

                try {
                    socket.send(packet);
                    socket.setSoTimeout(500);
                    packet = new DatagramPacket(recivingData, Packets.PACKET_LENGHT);
                    socket.receive(packet);

                    navesRecivedService.setNavesRecived(Transformer.jsonToArrayListNaves(Transformer.packetDataToString(packet)));

                    navesRecivedService.renderNavesRecibidas();

                    nave.setLifes(navesRecivedService.getMyLives());
                }
                catch (SocketTimeoutException e){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("ERROR");
                    alert.setHeaderText("Connection Time Out");
                    alert.setContentText("");
                    alert.showAndWait();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

                if(navesRecivedService.getMyLives() > 0) {
                    nave.update(timing);

                    checkCollisions();

                    nave.render();
                }else {
                    runningGame = false;
                    multiplayerSpectatorMode(navesRecivedService, socket);
                    this.stop();
                }


            }
        }.start();
    }

    private void multiplayerSpectatorMode(NavesRecivedService navesRecivedService, DatagramSocket socket){
        new AnimationTimer(){

            @Override
            public void handle(long l) {
                try {
                    packet = new DatagramPacket("Dead".getBytes(),
                            "Dead".getBytes().length,
                            ipServer,
                            portServer);
                    socket.send(packet);
                    socket.setSoTimeout(500);
                    packet = new DatagramPacket(recivingData, Packets.PACKET_LENGHT);
                    socket.receive(packet);

                    graphicsContext.clearRect(0,0, stage.getWidth(), stage.getHeight());
                    navesRecivedService.setNavesRecived(Transformer.jsonToArrayListNaves(Transformer.packetDataToString(packet)));


                } catch (IOException e) {
                    e.printStackTrace();
                }
                navesRecivedService.renderNavesRecibidas();

            }
        }.start();}

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

            Rectangle meteorArea = new Rectangle(
                    (int) meteor.getPosX(),
                    (int) meteor.getPosY(),
                    (int) meteor.getImgMeteoritoRotada().getWidth(),
                    (int) meteor.getImgMeteoritoRotada().getHeight()
            );
            Rectangle otherObject;
            for (Bala bala:nave.getArma().getBalas()) {
                otherObject = new Rectangle(
                        (int)bala.getPosX(),
                        (int)bala.getPosY(),
                        (int)bala.getImagenRotada().getWidth(),
                        (int)bala.getImagenRotada().getHeight());
                if(meteorArea.intersects(otherObject)){
                    bala.remove();
                    meteor.remove();
                    score_p1.setText(String.valueOf(Integer.parseInt(score_p1.getText()) + 50));
                    if(Integer.parseInt(score_p1.getText())%500 == 0 && nave.getLifes() != 5){
                        nave.addLive();
                    }
                    dificulty += 0.5;
                }
            }
            if(meteorArea.intersects(new Rectangle(
                    (int)nave.getPosX(),
                    (int)nave.getPosY(),
                    (int)nave.getImagenRotada().getWidth(),
                    (int)nave.getImagenRotada().getHeight()))){
                meteor.remove();
                nave.subsLive();
                if(nave.getLifes() == 0){
                    runningGame = false;
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
            Map<Integer, Image> imagenRotadaOtrasNaves = navesRecivedService.getImagenRotadaOtrasNaves();

            Map<Integer, ImageView> imagenOtrasNaves = navesRecivedService.getImagenOtrasNaves();

            navesRecivedService.getNavesRecived().forEach(naveRecivedService -> {
                if (naveRecivedService.getIdNave() != nave.getId()) {
                    Rectangle naveArea = new Rectangle(
                            (int) naveRecivedService.getNavePosX(),
                            (int) naveRecivedService.getNavePosY(),
                            (int) imagenRotadaOtrasNaves.get(naveRecivedService.getIdNave()).getWidth(),
                            (int) imagenRotadaOtrasNaves.get(naveRecivedService.getIdNave()).getHeight());

                    nave.getArma().getBalas().forEach(bala -> {
                        if (naveArea.intersects(new Rectangle(
                                (int) bala.getPosX(),
                                (int) bala.getPosY(),
                                (int) bala.getImagenRotada().getWidth(),
                                (int) bala.getImagenRotada().getHeight()))) {
                            bala.remove();
                            //HACER QUE SE GUARDE EL ID DE LA NAVE QUE HA SIDO TOCADA EN LOS DATOS QUE VAMOS A MANDAR AL SERVIDOR.

                            // Añadimos la las id de las naves que han sido tocadas por tus balas
                            dataToSend.addIdNaveTocada(naveRecivedService.getIdNave());
                        }
                    });
                }
            });
        }
    }

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