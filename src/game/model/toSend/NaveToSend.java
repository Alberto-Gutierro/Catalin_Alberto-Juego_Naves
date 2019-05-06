package game.model.toSend;

import game.model.Nave;
import game.model.Timer;

import java.util.ArrayList;

//Se pondrán las variables que se necesite para mandar al servidor.
public class NaveToSend {

    private int idNave;

    private int score;

    //posicion nave
    private double navePosX;
    private double navePosY;

    //posiciones cursor
    private double naveCursorPosX;
    private double naveCursorPosY;

    //angulo (nave y bala)
    private double angle;

    //balas
    private ArrayList<BalaToSend> naveArmaBalas;
    private ArrayList<Integer> navesTocadas;
    private ArrayList<Integer> meteoritosTocados;

    private int lives;

    //////FALTA: ESTA VARIABLE NO SE TIENE QUE PASAR EN EL JSON.
    private Timer timer;

    public NaveToSend(){
        naveArmaBalas = new ArrayList<>();
        timer = new Timer(3);
        navesTocadas = new ArrayList<>();
        meteoritosTocados = new ArrayList<>();
    }
    //change

    public void setData(Nave nave, double time) {
        lives = nave.getLifes();

        score = nave.getScore();

        timer.update(time);

        //Sin han pasado 10 segundos se borran todas las balas de dentro del array.
        if(timer.check()){
            naveArmaBalas.clear();
            nave.getArma().getBalas().forEach(bala->bala.setAdded(false));
        }

        this.idNave = nave.getId();
        navePosX = nave.getPosX();
        navePosY = nave.getPosY();

        angle = nave.getAngle();

        naveCursorPosX = nave.getOrientation().getPosX();
        naveCursorPosY = nave.getOrientation().getPosY();

        if(!nave.getArma().getBalas().isEmpty()) {
            nave.getArma().getBalas().forEach(bala -> {
                if(!bala.getAdded()) {
                    naveArmaBalas.add(new BalaToSend(bala.getPosX(), bala.getPosY(), nave.getId(), bala.getIdBala(), bala.getAngle()));
                    bala.setAdded(true);
                } else{
                    naveArmaBalas.forEach(balaToSend -> {
                        if (balaToSend.getIdBala() == bala.getIdBala()) {
                            balaToSend.setPos(bala.getPosX(),bala.getPosY());
                        }
                    });

                }
            });
        }

//        System.out.println("////////////////////" + naveArmaBalas.size());
    }

    public double getNavePosX() {
        return navePosX;
    }

    public double getNavePosY() {
        return navePosY;
    }

    public double getNaveCursorPosX() {
        return naveCursorPosX;
    }

    public double getNaveCursorPosY() {
        return naveCursorPosY;
    }

    public double getAngle(){
        return angle;
    }

    public ArrayList<BalaToSend> getNaveArmaBalas() {
        return naveArmaBalas;
    }

    public void addIdNaveTocada(int id){
        navesTocadas.add(id);
    }

    public void clearIdNaveTocada() {
        navesTocadas.clear();
    }
}
