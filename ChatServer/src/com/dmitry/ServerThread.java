package com.dmitry;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.Socket;
import java.util.List;

public class ServerThread extends Thread {
    private final Server server;
    private final Socket clientSocket;
    private String login = null;
    private OutputStream outputStream;

    public ServerThread(Server server, Socket clientSocket) {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    public String getLogin() {
        return login;
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
        outputStream = clientSocket.getOutputStream();

        outputStream.write("Hello!\n".getBytes());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while((line = bufferedReader.readLine()) != null) {
            String[] tokens = StringUtils.split(line);
            if(tokens != null && tokens.length > 0) {
                String cmd = tokens[0];
                if("logoff".equalsIgnoreCase(cmd) || "quit".equalsIgnoreCase(cmd)) {
                    handleLogoff();
                    break;
                } else if("login".equalsIgnoreCase(cmd)) {
                    handleLogin(outputStream, tokens);
                } else {
                    String msg = login != null ? login + cmd + "\n" : "unknown " + cmd + "\n";
                    outputStream.write(msg.getBytes());
                }
            }
        }

        clientSocket.close();
    }

    private void handleLogoff() throws IOException {
        List<ServerThread> threadList = server.getThreadList();

        String onlineMsg = "offline " + login + "\n";
        for(ServerThread thread : threadList) {
            if(!login.equals(thread.getLogin())) {
                thread.send(onlineMsg);
            }
        }
        clientSocket.close();
    }

    private void handleLogin(OutputStream outputStream, String[] tokens) throws IOException{
        if(tokens.length == 2) {
            String login = tokens[1];

            if(login.equalsIgnoreCase("quest") || login.equalsIgnoreCase("dima")) {
                String msg = "ok login\n";
                outputStream.write(msg.getBytes());
                this.login = login;
                System.out.println("User logged in successfully: " + login);

                List<ServerThread> threadList = server.getThreadList();

                for(ServerThread thread : threadList) {
                    if(thread.getLogin() != null) {
                        if(!login.equals(thread.getLogin())) {
                            String msg2 = "online " + thread.getLogin() + "\n";
                            send(msg2);
                        }
                    }
                }

                String onlineMsg = "online " + login + "\n";
                for(ServerThread thread : threadList) {
                    if(!login.equals(thread.getLogin())) {
                        thread.send(onlineMsg);
                    }
                }
            } else {
                String msg = "error login\n";
                outputStream.write(msg.getBytes());
            }
        }
    }

    private void send(String msg) throws IOException{
        outputStream.write(msg.getBytes());
    }
}
