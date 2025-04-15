package org.example.client.socket;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class SocketClient {
    private String host;
    private int port;

    public SocketClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String sendMessage(String message) {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(message);
            return in.readLine(); // JSON response from server

        } catch (IOException e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    public List<String> requestImageList() throws IOException {
        sendRaw("GET_LIST");
        return readJsonList();
    }

    private void sendRaw(String message) throws IOException {
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            out.println(message);
        }
    }

    private String readLine() throws IOException {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            return in.readLine();
        }
    }

    private List<String> readJsonList() throws IOException {
        String json = readLine();
        return new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
    }

    public boolean downloadImage(String imageName) {
        try (Socket imageSocket = new Socket(host, port);
             DataInputStream in = new DataInputStream(imageSocket.getInputStream());
             DataOutputStream out = new DataOutputStream(imageSocket.getOutputStream())) {

            out.writeBytes("GET_IMAGE:" + imageName + "\n");

            int fileSize = in.readInt();
            if (fileSize <= 0) return false;

            File outFile = new File("client_images/" + imageName);
            outFile.getParentFile().mkdirs();

            try (BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(outFile))) {
                byte[] buffer = new byte[4096];
                int bytesRead, totalRead = 0;

                while (totalRead < fileSize && (bytesRead = in.read(buffer, 0, Math.min(buffer.length, fileSize - totalRead))) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
