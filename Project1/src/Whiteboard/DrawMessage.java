package Whiteboard;

import java.io.Serializable;


public class DrawMessage implements Serializable {
    private int x, y;
    private String color;

    public DrawMessage(int x, int y, String color) {
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getColor() { return color; }

    @Override
    public String toString() {
        return "DrawMessage{x=" + x + ", y=" + y + ", color='" + color + "'}";
    }
}
