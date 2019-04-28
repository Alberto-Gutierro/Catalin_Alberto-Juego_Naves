package game.services;

import formatClasses.NaveToRecive;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NavesRecivedService {
    private Map<Integer, Image> imagenRotadaOtrasNaves;
    private Map<Integer, ImageView> imagenOtrasNaves;
    private SnapshotParameters snapshotParameters;
    private SnapshotParameters snapshotParametersBalas;

    private ImageView imagenBala;

    private ArrayList<NaveToRecive> navesRecived;

    private GraphicsContext graphicsContext;

    private int myNaveId;

    public NavesRecivedService(GraphicsContext graphicsContext, int myNaveId) {
        this.myNaveId = myNaveId;

        this.graphicsContext = graphicsContext;

        imagenOtrasNaves = new HashMap<>();
        imagenRotadaOtrasNaves = new HashMap<>();
        snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);

        snapshotParametersBalas = new SnapshotParameters();
        snapshotParametersBalas.setFill(Color.TRANSPARENT);

        imagenBala = new ImageView("game/res/img/bala.png");
    }

    public void setNavesRecived(ArrayList<NaveToRecive> navesRecived) {
        this.navesRecived = navesRecived;
    }

    public int getMyLives(){

        for (NaveToRecive naveToRecive : navesRecived) {
            System.out.println(naveToRecive.getIdNave());
            if(myNaveId == naveToRecive.getIdNave()){
                System.out.println(naveToRecive.getLives());
                return naveToRecive.getLives();
            }
        }
        return -1;
    }

    public void renderNavesRecibidas(){
        navesRecived.forEach(nave->{
            if(myNaveId != nave.getIdNave()) {
                if (!imagenOtrasNaves.containsKey(nave.getIdNave())) {
                    imagenOtrasNaves.put(nave.getIdNave(), new ImageView("game/res/img/naves/navePlayer_" + nave.getIdNave() + ".png"));
                    imagenRotadaOtrasNaves.put(nave.getIdNave(), new Image("game/res/img/naves/navePlayer_" + nave.getIdNave() + ".png"));
//                        rotateNaveRecibida(nave.getIdNave(), nave.getAngle());
//                        graphicsContext.drawImage(imagenRotadaOtrasNaves.get(nave.getIdNave()), nave.getNavePosX(), nave.getNavePosY());
//
//                        nave.getNaveArmaBalas().forEach(bala -> {
//                            graphicsContext.drawImage(rotateBalaRecibida(bala.getAngle()), bala.getPosX(), bala.getPosY());
//
//                            System.out.println(bala.getPosX() + "  " + bala.getPosY());
//                            System.out.println(bala.getAngle());
//                        });

                    renderRecivedData(nave);
                }else {
                    renderRecivedData(nave);
                }
            }
        });

    }

    private void renderRecivedData(NaveToRecive nave) {
        rotateNaveRecibida(nave.getIdNave(), nave.getAngle());
        graphicsContext.drawImage(imagenRotadaOtrasNaves.get(nave.getIdNave()), nave.getNavePosX(), nave.getNavePosY());
        nave.getNaveArmaBalas().forEach(bala -> {
            graphicsContext.drawImage(rotateBalaRecibida(bala.getAngle()), bala.getPosX(), bala.getPosY());
        });
    }

    private void rotateNaveRecibida(int id, double angle){
        imagenOtrasNaves.get(id).setRotate(angle);
        imagenRotadaOtrasNaves.put(id, imagenOtrasNaves.get(id).snapshot(snapshotParameters, null));
    }

    private Image rotateBalaRecibida(double angle){
        imagenBala.setRotate(angle);
        return imagenBala.snapshot(snapshotParameters, null);
    }

    public ArrayList<NaveToRecive> getNavesRecived() {
        return navesRecived;
    }

    public Map<Integer, Image> getImagenRotadaOtrasNaves() {
        return imagenRotadaOtrasNaves;
    }

    public Map<Integer, ImageView> getImagenOtrasNaves() {
        return imagenOtrasNaves;
    }
}
