package game.services;

import formatClasses.DataToRecive;
import game.model.Animacion;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import statVars.Ajustes;
import statVars.Enums;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NavesRecivedService {
    private SnapshotParameters snapshotParameters;
    private SnapshotParameters snapshotParametersBalas;

    private Text[] scores;// score_p1,score_p2,score_p3,score_p4;

    private ImageView imagenBala;

    private ArrayList<DataToRecive> navesRecived;

    private GraphicsContext graphicsContext;

    private int myNaveId;

    private int myLifes;

    private Enums.NaveState myState;

    private ImageView[] imagenOtrasNaves;
    private Image[] imagenRotadaOtrasNaves;

    private Animacion animations;


    public NavesRecivedService(GraphicsContext graphicsContext, int myNaveId, Text score_p1, Text score_p2, Text score_p3, Text score_p4) {
        this.scores = new Text[]{score_p1, score_p2, score_p3, score_p4};
//        this.score_p1 = score_p1;
//        this.score_p2 = score_p2;
//        this.score_p3 = score_p3;
//        this.score_p4 = score_p4;



        animations = new Animacion();

        imagenOtrasNaves = new ImageView[Ajustes.NUM_NAVES+1];
        imagenRotadaOtrasNaves = new Image[Ajustes.NUM_NAVES+1];

        for (int i = 1; i <= Ajustes.NUM_NAVES; i++) {
            imagenOtrasNaves[i] = new ImageView("game/res/img/naves/navePlayer_" + i + ".png");
            imagenRotadaOtrasNaves[i] = new Image("game/res/img/naves/navePlayer_" + i + ".png");

        }
        myLifes = Ajustes.START_LIFES;

        this.myNaveId = myNaveId;

        this.graphicsContext = graphicsContext;

        snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);

        snapshotParametersBalas = new SnapshotParameters();
        snapshotParametersBalas.setFill(Color.TRANSPARENT);

        imagenBala = new ImageView("game/res/img/bala.png");
    }

    public void setNavesRecived(ArrayList<DataToRecive> navesRecived) {
        this.navesRecived = navesRecived;
    }

    public int getMyLifes(){
        return myLifes;
    }

    public void renderNavesRecibidas(){
        navesRecived.forEach(nave->{
            scores[nave.getIdNave()-1].setText(String.valueOf(nave.getScore()));
            if(myNaveId != nave.getIdNave()) {
                renderRecivedData(nave);
            }else {
                myLifes = nave.getLifes();
                myState = nave.getState();
            }
        });

    }

    private void renderRecivedData(DataToRecive nave) {
        rotateNaveRecibida(nave.getIdNave(), nave.getAngle());
        graphicsContext.drawImage(imagenRotadaOtrasNaves[nave.getIdNave()], nave.getNavePosX(), nave.getNavePosY());
        nave.getNaveArmaBalas().forEach(bala -> {
            graphicsContext.drawImage(rotateBalaRecibida(bala.getAngle()), bala.getPosX(), bala.getPosY());
        });
    }

    private void rotateNaveRecibida(int id, double angle){
        imagenOtrasNaves[id].setRotate(angle);
        imagenRotadaOtrasNaves[id] = imagenOtrasNaves[id].snapshot(snapshotParameters, null);
    }

    private Image rotateBalaRecibida(double angle){
        imagenBala.setRotate(angle);
        return imagenBala.snapshot(snapshotParameters, null);
    }

    public ArrayList<DataToRecive> getNavesRecived() {
        return navesRecived;
    }

    public Image[] getImagenRotadaOtrasNaves() {
        return imagenRotadaOtrasNaves;
    }

    public ImageView[] getImagenOtrasNaves() {
        return imagenOtrasNaves;
    }

    public Enums.NaveState getMyState() {
        return myState;
    }
}
