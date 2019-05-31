package game.services;

import game.model.Animacion;
import javafx.scene.image.ImageView;
import statVars.Ajustes;
import statVars.Enums;
import statVars.Resoluciones;
import game.model.Meteor;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class MeteorService {

    private ArrayList<Meteor> meteors = new ArrayList<>();

    private ImageView imgMeteor;

    private final int NORTH = 0,EAST = 1, SOUTH = 2, WEST = 3;

    private double screenWidth;
    private double screenHeight;
    private GraphicsContext graphicsContext;

    private Animacion animacion;

    public MeteorService(double screenWidth, double screenHeight, GraphicsContext graphicsContext){
        this.graphicsContext=graphicsContext;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        animacion = new Animacion();
        imgMeteor = new ImageView("game/res/img/img_meteor.png");
    }

    public void create(double xShip, double yShip, double speed) {

        double posX = (int) (Math.random() * screenWidth);
        double posY = (int) (Math.random() * screenHeight);

        switch ((int)(Math.random()*4)) {

            case NORTH: posY= 0 - Resoluciones.LINEA_DESTRUCCION; break;
            case EAST: posX = screenWidth + Resoluciones.LINEA_DESTRUCCION; break;
            case SOUTH: posY = screenHeight + Resoluciones.LINEA_DESTRUCCION; break;
            default: posX = 0 - Resoluciones.LINEA_DESTRUCCION;
        }
        meteors.add(new Meteor(posX, posY, xShip, yShip, speed, graphicsContext, imgMeteor));
    }

    public void update() {
        removeMeteor();
        if (!meteors.isEmpty()){
            meteors.forEach(Meteor::update);
        }

    }

    public void render(){
        if (!meteors.isEmpty()) meteors.forEach(Meteor::render);
    }

    private void removeMeteor(){
        ArrayList<Meteor> meteorToRemove = new ArrayList<>();
        meteors.forEach(meteor -> {
            if(meteor.getState() == Enums.MeteorState.TO_REMOVE) {
                if (animacion.getFrame() < Ajustes.METEORITODESTRUIR_LENGHT){
                    meteor.setImgMeteorRotada(animacion.meteorDestruido());
                }else {
                    animacion.finalAnimacion();
                    meteor.setState(Enums.MeteorState.DEAD);
                }
            }

            if (meteor.getState() == Enums.MeteorState.DEAD) {
                if(!meteorToRemove.contains(meteor)) {
                    meteorToRemove.add(meteor);
                }
            }
        });
        meteorToRemove.forEach(meteor -> meteors.remove(meteor));
    }

    public ArrayList<Meteor> getMeteors() {
        return meteors;
    }
}
