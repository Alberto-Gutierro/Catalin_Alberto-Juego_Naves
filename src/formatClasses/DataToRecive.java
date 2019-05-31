package formatClasses;

import game.model.toSend.BulletToSend;
import statVars.Enums;

import java.util.ArrayList;

public class DataToRecive {
    // Identificador de la ship
    private int idShip;

    // Identificador de la sala.
    private String idSala;

    // puntuación
    private int score;

    //posicion ship
    private double shipPosX;
    private double shipPosY;

    //posiciones cursor
    private double shipCursorPosX;
    private double shipCursorPosY;

    private ArrayList<Integer> shipsTocadas;
    private ArrayList<Integer> meteorsTocados;

    //angulo (ship y bullet)
    private double angle;

    private int lifes;

    private Enums.ShipState state;

    //bullets
    private ArrayList<BulletToSend> shipWeaponBullets;

    public int getScore() {
        return score;
    }

    public int getIdShip() {
        return idShip;
    }

    public String getIdSala(){
        return idSala;
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

    public double getAngle() {
        return angle;
    }

    public ArrayList<BulletToSend> getShipWeaponBullets() {
        return shipWeaponBullets;
    }

    public int getLifes() {
        return lifes;
    }

    public void setLifes(int lifes){
        this.lifes = lifes;
    }

    public ArrayList<Integer> getShipsTocadas() {
        return shipsTocadas;
    }

    public ArrayList<Integer> getMeteorsTocados() {
        return meteorsTocados;
    }

    public Enums.ShipState getState() {
        return state;
    }

    public void setState(Enums.ShipState state) {
        this.state = state;
    }

    @Override
    public boolean equals(Object obj) {
        return idShip == ((DataToRecive) obj).getIdShip();
    }


    @Override
    public String toString() {
        return "ID: " + idShip + "\nAngulo: " + angle;
    }
}
