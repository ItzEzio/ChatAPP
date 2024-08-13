import java.net.*;
import java.io.*;
import java.util.*;

public class client {
    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {

        try (Socket s = new Socket("192.168.1.12", 4999)) {
            Scanner client = new Scanner(System.in);
            PrintWriter pr = new PrintWriter(s.getOutputStream(), true);
            System.out.print("Inserisci il tuo nickname: ");
            String c = client.nextLine();
            pr.println(c);

            UserInput userinput = new UserInput(pr, client);
            UsersMessages usersmessages = new UsersMessages(s);

            userinput.start();
            usersmessages.start();

            userinput.join();
            usersmessages.join();

        } catch (IOException e) {
            System.out.println("Server Offline");
        }
    }
}

// scrivere
class UserInput extends Thread {
    private Scanner client;
    private PrintWriter pr;
    private String c;

    public UserInput(PrintWriter pr, Scanner client) {
        this.pr = pr;
        this.client = client;

    }

    @Override
    public void run() {
        pr.println();
        while (true) {
            c = client.nextLine();
            pr.println(c);
        }
    }
}

// leggere
class UsersMessages extends Thread {
    private Socket s;

    public UsersMessages(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        String str;
        try {
            InputStreamReader in = new InputStreamReader(s.getInputStream());
            BufferedReader bf = new BufferedReader(in);

            while (true) {
                str = bf.readLine();
                System.out.println(str);
            }
        } catch (IOException e) {
            e.addSuppressed(e);
        }
    }
}