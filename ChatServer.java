package njit.cs602.project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

//          Aashay Thakkar

public class ChatServer {
    private Set<ServerHandler> serverHandlers = new HashSet<>();

    public static void main(String[] args) {

        new ChatServer().process();
    }

    public void process() {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server started at port 12345");
            for (;;) {
                Socket clientSocket = null;
                try {
                    // waiting for client connection
                    clientSocket = serverSocket.accept();
                    new ServerHandler(clientSocket, serverHandlers);
                } catch (IOException e) {
                    System.out.println("Accept failed.");
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            System.out.println("Could not listen on port: 12345.");
            System.exit(0);
        }
    }

}
