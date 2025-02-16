package Whiteboard;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static List<ObjectOutputStream> clientStreams = Collections.synchronizedList(new ArrayList<>());
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(5000);
            System.out.println("Server is running on port 5000...");

            // Handle graceful shutdown
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down server...");
                try {
                    for (ObjectOutputStream out : clientStreams) {
                        try {
                            out.close();  // Ensure proper closing of output streams
                        } catch (IOException e) {
                            System.out.println("Error closing output stream: " + e.getMessage());
                        }
                    }
                    serverSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing server socket: " + e.getMessage());
                }
            }));

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket);

                    // Start a new thread for each client
                    new Thread(new ClientHandler(clientSocket)).start();
                } catch (IOException e) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                    break;  // Stop the server if an error occurs while accepting connections
                }
            }
        } catch (IOException e) {
            System.out.println("Server stopped due to IOException: " + e.getMessage());
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                // Handle creating streams with proper exception handling
                try {
                    out = new ObjectOutputStream(socket.getOutputStream());
                    out.flush(); // Ensuring the header is written
                    in = new ObjectInputStream(socket.getInputStream());
                } catch (IOException e) {
                    System.out.println("Error creating input/output streams for client: " + socket);
                    e.printStackTrace();
                    return; // Exit the thread if streams can't be created
                }

                // Add client output stream to list
                synchronized (clientStreams) {
                    clientStreams.add(out);
                }

                while (true) {
                    try {
                        DrawMessage message = (DrawMessage) in.readObject();
                        if (message != null) {
                            broadcast(message);
                        }
                    } catch (EOFException e) {
                        System.out.println("Client unexpectedly disconnected: " + socket);
                        break; // Exit the loop if client disconnects
                    } catch (IOException | ClassNotFoundException e) {
                        System.out.println("Error reading object from client: " + socket);
                        e.printStackTrace();
                        break; // Exit the loop if error occurs
                    }
                }
            } finally {
                try {
                    // Close input/output streams before closing the socket
                    if (in != null) {
                        in.close();
                    }
                    if (out != null) {
                        out.close();
                    }
                    socket.close();
                    synchronized (clientStreams) {
                        clientStreams.remove(out);
                    }
                    System.out.println("Closed connection with client: " + socket);
                } catch (IOException e) {
                    System.out.println("Error closing resources: " + e.getMessage());
                }
            }
        }
    }

    private static void broadcast(DrawMessage message) {
        synchronized (clientStreams) {
            Iterator<ObjectOutputStream> iterator = clientStreams.iterator();
            while (iterator.hasNext()) {
                ObjectOutputStream out = iterator.next();
                try {
                    out.writeObject(message);
                    out.flush(); // Ensuring data is sent immediately
                } catch (IOException e) {
                    iterator.remove(); // Remove client if sending fails
                    System.out.println("Error sending message to client, removing: " + out);
                }
            }
        }
    }
}
