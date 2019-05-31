package game.model.toSend;

import game.model.Ship;
import game.model.Timer;
import statVars.Enums;

import java.util.ArrayList;

//Se pondrán las variables que se necesite para mandar al servidor.
public class DataToSend {

    private int idShip;

    private String idSala;

    private int score;

    //posicion ship
    private double shipPosX;
    private double shipPosY;

    //posiciones cursor
    private double shipCursorPosX;
    private double shipCursorPosY;
    // .
    //angulo (ship y bullet)
    private double angle;

    //bullets
    private ArrayList<BulletToSend> shipWeaponBullets;
    private ArrayList<Integer> shipsTocadas;
    private ArrayList<Integer> meteorsTocados;

    private int lifes;

    private Enums.ShipState state;

    private Timer timer;

    public DataToSend(){
        shipWeaponBullets = new ArrayList<>();
        timer = new Timer(3);
        shipsTocadas = new ArrayList<>();
        meteorsTocados = new ArrayList<>();
    }
    //change

    public void setData(Ship ship, double time, String idSala) {
        this.idSala = idSala;

        lifes = ship.getLifes();

        state = ship.getState();

        score = ship.getScore();

        timer.update(time);

        //Si han pasado 10 segundos se borran todas las bullets de dentro del array.
        if(timer.check()){
            shipWeaponBullets.clear();
            ship.getWeapon().getBullets().forEach(bullet->bullet.setAdded(false));
        }

        this.idShip = ship.getId();
        shipPosX = ship.getPosX();
        shipPosY = ship.getPosY();

        angle = ship.getAngle();

        shipCursorPosX = ship.getOrientation().getPosX();
        shipCursorPosY = ship.getOrientation().getPosY();

        if(!ship.getWeapon().getBullets().isEmpty()) {
            ship.getWeapon().getBullets().forEach(bullet -> {
                if(!bullet.getAdded()) {
                    shipWeaponBullets.add(new BulletToSend(bullet.getPosX(), bullet.getPosY(), ship.getId(), bullet.getIdBullet(), bullet.getAngle()));
                    bullet.setAdded(true);
                } else{
                    shipWeaponBullets.forEach(bulletToSend -> {
                        if (bulletToSend.getIdBullet() == bullet.getIdBullet()) {
                            bulletToSend.setPos(bullet.getPosX(),bullet.getPosY());
                        }
                    });

                }
            });
        }

//        System.out.println("////////////////////" + shipWeaponBullets.size());
    }

    public double getShipPosX() {
        return shipPosX;
    }

    public double getShipPosY() {
        return shipPosY;
    }

    public double getShipCursorPosX() {
        return shipCursorPosX;
    }

    public double getShipCursorPosY() {
        return shipCursorPosY;
    }

    public double getAngle(){
        return angle;
    }

    public ArrayList<BulletToSend> getShipWeaponBullets() {
        return shipWeaponBullets;
    }

    public void addIdShipTocada(int id){
        shipsTocadas.add(id);
    }

    public void clearIdShipTocada() {
        shipsTocadas.clear();
    }
}
