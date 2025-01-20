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

        setBackground(Color.WHITE); // Set white background

        // Mouse events for drawing
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                sendMessage(e.getX(), e.getY(), e.getX(), e.getY(), "draw");
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!messages.isEmpty()) {
                    DrawMessage last = messages.get(messages.size() - 1);
                    sendMessage(last.x2, last.y2, e.getX(), e.getY(), "draw");
                }
            }
        });

        // Clear button to clear the drawing
        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> sendMessage(0, 0, 0, 0, "clear"));
        add(clearButton);

        // Color picker button
        JButton colorPicker = new JButton("Color");
        colorPicker.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose a Color", Color.BLACK);
            if (newColor != null) {
                currentColor = String.format("#%02x%02x%02x", newColor.getRed(), newColor.getGreen(), newColor.getBlue());
            }
        });
        add(colorPicker);

        setLayout(new FlowLayout(FlowLayout.LEFT)); // Set layout for buttons
    }

    // Send message to the output stream and update local message list
    private void sendMessage(int x, int y, int x2, int y2, String action) {
        try {
            DrawMessage message = new DrawMessage(x, y, x2, y2, currentColor, action);
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

    // Apply received messages for drawing
    public void applyMessage(DrawMessage message) {
        if (message.action.equals("clear")) {
            messages.clear();
        } else {
            messages.add(message);
        }
        repaint();
    }

    // Draw all messages
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (DrawMessage message : messages) {
            g.setColor(Color.decode(message.color));
            g.drawLine(message.x, message.y, message.x2, message.y2);
        }
    }
}
