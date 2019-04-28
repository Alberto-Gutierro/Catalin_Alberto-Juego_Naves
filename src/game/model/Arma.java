package game.model;

import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import statVars.Enums;
import javafx.scene.canvas.GraphicsContext;

import java.io.File;
import java.util.ArrayList;

public class Arma {
    ///////UTILIZAR UN ARRAY DE BALAS Y QUE SE QUITEN CUANDO HAYAN SALIDO DE LA PANTALLA


    private ArrayList<Bala> balas;

    private ArrayList<Bala> balasToRemove;

    private GraphicsContext graphicsContext;
    private int idBalaActual;

    private int balasDisponibles;
    private final int MAX_BALAS = 3;

    private Media soundBala;
    private Media soundOutOfAmmo;

    private Timer reloadTimer;

    private Image imgAmmoBala;


    public Arma(GraphicsContext graphicsContext){
        imgAmmoBala = new Image("game/res/img/bala.png");

        reloadTimer = new Timer(0.75);

        balasDisponibles = 3;

        soundBala = new Media(new File("src/game/res/audio/chipium.mp3").toURI().toString());
        soundOutOfAmmo = new Media(new File("src/game/res/audio/outOfAmmo.mp3").toURI().toString());

        this.graphicsContext = graphicsContext;
        balas = new ArrayList<>();
    }

    public void shoot(double x, double y, double cc, double co, double angle) {
        if(balasDisponibles != 0) {
            if(balasDisponibles == MAX_BALAS){
                reloadTimer.setTime(0);
            }
            new MediaPlayer(soundBala).play();
            balasDisponibles--;
            balas.add(new Bala(graphicsContext, x, y, cc, co, angle, addIdToBala()));
        }else {
            new MediaPlayer(soundOutOfAmmo).play();
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
        for (int i = 0; i < balasDisponibles; i++) {
            graphicsContext.drawImage(imgAmmoBala, 125 + 20*i, 85);
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
