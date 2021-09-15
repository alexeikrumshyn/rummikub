package rummikub;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Scanner;

/*
    The Player class handles the individual player's logic and actions

    Contains a nested class "Client" which handles an individual player's communication to the server
 */
public class Player implements Serializable {

    private static final long serialVersionUID = 1L;

    public String name;
    int playerId = 0;

    Client clientConnection;

    public Player(String n) {
        name = n;
    }

    public static void main(String args[]) {
        Scanner myObj = new Scanner(System.in);
        System.out.print("What is your name ? ");
        String name = myObj.next();
        Player p = new Player(name);
        p.connectToClient();
        myObj.close();
    }

    public void connectToClient() {
        clientConnection = new Client();
    }


    /*
        The Client class handles individual player's client activities
     */
    private class Client {
        Socket socket;
        private ObjectInputStream dIn;
        private ObjectOutputStream dOut;

        public Client() {
            this(Config.GAME_SERVER_PORT_NUMBER);
        }

        public Client(int portId) {
            try {
                socket = new Socket("localhost", portId);
                dOut = new ObjectOutputStream(socket.getOutputStream());
                dIn = new ObjectInputStream(socket.getInputStream());

                playerId = dIn.readInt();
                System.out.println("Connected as " + playerId);

            } catch (IOException ex) {
                System.out.println("Client failed to open");
            }
        }

    }

}
