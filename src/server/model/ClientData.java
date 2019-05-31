package server.model;

public class ClientData {

    public ClientData(int idShip, int port) {
        this.idShip = idShip;
        this.port = port;
    }

    private final int idShip;
    private final int port;
    private int lifes;

    public int getIdShip() {
        return idShip;
    }

    public int getPort() {
        return port;
    }

    public int getLifes() {
        return lifes;
    }

}
