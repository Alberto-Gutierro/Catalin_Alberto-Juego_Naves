package server;

import statVars.Packets;
import server.model.ClientData;
import formatClasses.NaveToRecive;
import transformmer.Transformer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerGame {

    private DatagramSocket socket;

    private ArrayList<NaveToRecive> naves;

    private Map<InetAddress, ClientData> mapIdNaves;

    private Map<Integer, Integer> livesNaves;

    private boolean[] navesVivas = {false, true, true, true, true};
    private int[] vidasNaves = {-1, 3, 3, 3, 3};

    public void init(int port) throws SocketException {
        socket = new DatagramSocket(port);
        naves = new ArrayList<>();
        mapIdNaves = new HashMap<>();
        livesNaves = new HashMap<>();
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
                System.out.println(clientIP.getHostAddress());
                packet = new DatagramPacket(sendingData, sendingData.length, clientIP, clientPort);
                //System.out.println(new String(respuesta, Charset.defaultCharset()));

                //enviament de la resposta
                socket.send(packet);
            }
        }
    }

    private byte[] processData(DatagramPacket packet) {

        /**
         * 1. Esperar a recibir todos los JSON
         * 2. Montar el Json para recoger los datos
         * 3. Comparar Todos los Json
         * 4. Montar un Json Actualizado
         * 5. devolver un byte[]
         */
        try {
            //POR AQUI: EL SERVIDOR RECIBE UN ARRAY Y NO UN OBJETO.
            System.out.println(Transformer.packetDataToString(packet));
            switch (Transformer.packetDataToString(packet)) {
                case "Connect":
                    return getIdOfNaveClient(packet).getBytes();
                case "Start":
                    return signalToStart().getBytes();
                case "Dead":
                    return deadData(packet.getAddress()).getBytes();
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

    private NaveToRecive naveToRemove;
    private String deadData(InetAddress ipClient){
        if(navesVivas[mapIdNaves.get(ipClient).getIdNave()]) {
            naves.forEach(nave -> {
                if (nave.getIdNave() == mapIdNaves.get(ipClient).getIdNave()) {
                    naveToRemove = nave;
                }
            });
            naves.remove(naveToRemove);
        }

        return Transformer.classToJson(naves);
    }


    private String updateJsonGame(DatagramPacket packet) throws UnsupportedEncodingException {
        NaveToRecive naveRecibida = Transformer.jsonToNaveToRecive(Transformer.packetDataToString(packet));

        //naveRecibida.getNaveArmaBalas().forEach(balaToSend -> System.out.println(balaToSend.getAngle()));

        if(naves.contains(naveRecibida)){
            naves.set(naves.indexOf(naveRecibida), naveRecibida);
        }else {
            naves.add(naveRecibida);
        }
        if(naveRecibida.getNavesTocadas() != null || naveRecibida.getNavesTocadas().size() == 0) {
            naves.forEach(nave -> {
                nave.setLives(vidasNaves[nave.getIdNave()]);
                naveRecibida.getNavesTocadas().forEach(naveTocada -> {
                    if (nave.getIdNave() == naveTocada) {
                        System.out.println("NAVE: " + nave.getIdNave());
                        System.out.println(nave.getLives());
                        vidasNaves[naveTocada]--;
                        nave.setLives(vidasNaves[naveTocada]);

                        System.out.println(nave.getLives());
                    }
                });
            });
        }


        //naves.forEach(nave-> System.out.println(nave.toString()));
        return Transformer.classToJson(naves);
    }

    private String signalToStart() {
        sendAll("Start");
        return "Starting";
    }


    private String getIdOfNaveClient(DatagramPacket packet){
        /**
         * 1. Asignar ID a la Nave.
         * (Num 1-4)
         *
         */

        if (!mapIdNaves.containsKey(packet.getAddress()) && mapIdNaves.size() <= 4) {
            mapIdNaves.put(packet.getAddress(),new ClientData(mapIdNaves.size()+1, packet.getPort()));
            return String.valueOf(mapIdNaves.size());
        } else if (mapIdNaves.containsKey(packet.getAddress())) {
            return String.valueOf(mapIdNaves.get(packet.getAddress()).getIdNave());
        } else return String.valueOf(0);
    }


    private void sendAll(String signal) {
        mapIdNaves.forEach((ip,clientData)-> {
            try {
                socket.send(new DatagramPacket(signal.getBytes(), signal.getBytes().length, ip, clientData.getPort() ));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }



    //     public void checkJsonCecived(){
    //        System.out.println(updateJsonGame("{\"idNave\":1,\"navePosX\":500.0,\"navePosY\":500.0,\"naveCursorPosX\":1381.6,\"naveCursorPosY\":17.6,\"angle\":59.0,\"naveArmaBalas\":[{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idNave\":1},{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idNave\":1},{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idNave\":1}],\"timer\":{\"max\":10.0,\"time\":2.299999999079997},\"prueba\":0}\n".getBytes()));
    //        System.out.println(updateJsonGame("{\"idNave\":2,\"navePosX\":500.0,\"navePosY\":500.0,\"naveCursorPosX\":1381.6,\"naveCursorPosY\":17.6,\"angle\":59.0,\"naveArmaBalas\":[{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idNave\":1},{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idNave\":1},{\"posX\":535.9119184743529,\"posY\":516.6606853405079,\"idNave\":1}],\"timer\":{\"max\":10.0,\"time\":2.299999999079997},\"prueba\":0}\n".getBytes()));
    //
    //    }
}