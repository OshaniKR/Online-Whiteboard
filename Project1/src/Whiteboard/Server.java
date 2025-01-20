package Whiteboard;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static List<ObjectOutputStream> clientStreams = new ArrayList<>();

    public static void main(String[] args) {
        System.out.println("Server started...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                clientStreams.add(out);

                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void broadcast(DrawMessage message) {
        for (ObjectOutputStream out : clientStreams) {
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
                while (true) {
                    DrawMessage message = (DrawMessage) in.readObject();
                    broadcast(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Client disconnected: " + socket);
            }
        }
    }
}
