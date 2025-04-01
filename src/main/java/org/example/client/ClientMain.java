package org.example.client;

import org.example.client.gui.MainGUI;
import org.example.client.socket.SocketClient;

public class ClientMain {
    public static void main(String[] args) {
        try {
            SocketClient client = new SocketClient("localhost", 12345);
            MainGUI.setConnection(client); // Inject before JavaFX launches
            MainGUI.main(args); // Start the GUI
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
