package game.model;

import javafx.scene.image.ImageView;
import statVars.Ajustes;

public class Animacion {

    private ImageView[] meteorAnimation;
    private ImageView[][] naveDeathAnimation;
    private ImageView[][] naveShootAnimation;

    private int frames;

    private int nFrames;

    private int frame;

    public Animacion() {
//        frames = 0;
//
//        nFrames = 0;

        frame = 0;

        meteorAnimation= new ImageView[Ajustes.METEORITODESTRUIR_LENGHT];
        naveDeathAnimation = new ImageView[Ajustes.NUM_NAVES][Ajustes.NAVEDESTRUIR_LENGHT];
        naveShootAnimation = new ImageView[Ajustes.NUM_NAVES][Ajustes.NAVESHOOT_LENGHT];

        for (int i = 0; i < Ajustes.METEORITODESTRUIR_LENGHT; i++) {
            meteorAnimation[i] = new ImageView("game/res/img/meteorDestroy/meteor_animation_"+ (i+1) +".png");
        }

        for (int i = 0; i < Ajustes.NUM_NAVES ; i++) { // i = id Nave - 1
            for (int j = 0; j < Ajustes.NAVEDESTRUIR_LENGHT; j++) { // j = num de la imagen
                naveDeathAnimation[i][j] = new ImageView("game/res/img/navesDestroy/navePlayer_" + (i+1) + "/nave"+(i+1)+"_destroy_"+ j +".png");
            }
            for (int j = 0; j < Ajustes.NAVESHOOT_LENGHT; j++) {
                naveShootAnimation[i][j] = new ImageView("game/res/img/navesDisparo/navePlayer_" + (i+1) + "/nave"+(i+1)+"_disparo_"+ j +".png");
            }
        }
    }

    public ImageView naveDisparo(int id) {
//        if(frames>=nFrames){
//            frames = 0;
//        }
//        frames++;
        return naveShootAnimation[id-1][frame++];

        //return naveShootAnimation[id-1][frames>nFrames?frame++:frame];
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
