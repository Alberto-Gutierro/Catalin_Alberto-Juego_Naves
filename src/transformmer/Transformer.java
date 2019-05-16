package transformmer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import formatClasses.DataToRecive;
import server.model.Sala;

import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.Map;

public abstract class Transformer {
    public static String classToJson(Object object){
        return new Gson().toJson(object);
    }

    public static ArrayList<DataToRecive> jsonToArrayListNaves(String json) {
        return new Gson().fromJson(json, new TypeToken<ArrayList<DataToRecive>>(){}.getType());
    }

    public static String packetDataToString(DatagramPacket packet) throws UnsupportedEncodingException {
        //return String.valueOf(new String(data, StandardCharsets.UTF_8));
        return new String(packet.getData(),
                packet.getOffset(),
                packet.getLength(), "UTF-8");
    }

    public static DataToRecive jsonToNaveToRecive(String json) {
        return new Gson().fromJson(json, DataToRecive.class);
    }

    public static Map<Integer, Sala> jsonToMapSalas(String json){
        return new Gson().fromJson(json, new TypeToken<Map<Integer, Sala>>(){}.getType());
    }

    public static Sala jsonToSala(String json){
        return new Gson().fromJson(json, Sala.class);
    }
}
