package org.example.client.socket;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        try (Socket socket = new Socket(host, port);
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            out.println("GET_LIST");

            String json = in.readLine();
            return new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
        }
    }

    public boolean downloadImage(String imageName) {
        try (Socket imageSocket = new Socket(host, port);
             DataInputStream in = new DataInputStream(imageSocket.getInputStream());
             OutputStream out = imageSocket.getOutputStream()) {

            // Send the request
            out.write(("GET_IMAGE:" + imageName + "\n").getBytes(StandardCharsets.UTF_8));
            out.flush();

            long fileSize = in.readLong();
            if (fileSize <= 0) {
                System.out.println("Image not found or invalid size.");
                return false;
            }

            File outFile = new File("client_images/" + imageName);
            outFile.getParentFile().mkdirs();

            try (BufferedOutputStream fileOut = new BufferedOutputStream(new FileOutputStream(outFile))) {
                byte[] buffer = new byte[4096];
                long totalRead = 0;
                int bytesRead;

                while (totalRead < fileSize &&
                        (bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize - totalRead))) != -1) {
                    fileOut.write(buffer, 0, bytesRead);
                    totalRead += bytesRead;
                }
            }

            System.out.println("Downloaded: " + imageName + " (" + fileSize + " bytes)");
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
