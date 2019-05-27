package game.model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import statVars.AjustesNave;
import statVars.Enums;
import javafx.scene.canvas.GraphicsContext;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class Arma {
    ///////UTILIZAR UN ARRAY DE BALAS Y QUE SE QUITEN CUANDO HAYAN SALIDO DE LA PANTALLA


    private ArrayList<Bala> balas;

    private ArrayList<Bala> balasToRemove;

    private GraphicsContext graphicsContext;
    private int idBalaActual;

    private int balasDisponibles;
    private final int MAX_BALAS = AjustesNave.MAX_AMMO;

    private Media soundBala;
    private Media soundOutOfAmmo;

    private Timer reloadTimer;


    private ImageView[] imgAmmoBalas;


    public Arma(GraphicsContext graphicsContext, Pane pane){
        imgAmmoBalas = new ImageView[MAX_BALAS];
        ///IMAGENES A LAS VIDAS
        for (int i = 0; i<MAX_BALAS; i++) {
            ImageView imagen = new ImageView("game/res/img/bala.png");
            imagen.setX(125 + (imagen.getImage().getWidth() + 20) * i+1);
            imagen.setY(85);
            pane.getChildren().add(imagen);
            imgAmmoBalas[i] = imagen;
        }

        reloadTimer = new Timer(0.75);

        balasDisponibles = 3;

        try {
            soundBala = new Media(getClass().getClassLoader().getResource("game/res/audio/chipium.mp3").toURI().toString());
            soundOutOfAmmo = new Media(getClass().getClassLoader().getResource("game/res/audio/outOfAmmo.mp3").toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        this.graphicsContext = graphicsContext;
        balas = new ArrayList<>();
    }

    public void shoot(double x, double y, double cc, double co, double angle) {
        if(balasDisponibles != 0) {
            if(balasDisponibles == MAX_BALAS){
                reloadTimer.setTime(0);
            }
            //Executors.newFixedThreadPool(4).execute(()->new MediaPlayer(soundBala).play());
            balasDisponibles--;
            balas.add(new Bala(graphicsContext, x, y, cc, co, angle, addIdToBala()));
        }else {
            //Executors.newFixedThreadPool(4).execute(()->new MediaPlayer(soundOutOfAmmo).play());
        }
    }

    private int addIdToBala(){
        if(idBalaActual > 10){
            idBalaActual = 0;
        }
        idBalaActual++;
        return idBalaActual;
    }

    //borra las balas
    private void removeOOSBalas() {
        balasToRemove = new ArrayList<>();
        balas.forEach(bala -> {
            if(bala.getState() == Enums.BulletState.TO_REMOVE) {
                if(!balasToRemove.contains(bala)) {
                    balasToRemove.add(bala);
                }
            }
        });
        balasToRemove.forEach(bala -> balas.remove(bala));
    }

    public void update(double time){
        removeOOSBalas();
        reloadTimer.update(time);
        //System.out.println(reloadTimer.check());
        if(reloadTimer.check() && balasDisponibles != MAX_BALAS){
            balasDisponibles++;
        }

        if(!balas.isEmpty()) {
            balas.forEach(Bala::update);
        }
    }

    public void render(){
        for (int i = 0; i < MAX_BALAS; i++) {
            if(i<balasDisponibles) {
                imgAmmoBalas[i].setOpacity(1);
            }else{
                imgAmmoBalas[i].setOpacity(0.5);
            }
        }

        if(!balas.isEmpty()) {
            balas.forEach(Bala::render);
        }
    }

    public ArrayList<Bala> getBalas() {
        return balas;
    }

    public ArrayList<Bala> getBalasToRemove() {
        return balasToRemove;
    }
}
