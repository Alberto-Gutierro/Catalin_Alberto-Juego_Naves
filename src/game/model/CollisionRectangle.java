package game.model;


import java.awt.*;

public class CollisionRectangle extends Rectangle {
    public void setCollisions(int x, int y, int width, int height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
}
