package server.model;

public class ClientData {

    public ClientData(int idNave, int port) {
        this.idNave = idNave;
        this.port = port;
    }

    private final int idNave;
    private final int port;
    private int lives;

    public int getIdNave() {
        return idNave;
    }

    public int getPort() {
        return port;
    }

    public int getLives() {
        return lives;
    }

}
