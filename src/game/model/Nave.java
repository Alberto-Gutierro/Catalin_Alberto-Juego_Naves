package game.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import statVars.Ajustes;
import statVars.Enums;

public class Nave {
    private int id;

    private Cursor orientation;

    private int score;

    private double posX;
    private double posY;

    private final int SPEED = 5;
    private int lifes;
    private final int MAX_LIFES = Ajustes.MAX_LIFES;

    private BooleanProperty upPressed, downPressed, rightPressed, leftPressed;
    private BooleanBinding anyPressed;

    private ImageView imgNave;
    private Image imagenRotada;

    private Arma arma;

    private SnapshotParameters snapshotParameters;

    private GraphicsContext graphicsContext;

    private ImageView[] imgVidas;

    private Enums.NaveState state;
    private Animacion animacion;

    public Nave(GraphicsContext graphicsContext, Pane pane, int idNave, ImageView imgNave, BooleanProperty upPressed, BooleanProperty downPressed, BooleanProperty rightPressed, BooleanProperty leftPressed, BooleanBinding anyPressed) {
        animacion=new Animacion();
        imgVidas = new ImageView[MAX_LIFES];
        ///IMAGENES A LAS VIDAS
        for (int i = 0; i<MAX_LIFES; i++) {
            ImageView imagen = new ImageView("game/res/img/life.png");
            imagen.setX(120 + (imagen.getImage().getWidth() + 5) * i+1);
            imagen.setY(110);
            pane.getChildren().add(imagen);
            imgVidas[i] = imagen;
        }

        score = 0;

        lifes = Ajustes.START_LIFES;

        this.id = idNave;

        arma = new Arma(graphicsContext, pane);
        orientation = new Cursor();

        this.graphicsContext = graphicsContext;

        if(idNave == 1){
            this.posX = 250;
            this.posY = 250;
        }else if(idNave == 2){
            this.posX = pane.getWidth()-250;
            this.posY = 250;
        }else if(idNave == 3){
            this.posX = 250;
            this.posY = pane.getHeight()-250;
        }else {
            this.posX = pane.getWidth()-250;
            this.posY = pane.getHeight()-250;
        }

        this.upPressed = upPressed;
        this.downPressed = downPressed;
        this.rightPressed = rightPressed;
        this.leftPressed = leftPressed;
        this.anyPressed = anyPressed;

        this.imgNave = imgNave;

        this.snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);

        state = Enums.NaveState.ALIVE;

    }

    public Arma getArma(){
        return arma;
    }

    public Cursor getOrientation() {
        return orientation;
    }

    public void setOrientation(double x, double y){
        orientation.setPosX(x);
        orientation.setPosY(y);
    }

    public void setImagenRotada(Image imagenRotada) {
        this.imagenRotada = imagenRotada;
    }

    public Image getImagenRotada() {
        return imagenRotada;
    }

    public ImageView getImgNave(){
        return imgNave;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosX(double pos){
        posX = pos;
    }

    public void setPosY(double pos){
        posY = pos;
    }

    public int getId(){
        return id;
    }

    private void mover(){
        if (upPressed.get()) {
            posY = getPosY() - SPEED;
        }
        if (downPressed.get()) {
            posY = getPosY() + SPEED;
        }
        if (leftPressed.get()) {
            posX = getPosX() - SPEED;
        }
        if (rightPressed.get()) {
            posX = getPosX() + SPEED;
        }
    }

    public double getAngle(){
//        double centerX = posX + imgNave.getHeight()/2;
//        double centerY = posY + imgNave.getWidth()/2;
//
//
//        double cc = (centerX - orientation.getPosX());
//        double co = (centerY - orientation.getPosY());
//
//        return Math.toDegrees(Math.atan2(co, cc))-90;
        return Math.round(Math.toDegrees(
                Math.atan2(
                        ((posY + imagenRotada.getWidth()/2) - orientation.getPosY()),
                        ((posX + imagenRotada.getHeight()/2) - orientation.getPosX()))
                )
        )-90;
    }

    private void rotate(){
        imgNave.setRotate(getAngle());

        imagenRotada = imgNave.snapshot(snapshotParameters, null);
    }

    public void shoot(double cursorX, double cursorY) {
        state = Enums.NaveState.SHOOTING;

        arma.shoot(
                (posX + imgNave.getImage().getWidth()/2),
                (posY + imgNave.getImage().getHeight()/2),
                (posX + imgNave.getImage().getWidth()/2) - cursorX,
                (posY + imgNave.getImage().getHeight()/2) - cursorY,
                getAngle());


    }

    public void update(double time){
        arma.update(time);
        if(anyPressed.get()) {
            mover();
        }
        if(state.equals(Enums.NaveState.DYING)) {
            if (animacion.getFrame() < Ajustes.NAVEDESTRUIR_LENGHT) {
                imgNave = animacion.naveDestruir(id);
            }
            else {
                animacion.finalAnimacion();
                state=Enums.NaveState.DEAD;
            }
        } else if (state.equals(Enums.NaveState.SHOOTING)) {
            if (animacion.getFrame() < Ajustes.NAVESHOOT_LENGHT) {
                imgNave = animacion.naveDisparo(id);
            }
            else {
                animacion.finalAnimacion();
                state=Enums.NaveState.ALIVE;
            }
        }

        rotate();
    }

    public void render(){
        graphicsContext.drawImage(imagenRotada, posX, posY);

        arma.render();
        for (int i = 0; i < MAX_LIFES; i++) {
            if(i< lifes) {
                imgVidas[i].setOpacity(1);
            }else{
                imgVidas[i].setOpacity(0.5);
            }
        }
    }

    public void subsLive(){
        lifes--;
    }

    public void addLive(){
        if(lifes != 5) {
            lifes++;
        }
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public int getLifes() {
        return lifes;
    }

    public void setLifes(int lifes) {
        this.lifes = lifes;
    }

    public void setState(Enums.NaveState state) {
        this.state = state;
    }

    public Enums.NaveState getState() {
        return state;
    }
}
