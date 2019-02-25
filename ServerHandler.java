package njit.cs602.project;

//          Aashay Thakkar

import java.net.*;
import java.util.Set;
import java.io.*;

public class ServerHandler implements Runnable{
    private String clientId;
    private Socket clientSocket;
    private Thread t;
    private Set<ServerHandler> serverHandlers;
    private ServerHandler neighbour;

    public ServerHandler(Socket clientSocket, Set<ServerHandler> serverHandlers) {
        this.clientSocket = clientSocket;
        this.serverHandlers = serverHandlers;
        t = new Thread(this);
        t.start();
    }

    public void run() {
        try (DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
             DataInputStream in = new DataInputStream(clientSocket.getInputStream())) {

            clientId = in.readUTF().trim();
            if (serverHandlers.add(this)) {
                out.writeUTF("OK");

                for (;;) {
                    String inputLine = in.readUTF();
                    String[] tokens = inputLine.split(" ");
                    String command = tokens[0];

                    switch (command) {
                        case "@open":
                            if (tokens.length > 1) {
                                String clientId = tokens[1];
                                openSession(clientId, out);
                            } else {
                                out.writeUTF("@error");
                                out.writeUTF("Client name required!");
                            }
                            break;

                        case "@close":
                            closeSession(out);
                            break;

                        case "@clients":
                            sendUserList(in, out);
                            break;

                        default:
                           if (neighbour != null) {
                              try{
                                  if (this.clientId == neighbour.neighbour.clientId){
                                      DataOutputStream neighbourOut = new DataOutputStream(
                                              neighbour.clientSocket.getOutputStream());
                                      neighbourOut.writeUTF(inputLine);
                                  }
                              }catch (NullPointerException e){
                               System.out.println(neighbour.clientId+ " not in session with "+this.clientId);
                              }

                            }
                            break;
                    }
                }
            } else {
                out.writeUTF("REJECT");
            }
            clientSocket.close();
            serverHandlers.remove(this);
        } catch (IOException e) {
            System.out.println("Read or write to socket failed.");
            System.exit(-1);
        }
    }

    private void openSession(String clientId, DataOutputStream out) {
        try {
            for (ServerHandler clientHandler : serverHandlers) {
                if (clientHandler.clientId.equals(clientId)) {
                    neighbour = clientHandler;
                    out.writeUTF("@open");
                    out.writeUTF(neighbour.clientId);
                    return;
                }
            }
            out.writeUTF("@error");
            out.writeUTF("Client not found!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeSession(DataOutputStream out) {
        try {
            neighbour = null;
            out.writeUTF("@close");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendUserList(DataInputStream in, DataOutputStream out) {
        // sending user list
        try {
            out.writeUTF("@clients");
            out.writeInt(serverHandlers.size());
            for (ServerHandler clientHandler : serverHandlers) {
                if(this.clientId!=clientHandler.clientId) {
                   out.writeUTF(clientHandler.clientId);
                  }
                  else{
                    out.writeUTF("");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((clientId == null) ? 0 : clientId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServerHandler other = (ServerHandler) obj;
        if (clientId == null) {
            if (other.clientId != null)
                return false;
        } else if (!clientId.equals(other.clientId))
            return false;
        return true;
    }

}
