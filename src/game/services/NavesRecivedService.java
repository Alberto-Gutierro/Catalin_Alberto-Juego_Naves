package game.services;

import formatClasses.DataToRecive;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NavesRecivedService {
    private Map<Integer, Image> imagenRotadaOtrasNaves;
    private Map<Integer, ImageView> imagenOtrasNaves;
    private SnapshotParameters snapshotParameters;
    private SnapshotParameters snapshotParametersBalas;

    private Text[] scores;// score_p1,score_p2,score_p3,score_p4;

    private ImageView imagenBala;

    private ArrayList<DataToRecive> navesRecived;

    private GraphicsContext graphicsContext;

    private int myNaveId;

    public NavesRecivedService(GraphicsContext graphicsContext, int myNaveId, Text score_p1, Text score_p2, Text score_p3, Text score_p4) {
        this.scores = new Text[]{score_p1, score_p2, score_p3, score_p4};
//        this.score_p1 = score_p1;
//        this.score_p2 = score_p2;
//        this.score_p3 = score_p3;
//        this.score_p4 = score_p4;

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

    public void setNavesRecived(ArrayList<DataToRecive> navesRecived) {
        this.navesRecived = navesRecived;
    }

    public int getMyLives(){

        for (DataToRecive naveToRecive : navesRecived) {
            if(myNaveId == naveToRecive.getIdNave()){
                System.out.println(naveToRecive.getLives());
                return naveToRecive.getLives();
            }
        }
        return -1;
    }

    public void renderNavesRecibidas(){
        navesRecived.forEach(nave->{
            scores[nave.getIdNave()-1].setText(String.valueOf(nave.getScore()));
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

    private void renderRecivedData(DataToRecive nave) {
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

    public ArrayList<DataToRecive> getNavesRecived() {
        return navesRecived;
    }

    public Map<Integer, Image> getImagenRotadaOtrasNaves() {
        return imagenRotadaOtrasNaves;
    }

    public Map<Integer, ImageView> getImagenOtrasNaves() {
        return imagenOtrasNaves;
    }
}
