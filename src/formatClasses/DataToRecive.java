package formatClasses;

import game.model.toSend.BalaToSend;

import java.util.ArrayList;

public class DataToRecive {
    // Identificador de la nave
    private int idNave;

    // Identificador de la sala.
    private String idSala;

    // puntuación
    private int score;

    //posicion nave
    private double navePosX;
    private double navePosY;

    //posiciones cursor
    private double naveCursorPosX;
    private double naveCursorPosY;

    private ArrayList<Integer> navesTocadas;
    private ArrayList<Integer> meteoritosTocados;

    //angulo (nave y bala)
    private double angle;

    private int lifes;

    //balas
    private ArrayList<BalaToSend> naveArmaBalas;

    public int getScore() {
        return score;
    }

    public int getIdNave() {
        return idNave;
    }

    public String getIdSala(){
        return idSala;
    }

    public double getNavePosX() {
        return navePosX;
    }

    public double getNavePosY() {
        return navePosY;
    }

    public double getNaveCursorPosX() {
        return naveCursorPosX;
    }

    public double getNaveCursorPosY() {
        return naveCursorPosY;
    }

    public double getAngle() {
        return angle;
    }

    public ArrayList<BalaToSend> getNaveArmaBalas() {
        return naveArmaBalas;
    }

    public int getLifes() {
        return lifes;
    }

    public void setLifes(int lifes){
        this.lifes = lifes;
    }

    public ArrayList<Integer> getNavesTocadas() {
        return navesTocadas;
    }

    public ArrayList<Integer> getMeteoritosTocados() {
        return meteoritosTocados;
    }

    @Override
    public boolean equals(Object obj) {
        return idNave == ((DataToRecive) obj).getIdNave();
    }


    @Override
    public String toString() {
        return "ID: " + idNave + "\nAngulo: " + angle;
    }
}
