package server.model;

public class SalaToSend {
    String idSala;

    int numPlayers;

    /////POR AQUI: ARRAY DE TODAS LAS NAVES DENTRO DE LA SALA;
    public SalaToSend(String idSala) {
        this.idSala = idSala;
        numPlayers = 1;
    }

    public void addNumPlayers(){
        numPlayers++;
    }

    public void subsNumPlayers(){
        numPlayers--;
    }

    public String getIdSala() {
        return idSala;
    }

    public int getNumPlayers() {
        return numPlayers;
    }
}
