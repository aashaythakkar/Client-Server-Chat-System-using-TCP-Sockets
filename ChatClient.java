package njit.cs602.project;

//          Jaymin Modi

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class ChatClient {
    public static void main(String[] args) {

        new ChatClient().process();
    }

    public void process() {
        String hostName = "localhost";
        int port = 12345;

        InetAddress ina = null;
        try {
            ina = InetAddress.getByName(hostName);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        Socket s = null;
        try {
            s = new Socket(ina, port);
            System.out.println("Server Status : UP");
        } catch (IOException e) {
            System.out.println("Server Status: DOWN");
            System.out.println("Try connecting later");
            System.exit(-2);
        }

        try (DataOutputStream out = new DataOutputStream(s.getOutputStream());
             DataInputStream in = new DataInputStream(s.getInputStream());
             Scanner kb = new Scanner(System.in)) {

            String request = "";
            String response = "";

            System.out.print("Enter your id (name): ");

            // send request to the socket
            request = kb.nextLine();
            out.writeUTF(request);

            // read response from the socket
            response = in.readUTF();

            if (response.equals("OK")) {
                System.out.println("OK");
                //Pass socket value to clientHandler
                new ClientHandler(s);

                for (;;) {
                    // read a line from user input
                    request = kb.nextLine().trim();

                    // send request to the socket
                    out.writeUTF(request);
                }
            } else {
                System.out.println("Already connected");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
