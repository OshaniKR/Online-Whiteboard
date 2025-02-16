package Whiteboard;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 5000)) {
            System.out.println("Connected to server.");

            // Initialize output and input streams after socket connection
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.flush(); // Ensure that header is written properly
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Start a thread to listen for server updates
            new Thread(() -> {
                try {
                    while (true) {
                        DrawMessage message = (DrawMessage) in.readObject();
                        System.out.println("Received update: " + message);
                        // Apply drawing updates in the UI (for example, repaint in a GUI framework)
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Disconnected from server.");
                } finally {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            // Example: Sending a drawing update
            DrawMessage drawMessage = new DrawMessage(10, 20, "RED");
            out.writeObject(drawMessage);
            out.flush(); // Ensure message is sent immediately

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
