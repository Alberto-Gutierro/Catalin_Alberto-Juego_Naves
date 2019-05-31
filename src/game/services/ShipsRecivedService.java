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

public class ShipsRecivedService {
    private SnapshotParameters snapshotParameters;
    private SnapshotParameters snapshotParametersBullets;

    private Text[] scores;// score_p1,score_p2,score_p3,score_p4;

    private ImageView imagenBullet;

    private ArrayList<DataToRecive> shipsRecived;

    private GraphicsContext graphicsContext;

    private int myShipId;

    private int myLifes;

    private Enums.ShipState myState;

    private ImageView[] imagenOtrasShips;
    private Image[] imagenRotadaOtrasShips;

    private Animacion animations;


    public ShipsRecivedService(GraphicsContext graphicsContext, int myShipId, Text score_p1, Text score_p2, Text score_p3, Text score_p4) {
        this.scores = new Text[]{score_p1, score_p2, score_p3, score_p4};
//        this.score_p1 = score_p1;
//        this.score_p2 = score_p2;
//        this.score_p3 = score_p3;
//        this.score_p4 = score_p4;

        animations = new Animacion();

        imagenOtrasShips = new ImageView[Ajustes.NUM_NAVES+1];
        imagenRotadaOtrasShips = new Image[Ajustes.NUM_NAVES+1];

        for (int i = 1; i <= Ajustes.NUM_NAVES; i++) {
            imagenOtrasShips[i] = new ImageView("game/res/img/ships/shipPlayer_" + i + ".png");
            imagenRotadaOtrasShips[i] = new Image("game/res/img/ships/shipPlayer_" + i + ".png");

        }
        myLifes = Ajustes.START_LIFES;

        this.myShipId = myShipId;

        this.graphicsContext = graphicsContext;

        snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);

        snapshotParametersBullets = new SnapshotParameters();
        snapshotParametersBullets.setFill(Color.TRANSPARENT);

        imagenBullet = new ImageView("game/res/img/bullet.png");
    }

    public void setShipsRecived(ArrayList<DataToRecive> shipsRecived) {
        this.shipsRecived = shipsRecived;
    }

    public int getMyLifes(){
        return myLifes;
    }

    public void renderShipsRecibidas(){
        shipsRecived.forEach(ship->{
            scores[ship.getIdShip() - 1].setText(String.valueOf(ship.getScore()));
            if (myShipId != ship.getIdShip()) {
                if(ship.getState() != Enums.ShipState.DEAD) {
                    renderRecivedData(ship);
                }
            }else {
                myLifes = ship.getLifes();
                myState = ship.getState();
            }
        });

    }

    private void renderRecivedData(DataToRecive ship) {
        if(ship.getState() == Enums.ShipState.ALIVE) {
            rotateShipRecibida(ship.getIdShip(), ship.getAngle());
            graphicsContext.drawImage(imagenRotadaOtrasShips[ship.getIdShip()], ship.getShipPosX(), ship.getShipPosY());
        }else if(ship.getState() == Enums.ShipState.DYING) {
            if (animations.getFrame() < Ajustes.NAVEDESTRUIR_LENGHT) {
                imagenOtrasShips[ship.getIdShip()] = animations.shipDestruir(ship.getIdShip());
            }
            else {
                animations.finalAnimacion();
            }
            rotateShipRecibida(ship.getIdShip(), ship.getAngle());
            graphicsContext.drawImage(imagenRotadaOtrasShips[ship.getIdShip()], ship.getShipPosX(), ship.getShipPosY());
        }
        ship.getShipWeaponBullets().forEach(bullet -> {
            graphicsContext.drawImage(rotateBulletRecibida(bullet.getAngle()), bullet.getPosX(), bullet.getPosY());
        });
    }

    private void rotateShipRecibida(int id, double angle){
        imagenOtrasShips[id].setRotate(angle);
        imagenRotadaOtrasShips[id] = imagenOtrasShips[id].snapshot(snapshotParameters, null);
    }

    private Image rotateBulletRecibida(double angle){
        imagenBullet.setRotate(angle);
        return imagenBullet.snapshot(snapshotParameters, null);
    }

    public ArrayList<DataToRecive> getShipsRecived() {
        return shipsRecived;
    }

    public Image[] getImagenRotadaOtrasShips() {
        return imagenRotadaOtrasShips;
    }

    public ImageView[] getImagenOtrasShips() {
        return imagenOtrasShips;
    }

    public Enums.ShipState getMyState() {
        return myState;
    }
}
