package game.model;

import statVars.Enums;
import statVars.Resoluciones;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public class Bullet {

    private Enums.BulletState state;

    private int idBullet;

    private double posX;
    private double posY;

    private double cos;
    private double sin;

    private final double speed = 20;

    private GraphicsContext graphicsContext;

    private Image imagenRotada;

    private boolean added;

    private double angle;

    public Bullet(GraphicsContext graphicsContext, double x, double y, double cc, double co, double angle, int idBullet, ImageView imgBullet){
        //Para saber si la he añadido dentro del JSON.
        this.angle = angle;

        added = false;

        this.idBullet = idBullet;

        state = Enums.BulletState.SHOOTING;

        SnapshotParameters snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);

        imgBullet.setRotate(angle);
        imagenRotada = imgBullet.snapshot(snapshotParameters, null);

        this.graphicsContext = graphicsContext;

        posX = x;
        posY = y;

        cos = (cc + Resoluciones.AJUSTAR_CATETOS)/Math.hypot(cc, co);
        sin = (co + Resoluciones.AJUSTAR_CATETOS)/Math.hypot(cc, co);

    }

    private void move(){
        posX -= cos * speed;
        posY -= sin * speed;

        //System.out.println(posX + " " + posY);
    }

    public void remove(){
        state = Enums.BulletState.TO_REMOVE;
    }

    public void update(){
        move();
    }

    public void render(){
        graphicsContext.drawImage(imagenRotada, posX, posY);
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public double getCos() {
        return cos;
    }

    public double getSin() {
        return sin;
    }

    public Enums.BulletState getState() {
        return state;
    }

    public boolean getAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    public int getIdBullet() {
        return idBullet;
    }

    public double getAngle() {
        return angle;
    }

    public Image getImagenRotada() {
        return imagenRotada;
    }

}
