package game.model;

import javafx.scene.image.ImageView;
import statVars.Ajustes;

import java.util.ArrayList;

public class Animacion {

    ArrayList<ImageView> meteorAnimation = new ArrayList<>();
    private int frame;

    public Animacion() {
        frame = 0;

        for (int i = 0; i < Ajustes.METEORITODESTRUIR_LENGHT; i++) {
            meteorAnimation.add(new ImageView("game/res/img/meteorDestroy/meteor_animation_"+ i +".png"));
        }
    }

    public ImageView naveDisparo(int id) {
        return new ImageView("game/res/img/navesDestroy/navePlayer_" + id + "/nave"+id+"_destroy_"+ ++frame +".png");
    }

    public ImageView naveDestruir(int id){
        return new ImageView("game/res/img/navesDestroy/navePlayer_" + id + "/nave"+id+"_destroy_"+ ++frame +".png");
    }

    public ImageView meteoritoDestruido(){
        return meteorAnimation.get(++frame);
    }

    public int getFrame() {
        return frame;
    }

    public void finalAnimacion(){
        frame = 0;
    }
}
