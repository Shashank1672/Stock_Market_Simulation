import java.net.*;
import java.io.*;

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {
        try {
            // get input and output streams
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            // read messages from client and send response
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println("Message received from " + clientSocket.getRemoteSocketAddress() + ": " + line);
                out.println("Response from server: " + line);
            }

            // close streams and socket
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}