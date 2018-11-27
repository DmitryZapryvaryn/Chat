package com.dmitry;

public class ServerMain {
    public static int PORT = 8080;

    public static void main(String[] args) {
        Server server = new Server(PORT);
        server.start();
    }
}
