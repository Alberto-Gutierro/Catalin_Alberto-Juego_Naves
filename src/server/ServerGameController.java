package server;

import formatClasses.DataToRecive;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import server.model.ClientData;
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
import java.util.Arrays;
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

        }else {

            serverOn = true;
            server = (ThreadPoolExecutor) Executors.newFixedThreadPool(12);
            try {
                ipServer.setText(InetAddress.getLocalHost().getHostAddress() + ":5568");
            } catch (UnknownHostException e) {
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
        while(serverOn/* No esten todos los jugadores */){

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
                return getIdOfNaveClient(packet).getBytes();
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

            sala.subsAConnectedPerson(sala.getMapIdNaves().get(packet.getAddress()).getIdNave());

            sala.getMapIdNaves().remove(packet.getAddress());


            if(sala.getMapIdNaves().size() == 0) {
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return Transformer.classToJson(sala.getConnectedPersons()).getBytes();
    }

    private DataToRecive naveToRemove;
    private String deadData(DatagramPacket packet){
        try {
            final Sala sala = salas.get(Transformer.packetDataToString(packet).split(":")[1]);

            if(sala.getNavesVivas()[sala.getMapIdNaves().get(packet.getAddress()).getIdNave()]) {
                sala.getNaves().forEach(nave -> {
                    if (nave.getIdNave() == sala.getMapIdNaves().get(packet.getAddress()).getIdNave()) {
                        naveToRemove = nave;
                    }
                });
                sala.getNaves().remove(naveToRemove);
            }

            return Transformer.classToJson(sala.getNaves());

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return "ERROR";
    }


    private String updateJsonGame(DatagramPacket packet) throws UnsupportedEncodingException {
        DataToRecive receivedData = Transformer.jsonToNaveToRecive(Transformer.packetDataToString(packet));
        Sala sala = salas.get(receivedData.getIdSala());


        if(!sala.getNaves().contains(receivedData)) {
            sala.getNaves().add(receivedData);
        }

        //receivedData.getNaveArmaBalas().forEach(balaToSend -> System.out.println(balaToSend.getAngle()));

        if(receivedData.getNavesTocadas() != null || receivedData.getNavesTocadas().size() != 0) {
            sala.getNaves().forEach(nave -> {
                nave.setLifes(sala.getVidasNaves()[nave.getIdNave()]);
                receivedData.getNavesTocadas().forEach(naveTocada -> {
                    if (nave.getIdNave() == naveTocada) {
                        //RESTAMOS UNA VIDA A LA NAVE QUE HA SIDO TOCADA
                        nave.setLifes(--sala.getVidasNaves()[naveTocada]);

                        if(nave.getLifes() <= 0 && nave.getState() == Enums.NaveState.ALIVE){
                            nave.setState(Enums.NaveState.DYING);
                        }

                        //AÑADIMOS UNA VIDA A LA NAVE QUE HA TOCADO A LA OTRA
                        if(sala.getVidasNaves()[receivedData.getIdNave()] < Ajustes.MAX_LIFES) {
                            sala.getVidasNaves()[receivedData.getIdNave()]++;
                        }
                    }
                });
            });
        }

        receivedData.setState(sala.getNavesState()[receivedData.getIdNave()]);
        receivedData.setLifes(sala.getVidasNaves()[receivedData.getIdNave()]);
        sala.getNaves().set(sala.getNaves().indexOf(receivedData), receivedData);

        //naves.forEach(nave-> System.out.println(nave.toString()));
        return Transformer.classToJson(sala.getNaves());
    }

    private String signalToStart(DatagramPacket packet) {
        //sendAll("Start", packet);
        return "Starting";
    }

    //ESTO SE HARÁ CUANDO se conecte a una sala y tiene que devolver una sala.
    private String getIdOfNaveClient(DatagramPacket packet){
        /**
         * 1. Asignar ID a la Nave.
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
            //SI NO CONTIENE LA IP DE EL CLIENTE && El límite de naves es inferior a 4
            if (!salas.get(numSala).getMapIdNaves().containsKey(packet.getAddress()) && salas.get(numSala).getMapIdNaves().size() < 4) {

                salasToSend.get(numSala).addNumPlayers();

                int id;
                for (id = 1; id <= 4; id++) {
                    if (!salas.get(numSala).getConnectedPersons()[id]) {
                        salas.get(numSala).addAConnectedPerson(id);
                        break;
                    }
                }
                salas.get(numSala).getMapIdNaves().put(packet.getAddress(), new ClientData(id, packet.getPort()));

                return String.valueOf(id + ":" + numSala);

                //CAMBIAR: Este else if dejará de existir ya que cuando te salgas de la sala se borrarán todos los datos.
            } else if(salas.get(numSala).getMapIdNaves().containsKey(packet.getAddress())){
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

        sala.getMapIdNaves().put(packet.getAddress(),new ClientData(sala.getMapIdNaves().size()+1, packet.getPort()));

        salas.put(sala.getIdSala(), sala);

        salasToSend.put(sala.getIdSala(), new SalaToSend(sala.getIdSala()));

        sala.addAConnectedPerson(sala.getMapIdNaves().get(packet.getAddress()).getIdNave());

        return String.valueOf(sala.getIdSala());
    }


//    private void sendAll(String signal, DatagramPacket packet) {
//        salas.get(numSala).getMapIdNaves().forEach((ip,clientData)-> {
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
    //        System.out.println(updateJsonGame("{\"idNave\":1,\"navePosX\":500.0,\"navePosY\":500.0,\"naveCursorPosX\":1381.6,\"naveCursorPosY\":17.6,\"angle\":59.0,\"naveArmaBalas\":[{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idNave\":1},{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idNave\":1},{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idNave\":1}],\"timer\":{\"max\":10.0,\"time\":2.299999999079997},\"prueba\":0}\n".getBytes()));
    //        System.out.println(updateJsonGame("{\"idNave\":2,\"navePosX\":500.0,\"navePosY\":500.0,\"naveCursorPosX\":1381.6,\"naveCursorPosY\":17.6,\"angle\":59.0,\"naveArmaBalas\":[{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idNave\":1},{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idNave\":1},{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idNave\":1}],\"timer\":{\"max\":10.0,\"time\":2.299999999079997},\"prueba\":0}\n".getBytes()));
    //
    //    }
}