package game.services;

import game.model.Animacion;
import javafx.scene.image.ImageView;
import statVars.Ajustes;
import statVars.Enums;
import statVars.Resoluciones;
import game.model.Meteorito;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

public class MeteorService {

    private ArrayList<Meteorito> meteoritos = new ArrayList<>();

    private ImageView imgMeteorito;

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
        imgMeteorito = new ImageView("game/res/img/img_meteorito.png");
    }

    public void create(double xNave, double yNave, double speed) {

        double posX = (int) (Math.random() * screenWidth);
        double posY = (int) (Math.random() * screenHeight);

        switch ((int)(Math.random()*4)) {

            case NORTH: posY= 0 - Resoluciones.LINEA_DESTRUCCION; break;
            case EAST: posX = screenWidth + Resoluciones.LINEA_DESTRUCCION; break;
            case SOUTH: posY = screenHeight + Resoluciones.LINEA_DESTRUCCION; break;
            default: posX = 0 - Resoluciones.LINEA_DESTRUCCION;
        }
        meteoritos.add(new Meteorito(posX, posY, xNave, yNave, speed, graphicsContext, imgMeteorito));
    }

    public void update() {
        removeMeteor();
        if (!meteoritos.isEmpty()){
            meteoritos.forEach(Meteorito::update);
        }

    }

    public void render(){
        if (!meteoritos.isEmpty()) meteoritos.forEach(Meteorito::render);
    }

    private void removeMeteor(){
        ArrayList<Meteorito> meteorToRemove = new ArrayList<>();
        meteoritos.forEach(meteorito -> {
            if(meteorito.getState() == Enums.MeteorState.TO_REMOVE) {
                if (animacion.getFrame() < Ajustes.METEORITODESTRUIR_LENGHT){
                    meteorito.setImgMeteoritoRotada(animacion.meteoritoDestruido());
                }else {
                    animacion.finalAnimacion();
                    meteorito.setState(Enums.MeteorState.DEAD);
                }
            }

            if (meteorito.getState() == Enums.MeteorState.DEAD) {
                if(!meteorToRemove.contains(meteorito)) {
                    meteorToRemove.add(meteorito);
                }
            }
        });
        meteorToRemove.forEach(meteorito -> meteoritos.remove(meteorito));
    }

    public ArrayList<Meteorito> getMeteoritos() {
        return meteoritos;
    }
}
