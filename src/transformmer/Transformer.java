package transformmer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import formatClasses.DataToRecive;
import server.model.LobbyData;
import server.model.SalaToSend;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Map;

public abstract class Transformer {
    public static String classToJson(Object object){
        return new Gson().toJson(object);
    }

    public static ArrayList<DataToRecive> jsonToArrayListShips(String json) {
        return new Gson().fromJson(json, new TypeToken<ArrayList<DataToRecive>>(){}.getType());
    }

    public static String packetDataToString(DatagramPacket packet) throws UnsupportedEncodingException {
        //return String.valueOf(new String(data, StandardCharsets.UTF_8));
        return new String(packet.getData(),
                packet.getOffset(),
                packet.getLength(), "UTF-8");
    }

    public static DataToRecive jsonToShipToRecive(String json) {
        return new Gson().fromJson(json, DataToRecive.class);
    }

    public static Map<String, SalaToSend> jsonToMapSalas(String json){
        return new Gson().fromJson(json, new TypeToken<Map<String, SalaToSend>>(){}.getType());
    }

    public static LobbyData jsonToLobbyData(String json) {
        return new Gson().fromJson(json, LobbyData.class);
    }
}
