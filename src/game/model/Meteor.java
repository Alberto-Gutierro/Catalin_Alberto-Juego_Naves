package game.model;

import statVars.Enums;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Meteor {
    private double cos;
    private double sin;

    private double posX;
    private double posY;

    private GraphicsContext graphicsContext;

    private Image imgMeteorRotada;
    private double speed = 10;

    private Enums.MeteorState state;

    private double angulo;

    private SnapshotParameters snapshotParameters;



    public Meteor(double posX, double posY, double xShip, double yShip, double speed, GraphicsContext graphicsContext, ImageView imgMeteor) {
        this.posX = posX;
        this.posY = posY;

        this.speed = speed;

        snapshotParameters= new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);

        angulo = Math.random()*360;

        imgMeteor.setRotate(angulo);
        imgMeteorRotada = imgMeteor.snapshot(snapshotParameters, null);

        double cc = posX + imgMeteorRotada.getWidth()/2 - xShip;
        double co = posY + imgMeteorRotada.getHeight()/2 - yShip;

        cos = cc / Math.hypot(cc, co);
        sin = co / Math.hypot(cc, co);

        state=Enums.MeteorState.MOVING;

        this.graphicsContext = graphicsContext;
    }


    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public void move(){
        posX -= cos * speed;
        posY -= sin * speed;
    }

    public void remove(){
        state = Enums.MeteorState.TO_REMOVE;
    }

    public void update(){
        move();
    }

    public void render(){
        graphicsContext.drawImage(imgMeteorRotada, posX, posY);
    }

    public Enums.MeteorState getState() {
        return state;
    }

    public void setState(Enums.MeteorState state) {
        this.state = state;
    }

    public Image getImgMeteorRotada() {
        return imgMeteorRotada;
    }

    public void setImgMeteorRotada(ImageView imgMeteor) {
        imgMeteor.setRotate(angulo);
        imgMeteorRotada = imgMeteor.snapshot(snapshotParameters, null);
    }

    public double getAngulo() {
        return angulo;
    }
}