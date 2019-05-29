package game.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import statVars.Ajustes;

import java.util.ArrayList;

public class Animacion {

    private ImageView[] meteorAnimation;
    private ImageView[][] naveDeathAnimation;
    private ImageView[][] naveShootAnimation;

    private int frame;

    public Animacion() {
        frame = 1;

        meteorAnimation= new ImageView[Ajustes.METEORITODESTRUIR_LENGHT];
        naveDeathAnimation = new ImageView[Ajustes.NUM_NAVES][Ajustes.NAVEDESTRUIR_LENGHT];
        naveShootAnimation = new ImageView[Ajustes.NUM_NAVES][Ajustes.NAVESHOOT_LENGHT];

        for (int i = 1; i < Ajustes.METEORITODESTRUIR_LENGHT; i++) {
            meteorAnimation[i] = new ImageView("game/res/img/meteorDestroy/meteor_animation_"+ i +".png");
        }

        for (int i = 0; i < Ajustes.NUM_NAVES ; i++) { // i = id Nave - 1
            for (int j = 0; j <Ajustes.NAVEDESTRUIR_LENGHT ; j++) { // j = num de la imagen
                System.out.println(i+" "+ j);
                naveDeathAnimation[i][j] = new ImageView("game/res/img/navesDestroy/navePlayer_" + (i+1) + "/nave"+(i+1)+"_destroy_"+ j +".png");
            }
        }

    }

    /***
     *
     * TODO : FALTA ARRAY[] de las imagenes del disparo.
     */

    public ImageView naveDisparo(int id) {
        return new ImageView("game/res/img/navesDisparo/nave" + id + "/nave"+id+"_disparo_"+ ++frame +".png");
    }

    public ImageView naveDestruir(int id){
        return naveDeathAnimation[id-1][frame++];
    }

    public ImageView meteoritoDestruido(){
        return meteorAnimation[frame++];
    }

    public int getFrame() {
        return frame;
    }

    public void finalAnimacion(){
        frame = 1;
    }
}
