package Whiteboard;

import java.io.Serializable;

public class DrawMessage implements Serializable {
    public int x, y, x2, y2;
    public String color, action;

    public DrawMessage(int x, int y, int x2, int y2, String color, String action) {
        this.x = x;
        this.y = y;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.action = action;
    }
}
