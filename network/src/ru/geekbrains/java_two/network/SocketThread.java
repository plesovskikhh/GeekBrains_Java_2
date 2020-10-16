package ru.geekbrains.java_two.network;


import java.io.Closeable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

// Класс объекты которого работают с двух сторон и с серверной и с клиентской
// должен быть настолько универсальным, чтобы мочь обслуживать как сервер так и клиент
// Как с одной стороны работать в группе подключившихся клиентов
// Так и работать с другой стороны как самостоятельный клиент, который серваку что-то отвечает
public class SocketThread extends  Thread implements Closeable {

    private final SocketThreadListener listener;
    private final Socket socket;
    private DataOutputStream out;


    // Конструктор задает название треда, сохраняет прилетевший в него сокет, листнера сохраняем
    public SocketThread(SocketThreadListener listener, String name, Socket socket){
        super(name);
        this.socket = socket;
        this.listener = listener;
        start();
    }

    @Override
    public void run() {
        // Сам тред будет асинхронно и постоянно осуществлять прослушивание входящего стрима
        // и по факту получения каждой строки будет отправлять полученную строку своему слушателю
        try {
            listener.onSocketStart(this, socket);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            listener.onSocketReady(this, socket);
            while (!isInterrupted()) { // пока нас не прервали
                String msg = in.readUTF();// мы готовы слушать входящие строки
                listener.onReceiveString(this, socket, msg);// по факту получения отдавать их листнеру
            }
        } catch (IOException e) {
            listener.onSocketException(this, e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                listener.onSocketException(this, e);
            }
            listener.onSocketStop(this);
        }
    }

    // Метод отправки сообщения будет брать тот сокет, который есть внутри треда
    // и в исходящий стрим отправлять UTFстроку
    public synchronized boolean sendMessage(String msg){
        try {
            out.writeUTF(msg);
            out.flush();
            return true;
        } catch (IOException e) {
            listener.onSocketException(this, e);
            close();
            return false;
        }
    }

    public synchronized void close(){
        interrupt();
        try {
            socket.close();
        }catch (IOException e) {
            listener.onSocketException(this, e);
        }
    }

}
