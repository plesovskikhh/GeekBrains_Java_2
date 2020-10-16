package ru.geekbrains.java_two.network;

import java.net.ServerSocket;
import java.net.Socket;

public interface ServerSocketThreadListener {
    // Стартанули тред
    void onServerStart(ServerSocketThread thread); //
    // Остановили тред
    void onServerStop(ServerSocketThread thread); // передаем тред и говорим какой именно тред  остановился
    // Начали серверсокетом слушать порт
    void onServerSocketCreated(ServerSocketThread thread, ServerSocket server); // в каком треде создался и какой именно серверсокет создался внутри треда
    // Здесь происходит таймаут
    void onServerTimeout(ServerSocketThread thread, ServerSocket server); // в каком треде и у какого сервера произошел таймаут
   // Здесь произошло исключение
    void onServerException(ServerSocketThread thread, Throwable exception); // в каком треде произошел эксепшн и знаем какой эксепшн
    // Здесь произошло подключение клиента
    void onSocketAccepted(ServerSocketThread thread, ServerSocket server, Socket socket); //кагда приняли сокет мы знаем в каком треде произошло, какой сервер породил сокет, какой сокет родился

}
