package com.dmitry;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class ServerThread extends Thread {
    private Socket clientSocket;

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            handleClientSocket();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void handleClientSocket() throws IOException, InterruptedException {
        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();
        outputStream.write("Hello!\n".getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = bufferedReader.readLine()) != null) {
            if("quit".equalsIgnoreCase(line)) {
                break;
            }
            outputStream.write(("You typed: " + line + "\n").getBytes());
        }

        clientSocket.close();
    }
}
