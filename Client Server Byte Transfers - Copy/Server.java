import java.net.*;
import java.io.*;
import java.sql.*;

public class Server{
    private ServerSocket serverSocket;
    public Connection db_conn;

    public Server (ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer() {
        try{
            while(!serverSocket.isClosed()){
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected");
                ClientHandler clientHandler = new ClientHandler(socket, db_conn);

                Thread thread = new Thread(clientHandler);
                thread.start();

            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closeServerSocket(){
        try{
            if (serverSocket != null){
                serverSocket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        //Create a Server instance
        ServerSocket serverSocket = new ServerSocket(1234);  // Port number has to taken by choice. Write an input for thw same
        Server server = new Server(serverSocket);

        //Connect to a database
        String url = "jdbc:mysql://localhost:3306/";
        String username = "root"; String password = "forgotpassword";
        try {
            Class.forName("com.mysql.jdbc.Driver");
            server.db_conn = DriverManager.getConnection(url, username, password);
            System.out.println("Server connected to the database");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("This Server is accepting client connection in port number 1234");
        server.startServer();
        
    }
}