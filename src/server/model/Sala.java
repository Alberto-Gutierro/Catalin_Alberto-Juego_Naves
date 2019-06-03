package server.model;

import formatClasses.DataToRecive;
import statVars.Ajustes;
import statVars.Enums;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Sala {
    private String idSala;

    private int winner;

    private ArrayList<DataToRecive> ships;

    private Map<InetAddress, ClientData> mapIdShips;

    private boolean[] shipsVivas;
    private int numShipsVivas;

    private int[] lifesShips;

    private boolean[] connectedPersons;

    private Enums.ShipState[] shipsState;

    private boolean terminada;

    public void resetSala(){
        numShipsVivas = 0;
        terminada = false;
        lifesShips = new int[]{-1, Ajustes.START_LIFES, Ajustes.START_LIFES, Ajustes.START_LIFES, Ajustes.START_LIFES};
        shipsState = new Enums.ShipState[]{Enums.ShipState.DEAD,Enums.ShipState.ALIVE,Enums.ShipState.ALIVE,Enums.ShipState.ALIVE,Enums.ShipState.ALIVE};
        shipsVivas = new boolean[]{false, false, false, false, false};
        //connectedPersons = new boolean[]{false, false, false, false, false};
        ships = new ArrayList<>();

    }

    public Sala(String id) {
        shipsState = new Enums.ShipState[]{Enums.ShipState.DEAD,Enums.ShipState.ALIVE,Enums.ShipState.ALIVE,Enums.ShipState.ALIVE,Enums.ShipState.ALIVE};
        connectedPersons = new boolean[]{false, false, false, false, false};
        shipsVivas = new boolean[]{false, false, false, false, false};
        idSala = id;
        lifesShips = new int[]{-1, Ajustes.START_LIFES, Ajustes.START_LIFES, Ajustes.START_LIFES, Ajustes.START_LIFES};
        ships = new ArrayList<>();
        mapIdShips = new HashMap<>();
        terminada=false;
    }

    public String getIdSala() {
        return idSala;
    }

    public ArrayList<DataToRecive> getShips() {
        return ships;
    }

    public Map<InetAddress, ClientData> getMapIdShips() {
        return mapIdShips;
    }

    public boolean[] getShipsVivas() {
        return shipsVivas;
    }

    public int[] getLifesShips() {
        return lifesShips;
    }

    @Override
    public boolean equals(Object obj) {
        return ((Sala)obj).getIdSala().equals(idSala);
    }

    public boolean[] getConnectedPersons() {
        return connectedPersons;
    }

    public void addAConnectedPerson(int pos){
        connectedPersons[pos] = true;
    }

    public void subsAConnectedPerson(int pos){
        connectedPersons[pos] = false;
    }

    public Enums.ShipState[] getShipsState() {
        return shipsState;
    }

    public int getNumShipsVivas() {
        return numShipsVivas;
    }

    public void addNumShipsVivas() {
        numShipsVivas++;
    }

    public void subsNumShipsVivas() {
        numShipsVivas--;
    }

    public boolean isTerminada() {
        return terminada;
    }

    public void setTerminada(boolean terminada) {
        this.terminada = terminada;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }
}
