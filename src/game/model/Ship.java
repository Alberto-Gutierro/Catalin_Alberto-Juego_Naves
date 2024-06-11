package game.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;

import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import statVars.Ajustes;
import statVars.Enums;

public class Ship {
    private int id;

    private Cursor orientation;

    private int score;

    private double posX;
    private double posY;

    private final int SPEED = 7;
    private int lifes;
    private final int MAX_LIFES = Ajustes.MAX_LIFES;

    private BooleanProperty upPressed, downPressed, rightPressed, leftPressed;
    private BooleanBinding anyPressed;

    private ImageView imgShip;
    private Image imagenRotada;

    private Weapon weapon;

    private SnapshotParameters snapshotParameters;

    private GraphicsContext graphicsContext;

    private ImageView[] imgLifes;

    private Enums.ShipState state;
    private Animacion animacion;

    public Ship(GraphicsContext graphicsContext, Pane pane, int idShip, ImageView imgShip, BooleanProperty upPressed, BooleanProperty downPressed, BooleanProperty rightPressed, BooleanProperty leftPressed, BooleanBinding anyPressed) {
        animacion=new Animacion();
        imgLifes = new ImageView[MAX_LIFES];
        ///IMAGENES A LAS VIDAS
        for (int i = 0; i<MAX_LIFES; i++) {
            ImageView imagen = new ImageView("game/res/img/life.png");
            imagen.setX(120 + (imagen.getImage().getWidth() + 5) * i+1);
            imagen.setY(110);
            pane.getChildren().add(imagen);
            imgLifes[i] = imagen;
        }

        score = 0;

        lifes = Ajustes.START_LIFES;

        this.id = idShip;

        weapon = new Weapon(graphicsContext, pane);
        orientation = new Cursor();

        this.graphicsContext = graphicsContext;

        if(idShip == 1){
            this.posX = 250;
            this.posY = 250;
        }else if(idShip == 2){
            this.posX = pane.getWidth()-250;
            this.posY = 250;
        }else if(idShip == 3){
            this.posX = 250;
            this.posY = pane.getHeight()-250;
        }else {
            this.posX = pane.getWidth()-250;
            this.posY = pane.getHeight()-250;
        }

        this.upPressed = upPressed;
        this.downPressed = downPressed;
        this.rightPressed = rightPressed;
        this.leftPressed = leftPressed;
        this.anyPressed = anyPressed;

        this.imgShip = imgShip;

        this.snapshotParameters = new SnapshotParameters();
        snapshotParameters.setFill(Color.TRANSPARENT);

        state = Enums.ShipState.ALIVE;

    }

    public Weapon getWeapon(){
        return weapon;
    }

    public Cursor getOrientation() {
        return orientation;
    }

    public void setOrientation(double x, double y){
        orientation.setPosX(x);
        orientation.setPosY(y);
    }

    public void setImagenRotada(Image imagenRotada) {
        this.imagenRotada = imagenRotada;
    }

    public Image getImagenRotada() {
        return imagenRotada;
    }

    public ImageView getImgShip(){
        return imgShip;
    }

    public double getPosX() {
        return posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosX(double pos){
        posX = pos;
    }

    public void setPosY(double pos){
        posY = pos;
    }

    public int getId(){
        return id;
    }

    private void mover(){
        if (upPressed.get()) {
            posY = getPosY() - SPEED;
        }
        if (downPressed.get()) {
            posY = getPosY() + SPEED;
        }
        if (leftPressed.get()) {
            posX = getPosX() - SPEED;
        }
        if (rightPressed.get()) {
            posX = getPosX() + SPEED;
        }
    }

    public double getAngle(){
//        double centerX = posX + imgShip.getHeight()/2;
//        double centerY = posY + imgShip.getWidth()/2;
//
//
//        double cc = (centerX - orientation.getPosX());
//        double co = (centerY - orientation.getPosY());
//
//        return Math.toDegrees(Math.atan2(co, cc))-90;
        return Math.round(Math.toDegrees(
                Math.atan2(
                        ((posY + imagenRotada.getWidth()/2) - orientation.getPosY()),
                        ((posX + imagenRotada.getHeight()/2) - orientation.getPosX()))
                )
        )-90;
    }

    private void rotate(){
        double newAngle = getAngle();

        imgShip.setRotate(newAngle);

        imagenRotada = imgShip.snapshot(snapshotParameters, null);
    }

    public void shoot(double cursorX, double cursorY) {
        state = Enums.ShipState.SHOOTING;

        weapon.shoot(
                (posX + imgShip.getImage().getWidth()/2),
                (posY + imgShip.getImage().getHeight()/2),
                (posX + imgShip.getImage().getWidth()/2) - cursorX,
                (posY + imgShip.getImage().getHeight()/2) - cursorY,
                getAngle());


    }

    public void update(double time){
        weapon.update(time);

        if(anyPressed.get()) {
            mover();
        }

        if(state.equals(Enums.ShipState.DYING)) {
            if (animacion.getFrame() < Ajustes.NAVEDESTRUIR_LENGHT) {
                imgShip = animacion.shipDestruir(id);
            }
            else {
                animacion.finalAnimacion();
                state=Enums.ShipState.DEAD;
            }
        } else if (state.equals(Enums.ShipState.SHOOTING)) {
            if (animacion.getFrame() < Ajustes.NAVESHOOT_LENGHT) {
                imgShip = animacion.shipShoot(id);
            }
            else {
                animacion.finalAnimacion();
                state=Enums.ShipState.ALIVE;
            }
        }

        rotate();
    }

    public void render(){
        graphicsContext.drawImage(imagenRotada, posX, posY);

        weapon.render();
        for (int i = 0; i < MAX_LIFES; i++) {
            if(i< lifes) {
                imgLifes[i].setOpacity(1);
            }else{
                imgLifes[i].setOpacity(0.5);
            }
        }
    }

    public void subsLive(){
        lifes--;
    }

    public void addLive(){
        if(lifes != 5) {
            lifes++;
        }
    }

    public int getScore() {
        return score;
    }

    public void addScore(int score) {
        this.score += score;
    }

    public int getLifes() {
        return lifes;
    }

    public void setLifes(int lifes) {
        this.lifes = lifes;
    }

    public void setState(Enums.ShipState state) {
        this.state = state;
    }

    public Enums.ShipState getState() {
        return state;
    }
}
