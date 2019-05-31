package game.model;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import statVars.Ajustes;
import statVars.Enums;
import javafx.scene.canvas.GraphicsContext;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class Weapon {
    ///////UTILIZAR UN ARRAY DE BALAS Y QUE SE QUITEN CUANDO HAYAN SALIDO DE LA PANTALLA


    private ArrayList<Bullet> bullets;

    private ArrayList<Bullet> bulletsToRemove;

    private GraphicsContext graphicsContext;
    private int idBulletActual;

    private int bulletsDisponibles;
    private final int MAX_BALAS = Ajustes.MAX_AMMO;

    private Media soundBullet;
    private Media soundOutOfAmmo;

    private Timer reloadTimer;

    ImageView imgBullet;

    private ImageView[] imgAmmoBullets;


    public Weapon(GraphicsContext graphicsContext, Pane pane){
        imgAmmoBullets = new ImageView[MAX_BALAS];

        imgBullet = new ImageView("game/res/img/bullet.png");

        ///IMAGENES A LAS BALAS
        for (int i = 0; i<MAX_BALAS; i++) {
            ImageView imagen = new ImageView("game/res/img/bullet.png");
            imagen.setX(125 + (imagen.getImage().getWidth() + 20) * i+1);
            imagen.setY(85);
            pane.getChildren().add(imagen);
            imgAmmoBullets[i] = imagen;
        }

        reloadTimer = new Timer(0.75);

        bulletsDisponibles = 3;

        try {
            soundBullet = new Media(getClass().getClassLoader().getResource("game/res/audio/chipium.mp3").toURI().toString());
            soundOutOfAmmo = new Media(getClass().getClassLoader().getResource("game/res/audio/outOfAmmo.mp3").toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        this.graphicsContext = graphicsContext;
        bullets = new ArrayList<>();
    }

    public void shoot(double x, double y, double cc, double co, double angle) {
        if(bulletsDisponibles != 0) {
            if(bulletsDisponibles == MAX_BALAS){
                reloadTimer.setTime(0);
            }
            //Executors.newFixedThreadPool(4).execute(()->new MediaPlayer(soundBullet).play());
            bulletsDisponibles--;
            bullets.add(new Bullet(graphicsContext, x, y, cc, co, angle, addIdToBullet(), imgBullet));
        }else {
            //Executors.newFixedThreadPool(4).execute(()->new MediaPlayer(soundOutOfAmmo).play());
        }
    }

    private int addIdToBullet(){
        if(idBulletActual > 10){
            idBulletActual = 0;
        }
        idBulletActual++;
        return idBulletActual;
    }

    //borra las bullets
    private void removeOOSBullets() {
        bulletsToRemove = new ArrayList<>();
        bullets.forEach(bullet -> {
            if(bullet.getState() == Enums.BulletState.TO_REMOVE) {
                if(!bulletsToRemove.contains(bullet)) {
                    bulletsToRemove.add(bullet);
                }
            }
        });
        bulletsToRemove.forEach(bullet -> bullets.remove(bullet));
    }

    public void update(double time){
        removeOOSBullets();
        reloadTimer.update(time);
        //System.out.println(reloadTimer.check());
        if(reloadTimer.check() && bulletsDisponibles != MAX_BALAS){
            bulletsDisponibles++;
        }

        if(!bullets.isEmpty()) {
            bullets.forEach(Bullet::update);
        }
    }

    public void render(){
        for (int i = 0; i < MAX_BALAS; i++) {
            if(i<bulletsDisponibles) {
                imgAmmoBullets[i].setOpacity(1);
            }else{
                imgAmmoBullets[i].setOpacity(0.5);
            }
        }

        if(!bullets.isEmpty()) {
            bullets.forEach(Bullet::render);
        }
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }

    public ArrayList<Bullet> getBulletsToRemove() {
        return bulletsToRemove;
    }
}
