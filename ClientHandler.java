package njit.cs602.project;

//          Jaymin Modi

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ClientHandler implements Runnable{
    private Socket clientSocket;
    private Thread t;
    private String neighbourName;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {
        try (DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
             DataInputStream in = new DataInputStream(clientSocket.getInputStream());
             Scanner kb = new Scanner(System.in)) {

            String response;
            String request = "@clients";
            out.writeUTF(request);

            for (;;) {
                // read response from the socket
                response = in.readUTF();

                switch (response) {
                    case "@open":
                        neighbourName = in.readUTF();
                        if (neighbourName != null)
                            System.out.print("Session with user " + neighbourName + " ");
                        break;

                    case "@close":
                        if (neighbourName != null) {
                            System.out.println("Disconnect from client " + neighbourName);
                            neighbourName = null;
                            printMenu();
                        }
                        break;

                    case "@clients":
                        printUserList(in);
                        printMenu();
                        break;

                    case "@error":
                        String errorDescr = in.readUTF();
                        System.out.println("ERROR: " + errorDescr);
                        break;


                    default:
                        if (neighbourName != null)
                       System.out.print(neighbourName + " :");

                        System.out.println(response);
                        break;
                }

             //   printMenu();
                System.out.print("> ");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printMenu() {
        System.out.println();
        if (neighbourName == null)
            System.out.println("@open <client Id> - to open a session with a client");
            System.out.println("@close - to close the session with a client");
            System.out.println("@clients - to display all clients connected to the server");
    }

    private void printUserList(DataInputStream in) {
        System.out.println();
        System.out.println("User list:");
        try {
            // receiving user list
            int userListSize = in.readInt();
            for (int i = 0; i < userListSize; i++) {
                String userName = in.readUTF();
                System.out.println(userName);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
