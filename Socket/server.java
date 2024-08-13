import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class server {
    // Lista per memorizzare tutti i client connessi
    private static List<ClientHandler> clients = new ArrayList<>();

    public static void main(String[] args) throws IOException {

        try (ServerSocket ss = new ServerSocket(4999)) {

            System.out.println("Server online");

            while (true) {
                Socket s = ss.accept();
                ClientHandler clientHandler = new ClientHandler(s);
                clients.add(clientHandler);
                clientHandler.start();
            }
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    // Metodo per inviare un messaggio a tutti i client
    public static synchronized void broadcast(String message, ClientHandler excludeClient) {
        for (ClientHandler client : clients) {
            if (client != excludeClient) {
                client.sendMessage(message);
            }
        }
    }
}

class ClientHandler extends Thread {
    private final Socket s;
    private PrintWriter pr;
    private String clientName;

    public ClientHandler(Socket s) {
        this.s = s;
    }

    @SuppressWarnings("unused")
    public void run() {
        try {
            InputStreamReader in = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(in);
            pr = new PrintWriter(s.getOutputStream(), true);

            clientName = bf.readLine();
            System.out.println(clientName + " is connected ");

            server.broadcast(clientName + " has joined the chat!", this);

            String str;
            int i = 0;
            while ((str = bf.readLine()) != null) {
                i++;
                if (i > 1) {
                    if (str == null) {

                    } else if (str.equals("/exit")) {
                        System.out.println(clientName + " disconnected");
                        server.broadcast(clientName + " has left the chat!", this);

                        break;
                    } else {
                        System.out.println(clientName + ": " + str);
                        server.broadcast(clientName + ": " + str, this);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(clientName + " disconneced");
        }
    }

    public void sendMessage(String message) {
        pr.println(message);
    }
}
