package game.model;

import statVars.Enums;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Meteorito {
    private double cos;
    private double sin;

    private double posX;
    private double posY;

    private GraphicsContext graphicsContext;

    private Image imgMeteoritoRotada;
    private double speed = 10;

    private Enums.MeteorState state;

    private ImageView imgMeteorito;

    private double angulo;

    private SnapshotParameters snapshotParameters;



    public Meteorito(double posX, double posY, double xNave, double yNave, double speed, GraphicsContext graphicsContext) {
        this.posX = posX;
        this.posY = posY;

        this.speed = speed;

        snapshotParameters= new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);

        imgMeteorito = new ImageView("game/res/img/img_meteorito.png");

        angulo = Math.random()*360;

        imgMeteorito.setRotate(angulo);
        imgMeteoritoRotada = imgMeteorito.snapshot(snapshotParameters, null);

        double cc = posX + imgMeteoritoRotada.getWidth()/2 - xNave;
        double co = posY + imgMeteoritoRotada.getHeight()/2 - yNave;

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
        graphicsContext.drawImage(imgMeteoritoRotada, posX, posY);
    }

    public Enums.MeteorState getState() {
        return state;
    }

    public void setState(Enums.MeteorState state) {
        this.state = state;
    }

    public Image getImgMeteoritoRotada() {
        return imgMeteoritoRotada;
    }

    public void setImgMeteoritoRotada(ImageView imgMeteorito) {
        imgMeteorito.setRotate(angulo);
        imgMeteoritoRotada = imgMeteorito.snapshot(snapshotParameters, null);
    }

    public double getAngulo() {
        return angulo;
    }
}