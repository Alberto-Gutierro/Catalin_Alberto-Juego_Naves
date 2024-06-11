package server;

import formatClasses.DataToRecive;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import server.model.ClientData;
import server.model.LobbyData;
import server.model.Sala;
import server.model.SalaToSend;
import statVars.Ajustes;
import statVars.Enums;
import statVars.MensajesServer;
import statVars.Packets;
import transformmer.Transformer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class ServerGameController {

    @FXML private Text ipServer;

    private DatagramSocket socket;

    private Map<String, Sala> salas;

    private Map<String, SalaToSend> salasToSend;

    private boolean serverOn;

    private ThreadPoolExecutor server;

    public boolean isServerOn() {
        return serverOn;
    }

    public void init(int port) throws SocketException {
        serverOn = false;

        server = (ThreadPoolExecutor) Executors.newFixedThreadPool(12);

        socket = new DatagramSocket(port);
        salas = new HashMap<>();
        salasToSend = new HashMap<>();

    }


    public void startStop(ActionEvent actionEvent) {
        if(serverOn){
            serverOn = false;
            ((Button) actionEvent.getSource()).setText("START");
            server.shutdown();
            ipServer.setText("DISCONNECTED");
            salas = new HashMap<>();
            salasToSend = new HashMap<>();

        }else {

            serverOn = true;
            server = (ThreadPoolExecutor) Executors.newFixedThreadPool(12);

            try {
                final DatagramSocket socket = new DatagramSocket();
                socket.connect(InetAddress.getByName("8.8.8.8"), 10002);

                ipServer.setText(socket.getLocalAddress().getHostAddress() + ":5568");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            server.execute(() -> {
                try {
                    runServer();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            ((Button) actionEvent.getSource()).setText("STOP");
        }
    }

    private void runServer() throws IOException {
        byte [] receivingData = new byte[Packets.PACKET_LENGHT];
        byte [] sendingData;
        InetAddress clientIP;
        int clientPort;

        //el servidor atén el port indefinidament
        while(serverOn){

            DatagramPacket packet = new DatagramPacket(receivingData, Packets.PACKET_LENGHT);
            //espera de les dades
            socket.receive(packet);
            if(serverOn) {
                //System.out.println(Transformer.packetDataToString(packet));
                //obtenció de l'adreça del client
                clientIP = packet.getAddress();
                //obtenció del port del client
                clientPort = packet.getPort();

                //processament de les dades rebudes i obtenció de la resposta
                sendingData = processData(packet);
                //System.out.println(sendingData.length);

                if (!new String(sendingData).equals("Starting")) {
                    //creació del paquet per enviar la resposta
                    packet = new DatagramPacket(sendingData, sendingData.length, clientIP, clientPort);
                    //System.out.println(new String(respuesta, Charset.defaultCharset()));

                    //enviament de la resposta
                    socket.send(packet);
                }
            }
        }
    }

    private byte[] processData(DatagramPacket packet) {
        try {

            if(Transformer.packetDataToString(packet).matches("^Room:.+$")){
                return getIdOfShipClient(packet).getBytes();
            }else if(Transformer.packetDataToString(packet).matches("^Dead:.+$")){
                return deadData(packet).getBytes();
            }else if(Transformer.packetDataToString(packet).matches("^Waiting:.+$")){
                return waitingData(packet);
            }else if(Transformer.packetDataToString(packet).matches("^Exit:.+$")){
                return exitingData(packet);
            }

            switch (Transformer.packetDataToString(packet)) {
                case "Connect": case "Rooms":
                    return getSalas().getBytes();
                case "Create":
                    return createSala(packet).getBytes();
                case "Start":
                    return signalToStart(packet).getBytes();
                default:
                    return updateJsonGame(packet).getBytes();

            }
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }

//        int numero = ByteBuffer.wrap(data).getInt(); //d'array de bytes a integer
//        if(numero > numeroRandom){
//            System.out.println(numero + " es más grande que " + numeroRandom);
//            return ByteBuffer.allocate(4).putInt(1).array(); //de integer a array de bytes;
//        }else if(numero < numeroRandom){
//            System.out.println(numero + " es más pequeño que " + numeroRandom);
//            return ByteBuffer.allocate(4).putInt(-1).array(); //de integer a array de bytes;
//        }else {
//            System.out.println("Has GANADO");
//            return ByteBuffer.allocate(4).putInt(0).array(); //de integer a array de bytes;
//        }
    }

    private byte[] exitingData(DatagramPacket packet) {
        try {
            Sala sala = salas.get(Transformer.packetDataToString(packet).split(":")[1]);

            if(sala.getWinner() == sala.getMapIdShips().get(packet.getAddress()).getIdShip()){
                sala.setWinner(0);
            }

            sala.subsAConnectedPerson(sala.getMapIdShips().get(packet.getAddress()).getIdShip());

            sala.getMapIdShips().remove(packet.getAddress());


            if(sala.getMapIdShips().size() == 0) {
                salasToSend.remove(Transformer.packetDataToString(packet).split(":")[1]);
                salas.remove(Transformer.packetDataToString(packet).split(":")[1]);
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "ExitSuccessful".getBytes();
    }

    private String getSalas() {
        return Transformer.classToJson(salasToSend);
    }

    private byte[] waitingData(DatagramPacket packet){

        Sala sala = null;
        try {
            sala = salas.get(Transformer.packetDataToString(packet).split(":")[1]);

            if(sala.isTerminada()){
                sala.resetSala();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return Transformer.classToJson(new LobbyData(sala.getConnectedPersons(), sala.getWinner())).getBytes();
    }

    private String deadData(DatagramPacket packet){
        try {
            final Sala sala = salas.get(Transformer.packetDataToString(packet).split(":")[1]);

            if(sala.getShipsVivas()[sala.getMapIdShips().get(packet.getAddress()).getIdShip()]) {
                sala.getShipsVivas()[sala.getMapIdShips().get(packet.getAddress()).getIdShip()] = false;
                sala.subsNumShipsVivas();
            }

            if(sala.getNumShipsVivas() == 1){
                sala.setTerminada(true);
                return "FinishGame";
            }
//            if(sala.getShipsVivas()[sala.getMapIdShips().get(packet.getAddress()).getIdShip()]) {
//                sala.getShips().forEach(ship -> {
//                    if (ship.getIdShip() == sala.getMapIdShips().get(packet.getAddress()).getIdShip()) {
//                        shipToRemove = ship;
//                        // Aqui cogemos de la array de ships vivas y la que tiene tu Id la pasas a false
//                        sala.getShipsVivas()[sala.getMapIdShips().get(packet.getAddress()).getIdShip()] = false;
//                    }
//                });
//                sala.getShips().remove(shipToRemove);
//            }
            return Transformer.classToJson(sala.getShips());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "ERROR";
    }


    private String updateJsonGame(DatagramPacket packet) throws UnsupportedEncodingException {

        DataToRecive receivedData = Transformer.jsonToShipToRecive(Transformer.packetDataToString(packet));
        Sala sala = salas.get(receivedData.getIdSala());

        if(sala.getNumShipsVivas() == 1 && sala.isTerminada()){
            sala.setWinner(receivedData.getIdShip());
            return "FinishGame";
        }

        if(!sala.getShips().contains(receivedData)) {
            sala.getShips().add(receivedData);
            sala.addNumShipsVivas();
            sala.getShipsVivas()[sala.getMapIdShips().get(packet.getAddress()).getIdShip()] = true;
        }

        //receivedData.getShipWeaponBullets().forEach(bulletToSend -> System.out.println(bulletToSend.getAngle()));


        if(receivedData.getShipsTocadas() != null || receivedData.getShipsTocadas().size() != 0) {
            sala.getShips().forEach(ship -> {

                ship.setLifes(sala.getLifesShips()[ship.getIdShip()]);
                receivedData.getShipsTocadas().forEach(shipTocada -> {

                    if (ship.getIdShip() == shipTocada && ship.getState() != Enums.ShipState.DEAD && ship.getState() != Enums.ShipState.DYING) {
                        //RESTAMOS UNA VIDA A LA NAVE QUE HA SIDO TOCADA
                        ship.setLifes(--sala.getLifesShips()[shipTocada]);

                        if(ship.getLifes() <= 0 && ship.getState() != Enums.ShipState.DYING){
                            sala.getShipsState()[ship.getIdShip()] = Enums.ShipState.DYING;
                        }

                        //AÑADIMOS UNA VIDA A LA NAVE QUE HA TOCADO A LA OTRA
                        if(sala.getLifesShips()[receivedData.getIdShip()] < Ajustes.MAX_LIFES) {
                            sala.getLifesShips()[receivedData.getIdShip()]++;
                        }
                    }
                });
            });
        }

        if(receivedData.getState() != Enums.ShipState.DEAD) {
            receivedData.setState(sala.getShipsState()[receivedData.getIdShip()]);
        }else {
            sala.getShipsState()[receivedData.getIdShip()] = Enums.ShipState.DEAD;
        }

        receivedData.setLifes(sala.getLifesShips()[receivedData.getIdShip()]);
        sala.getShips().set(sala.getShips().indexOf(receivedData), receivedData);

        //ships.forEach(ship-> System.out.println(ship.toString()));
        return Transformer.classToJson(sala.getShips());
    }

    private String signalToStart(DatagramPacket packet) {
        //sendAll("Start", packet);

        return "Starting";
    }

    //ESTO SE HARÁ CUANDO se conecte a una sala y tiene que devolver una sala.
    private String getIdOfShipClient(DatagramPacket packet){
        /**
         * 1. Asignar ID a la Ship.
         * (Num 1-4)
         *
         */
        String numSala = "";
        try {
            //Estoy cogiendo el numero de la sala a la que se quiera conectar el usuario. Connect:numSala
            numSala = String.valueOf(Transformer.packetDataToString(packet).split(":")[1]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if(salas.containsKey(numSala)) {
            //SI NO CONTIENE LA IP DE EL CLIENTE && El límite de ships es inferior a 4
            if (!salas.get(numSala).getMapIdShips().containsKey(packet.getAddress()) && salas.get(numSala).getMapIdShips().size() < 4) {

                salasToSend.get(numSala).addNumPlayers();

                int id;
                for (id = 1; id <= 4; id++) {
                    if (!salas.get(numSala).getConnectedPersons()[id]) {
                        salas.get(numSala).addAConnectedPerson(id);
                        break;
                    }
                }
                salas.get(numSala).getMapIdShips().put(packet.getAddress(), new ClientData(id, packet.getPort()));

                return String.valueOf(id + ":" + numSala);

                //CAMBIAR: Este else if dejará de existir ya que cuando te salgas de la sala se borrarán todos los datos.
            } else if(salas.get(numSala).getMapIdShips().containsKey(packet.getAddress())){
                return MensajesServer.YA_DENTRO;
            } else {
                return MensajesServer.SALA_LLENA;
            }
        }else {
            return MensajesServer.SALA_NO_EXISTE;
        }
    }

    private String createSala(DatagramPacket packet) {
        Sala sala = new Sala(UUID.randomUUID().toString());

        sala.getMapIdShips().put(packet.getAddress(),new ClientData(sala.getMapIdShips().size()+1, packet.getPort()));

        salas.put(sala.getIdSala(), sala);

        salasToSend.put(sala.getIdSala(), new SalaToSend(sala.getIdSala()));

        sala.addAConnectedPerson(sala.getMapIdShips().get(packet.getAddress()).getIdShip());

        return String.valueOf(1 + ":" + sala.getIdSala());
    }


//    private void sendAll(String signal, DatagramPacket packet) {
//        salas.get(numSala).getMapIdShips().forEach((ip,clientData)-> {
//            if(ip != packet.getAddress()) {
//                System.out.println("MANDA A " + ip.getHostAddress() + ":" + clientData.getPort());
//
//                try {
//                    //¡¡¡NO FUNCIONA!!!
//                    new DatagramSocket().send(new DatagramPacket(signal.getBytes(), signal.getBytes().length, ip, clientData.getPort()));
//                    System.out.println("asdasd");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//
//    }



    //     public void checkJsonCecived(){
    //        System.out.println(updateJsonGame("{\"idShip\":1,\"shipPosX\":500.0,\"shipPosY\":500.0,\"shipCursorPosX\":1381.6,\"shipCursorPosY\":17.6,\"angle\":59.0,\"shipWeaponBullets\":[{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idShip\":1},{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idShip\":1},{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idShip\":1}],\"timer\":{\"max\":10.0,\"time\":2.299999999079997},\"prueba\":0}\n".getBytes()));
    //        System.out.println(updateJsonGame("{\"idShip\":2,\"shipPosX\":500.0,\"shipPosY\":500.0,\"shipCursorPosX\":1381.6,\"shipCursorPosY\":17.6,\"angle\":59.0,\"shipWeaponBullets\":[{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idShip\":1},{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idShip\":1},{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idShip\":1}],\"timer\":{\"max\":10.0,\"time\":2.299999999079997},\"prueba\":0}\n".getBytes()));
    //
    //    }
}