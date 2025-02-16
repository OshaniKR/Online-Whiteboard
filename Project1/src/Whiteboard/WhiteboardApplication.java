package Whiteboard;

import javax.swing.*;

public class WhiteboardApplication {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Whiteboard Application");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // Whiteboard panel (without networking for now)
            WhiteboardPanel whiteboardPanel = new WhiteboardPanel(null);
            frame.add(whiteboardPanel);

            frame.setVisible(true);
        });
    }
}
