package game.model.toSend;

public class BulletToSend {

    private final int idBullet;
    private int idShip;

    private double posX;
    private double posY;
    private double angle;

    public BulletToSend(double posX, double posY, int idShip, int idBullet, double angle){
        this.posX = posX;
        this.posY = posY;
        this.idBullet = idBullet;
        this.idShip = idShip;
        this.angle = angle;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPos(double posX, double posY){
        this.posX = posX;
        this.posY = posY;
    }

    public int getIdBullet() {
        return idBullet;
    }

    public int getIdShip() {
        return idShip;
    }

    public double getAngle() {
        return angle;
    }

}
