package ru.geekbrains.java_two.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ServerSocketThread extends Thread {
    private final int port;
    private final int timeout;
    private final ServerSocketThreadListener listener;

// поток который слушает порт
    public ServerSocketThread(ServerSocketThreadListener listener, String name, int port, int timeout) {
        super(name);
        this.port = port;
        this.timeout = timeout;
        this.listener = listener;
        start();
    }

    @Override
    public void run() {
        listener.onServerStart(this);

        try(ServerSocket server = new ServerSocket(port)){
       // Так нельзя делать, т.к. будем висеть пока не будет подключения и не сможем интераптнуть из акцепта
            //         while (isInterrupted()){
       //             server.accept();
         //       }
            server.setSoTimeout(timeout); // позволяет включать акцепт на установленное время таймаута,
            listener.onServerSocketCreated(this, server);
            // чтоб не висеть постоянно в акцепте ожидая подключения
            while (!isInterrupted()) {
                Socket socket;
                try {
                    socket = server.accept(); // таймаут будет изредка отпускать из акцепта, для того чтоб проверить не интерапнули ли нас

                } catch (SocketTimeoutException e) {
                    listener.onServerTimeout(this, server);
                    continue;
                }
                listener.onSocketAccepted(this, server, socket);
            }
                } catch (IOException e){
                    listener.onServerException(this, e);
                } finally {
            listener.onServerStop(this);
        }

    }
}