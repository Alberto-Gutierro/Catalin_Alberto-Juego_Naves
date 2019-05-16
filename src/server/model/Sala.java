package server.model;

import formatClasses.DataToRecive;
import statVars.AjustesNave;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Sala {
    private String idSala;

    private ArrayList<DataToRecive> naves;

    private Map<InetAddress, ClientData> mapIdNaves;

    private boolean[] navesVivas = {false, true, true, true, true};
    private int[] vidasNaves = {-1, AjustesNave.START_LIFES, AjustesNave.START_LIFES, AjustesNave.START_LIFES, AjustesNave.START_LIFES};

    private int connectedPersons;

    public Sala(String id) {
        idSala = id;
        naves = new ArrayList<>();
        mapIdNaves = new HashMap<>();
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

    public void addAConnectedPerson(){

    }
    public void subsAConnectedPerson(){

    }
}
