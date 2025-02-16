package Whiteboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WhiteboardPanel extends JPanel {
    private ObjectOutputStream out;
    private List<DrawMessage> messages = new ArrayList<>();
    private String currentColor = "#000000"; // Default color is black

    public WhiteboardPanel(ObjectOutputStream out) {
        this.out = out;
        setBackground(Color.WHITE);
        setLayout(new BorderLayout());

        // Drawing panel
        DrawingCanvas canvas = new DrawingCanvas();
        add(canvas, BorderLayout.CENTER);

        // Toolbar for buttons
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Clear button
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            messages.clear();
            repaint();
        });

        // Color picker button
        JButton colorPicker = new JButton("Color");
        colorPicker.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose a Color", Color.BLACK);
            if (newColor != null) {
                currentColor = String.format("#%02x%02x%02x", newColor.getRed(), newColor.getGreen(), newColor.getBlue());
            }
        });

        toolbar.add(clearButton);
        toolbar.add(colorPicker);
        add(toolbar, BorderLayout.NORTH);
    }

    private void sendMessage(int x, int y) {
        try {
            DrawMessage message = new DrawMessage(x, y, currentColor);
            if (out != null) {
                out.writeObject(message);
                out.flush();
            }
            messages.add(message);
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void applyMessage(DrawMessage message) {
        messages.add(message);
        repaint();
    }

    private class DrawingCanvas extends JPanel {
        public DrawingCanvas() {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    sendMessage(e.getX(), e.getY());
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            for (DrawMessage message : messages) {
                g.setColor(Color.decode(message.getColor()));
                g.fillOval(message.getX(), message.getY(), 5, 5); // Draw a small dot at each point
            }
        }
    }
}
