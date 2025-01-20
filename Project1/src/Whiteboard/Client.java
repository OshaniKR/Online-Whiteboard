package Whiteboard;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_IP = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try {
            Socket socket = new Socket(SERVER_IP, PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            JFrame frame = new JFrame("Collaborative Whiteboard");
            WhiteboardPanel panel = new WhiteboardPanel(out);
            frame.add(panel);
            frame.setSize(800, 600);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            new Thread(() -> {
                try {
                    while (true) {
                        DrawMessage message = (DrawMessage) in.readObject();
                        panel.applyMessage(message);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
