package server;

import formatClasses.DataToRecive;
import javafx.scene.transform.Transform;
import server.model.ClientData;
import server.model.Sala;
import statVars.AjustesNave;
import statVars.Packets;
import transformmer.Transformer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Pruebas {

    private DatagramSocket socket;

    private Map<String, Sala> salas;

    public void init(int port) throws SocketException {
        socket = new DatagramSocket(port);
        salas = new HashMap<>();
    }

    public void runServer() throws IOException {
        byte [] receivingData = new byte[Packets.PACKET_LENGHT];
        byte [] sendingData;
        InetAddress clientIP;
        int clientPort;

        //el servidor atén el port indefinidament
        while(true/* No esten todos los jugadores */){

            DatagramPacket packet = new DatagramPacket(receivingData, Packets.PACKET_LENGHT);
            //espera de les dades

            socket.receive(packet);
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

    private byte[] processData(DatagramPacket packet) {
        try {
            System.out.println(Transformer.packetDataToString(packet));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            if(Transformer.packetDataToString(packet).matches("^Room:[1-9]+$")){
                return getIdOfNaveClient(packet).getBytes();
            }else if(Transformer.packetDataToString(packet).matches("^Dead:.+$")){
                return deadData(packet).getBytes();
                //////////////POR AQUI: FALLA EN LOS MATCHES
            }else if(Transformer.packetDataToString(packet).matches("^Waiting:.+$")){
                return waitingData(packet);
            }
            
            //POR AQUI: EL SERVIDOR RECIBE UN ARRAY Y NO UN OBJETO.
            switch (Transformer.packetDataToString(packet)) {
                case "Connect": case "Rooms":
                    System.out.println("CONNECT O ROOMS");
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

    private String getSalas() {
        return Transformer.classToJson(salas);
    }

    private byte[] waitingData(DatagramPacket packet){

        Sala sala = null;
        try {
            sala = salas.get(Transformer.packetDataToString(packet).split(":")[1]);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return String.valueOf(sala.getMapIdNaves().size()).getBytes();
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
        DataToRecive naveRecibida = Transformer.jsonToNaveToRecive(Transformer.packetDataToString(packet));
        Sala sala = salas.get(naveRecibida.getIdSala());
        
        
        if(!sala.getNaves().contains(naveRecibida)) {
            sala.getNaves().add(naveRecibida);
        }

        //naveRecibida.getNaveArmaBalas().forEach(balaToSend -> System.out.println(balaToSend.getAngle()));
        
        if(naveRecibida.getNavesTocadas() != null || naveRecibida.getNavesTocadas().size() == 0) {
            sala.getNaves().forEach(nave -> {
                nave.setLives(sala.getVidasNaves()[nave.getIdNave()]);
                naveRecibida.getNavesTocadas().forEach(naveTocada -> {
                    if (nave.getIdNave() == naveTocada) {
                        //RESTAMOS UNA VIDA A LA NAVE QUE HA SIDO TOCADA
                        nave.setLives(--sala.getVidasNaves()[naveTocada]);

                        //AÑADIMOS UNA VIDA A LA NAVE QUE HA TOCADO A LA OTRA
                        if(sala.getVidasNaves()[naveRecibida.getIdNave()] < AjustesNave.MAX_LIFES) {
                            sala.getVidasNaves()[naveRecibida.getIdNave()]++;
                        }
                    }
                });
            });
        }

        naveRecibida.setLives(sala.getVidasNaves()[naveRecibida.getIdNave()]);
        sala.getNaves().set(sala.getNaves().indexOf(naveRecibida), naveRecibida);

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


        if (!salas.get(numSala).getMapIdNaves().containsKey(packet.getAddress()) && salas.get(numSala).getMapIdNaves().size() < 4) {
            salas.get(numSala).getMapIdNaves().put(packet.getAddress(),new ClientData(salas.get(numSala).getMapIdNaves().size()+1, packet.getPort()));

            //sendAll(String.valueOf(mapIdNaves.size()), packet);

            return String.valueOf(salas.get(numSala).getMapIdNaves().size());
        } else if (salas.get(numSala).getMapIdNaves().containsKey(packet.getAddress())) {
            return String.valueOf(salas.get(numSala).getMapIdNaves().get(packet.getAddress()).getIdNave());
        } else return String.valueOf(0);
    }

    private String createSala(DatagramPacket packet) {
        Sala sala = new Sala(UUID.randomUUID().toString());

        sala.getMapIdNaves().put(packet.getAddress(),new ClientData(sala.getMapIdNaves().size()+1, packet.getPort()));

        salas.put(sala.getIdSala(), sala);

        System.out.println("SALA CREADA" + sala.getIdSala());

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