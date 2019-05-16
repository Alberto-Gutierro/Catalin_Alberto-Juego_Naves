package server.model;

public class SalaToSend {
    String idSala;
    int numPlayers;

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


}
