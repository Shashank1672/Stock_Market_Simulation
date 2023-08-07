import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String ip, int port) {
        try {
            clientSocket = new Socket(ip, port);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            Scanner scanner = new Scanner(System.in);
            String line;
            while (true) {
                System.out.print("Enter message (or 'quit' to exit): ");
                line = scanner.nextLine();
                if (line.equals("quit")) {
                    break;
                }
                out.println(line);
                System.out.println("Response from server: " + in.readLine());
            }
            scanner.close();
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 8000);
        client.start();
    }
}
