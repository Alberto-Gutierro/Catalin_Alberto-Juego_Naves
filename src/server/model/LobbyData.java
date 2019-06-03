package server.model;

public class LobbyData {
    boolean[] connectedPersons;
    int winner;

    public LobbyData(boolean[] connectedPersons, int winner) {
        this.connectedPersons = connectedPersons;
        this.winner = winner;
    }

    public boolean[] getConnectedPersons() {
        return connectedPersons;
    }

    public int getWinner() {
        return winner;
    }
}
