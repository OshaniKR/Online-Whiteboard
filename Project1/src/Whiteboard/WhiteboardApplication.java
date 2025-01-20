package Whiteboard;



import javax.swing.*;
import java.io.ObjectOutputStream;

public class WhiteboardApplication {
    public static void main(String[] args) {
        // Create the frame for the whiteboard
        JFrame frame = new JFrame("Whiteboard Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Whiteboard panel (no networking for simplicity)
        WhiteboardPanel whiteboardPanel = new WhiteboardPanel(null);
        frame.add(whiteboardPanel);

        frame.setVisible(true);
    }
}

