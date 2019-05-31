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

    private ArrayList<DataToRecive> naves;

    private Map<InetAddress, ClientData> mapIdNaves;

    private boolean[] navesVivas;
    private int numNavesVivas;

    private int[] vidasNaves;

    private boolean[] connectedPersons;

    private Enums.NaveState[] navesState;

    private boolean terminada;

    public void resetSala(){
        terminada = false;
        vidasNaves = new int[]{-1, Ajustes.START_LIFES, Ajustes.START_LIFES, Ajustes.START_LIFES, Ajustes.START_LIFES};
        navesState = new Enums.NaveState[]{Enums.NaveState.DEAD,Enums.NaveState.ALIVE,Enums.NaveState.ALIVE,Enums.NaveState.ALIVE,Enums.NaveState.ALIVE};
        navesVivas = new boolean[]{false, false, false, false, false};
        naves = new ArrayList<>();

    }

    public Sala(String id) {
        navesState = new Enums.NaveState[]{Enums.NaveState.DEAD,Enums.NaveState.ALIVE,Enums.NaveState.ALIVE,Enums.NaveState.ALIVE,Enums.NaveState.ALIVE};
        connectedPersons = new boolean[]{false, false, false, false, false};
        navesVivas = new boolean[]{false, false, false, false, false};
        idSala = id;
        vidasNaves = new int[]{-1, Ajustes.START_LIFES, Ajustes.START_LIFES, Ajustes.START_LIFES, Ajustes.START_LIFES};
        naves = new ArrayList<>();
        mapIdNaves = new HashMap<>();
        terminada=false;
    }

    public String getIdSala() {
        return idSala;
    }

    public ArrayList<DataToRecive> getNaves() {
        return naves;
    }

    public Map<InetAddress, ClientData> getMapIdNaves() {
        return mapIdNaves;
    }

    public boolean[] getNavesVivas() {
        return navesVivas;
    }

    public int[] getVidasNaves() {
        return vidasNaves;
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

    public Enums.NaveState[] getNavesState() {
        return navesState;
    }

    public int getNumNavesVivas() {
        return numNavesVivas;
    }

    public void addNumNavesVivas() {
        numNavesVivas++;
    }

    public void subsNumNavesVivas() {
        numNavesVivas--;
    }

    public boolean isTerminada() {
        return terminada;
    }

    public void setTerminada(boolean terminada) {
        this.terminada = terminada;
    }
}
