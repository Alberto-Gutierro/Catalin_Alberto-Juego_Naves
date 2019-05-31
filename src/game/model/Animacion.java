package game.model;

import javafx.scene.image.ImageView;
import statVars.Ajustes;

public class Animacion {

    private ImageView[] meteorAnimation;
    private ImageView[][] shipDeathAnimation;
    private ImageView[][] shipShootAnimation;

    private int frames;

    private int nFrames;

    private int frame;

    public Animacion() {
//        frames = 0;
//
//        nFrames = 0;

        frame = 0;

        meteorAnimation= new ImageView[Ajustes.METEORITODESTRUIR_LENGHT];
        shipDeathAnimation = new ImageView[Ajustes.NUM_NAVES][Ajustes.NAVEDESTRUIR_LENGHT];
        shipShootAnimation = new ImageView[Ajustes.NUM_NAVES][Ajustes.NAVESHOOT_LENGHT];

        for (int i = 0; i < Ajustes.METEORITODESTRUIR_LENGHT; i++) {
            meteorAnimation[i] = new ImageView("game/res/img/meteorDestroy/meteor_animation_"+ (i+1) +".png");
        }

        for (int i = 0; i < Ajustes.NUM_NAVES ; i++) { // i = id Ship - 1
            for (int j = 0; j < Ajustes.NAVEDESTRUIR_LENGHT; j++) { // j = num de la imagen
                shipDeathAnimation[i][j] = new ImageView("game/res/img/shipsDestroy/shipPlayer_" + (i+1) + "/ship"+(i+1)+"_destroy_"+ j +".png");
            }
            for (int j = 0; j < Ajustes.NAVESHOOT_LENGHT; j++) {
                shipShootAnimation[i][j] = new ImageView("game/res/img/shipsShoot/shipPlayer_" + (i+1) + "/ship"+(i+1)+"_disparo_"+ j +".png");
            }
        }
    }

    public ImageView shipShoot(int id) {
//        if(frames>=nFrames){
//            frames = 0;
//        }
//        frames++;
        return shipShootAnimation[id-1][frame++];

        //return shipShootAnimation[id-1][frames>nFrames?frame++:frame];
    }

    public ImageView shipDestruir(int id){
        return shipDeathAnimation[id-1][frame++];
    }

    public ImageView meteorDestruido(){
        return meteorAnimation[frame++];
    }

    public int getFrame() {
        return frame;
    }

    public void finalAnimacion(){
        frame = 1;
    }
}
