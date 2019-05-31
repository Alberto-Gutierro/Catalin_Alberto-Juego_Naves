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
    private int[] vidasNaves = {-1, Ajustes.START_LIFES, Ajustes.START_LIFES, Ajustes.START_LIFES, Ajustes.START_LIFES};

    private boolean[] connectedPersons;

    private Enums.NaveState[] navesState;

    private boolean terminada;

    public Sala(String id) {
        navesState = new Enums.NaveState[]{Enums.NaveState.DEAD,Enums.NaveState.ALIVE,Enums.NaveState.ALIVE,Enums.NaveState.ALIVE,Enums.NaveState.ALIVE};
        connectedPersons = new boolean[]{false, false, false, false, false};
        navesVivas = new boolean[]{false, true, true, true, true};
        idSala = id;
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
}
