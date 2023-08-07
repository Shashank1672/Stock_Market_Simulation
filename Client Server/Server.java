import java.net.*;
import java.io.*;

public class Server {
    private ServerSocket serverSocket;

    // Constructor
    public Server(int port) {
        try {
            serverSocket = new ServerSocket(port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void start() {
        System.out.println("Server is listening on port " + serverSocket.getLocalPort());
        while (true) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getRemoteSocketAddress());
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread t = new Thread(clientHandler);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        
        Server server = new Server(8000);
        server.start();
    }
}