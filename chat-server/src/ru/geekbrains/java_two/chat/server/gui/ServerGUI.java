package ru.geekbrains.java_two.chat.server.gui;

import ru.geekbrains.java_two.chat.server.core.ChatServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerGUI extends JFrame implements ActionListener, Thread.UncaughtExceptionHandler {
   private static final int POS_X = 1000;
    private static final int POS_Y = 550;
    private static final int WIDTH = 200;
    private static final int HEIGHT = 100;

   private final ChatServer chatServer = new ChatServer();
   private final JButton btnStart = new JButton("Start");
   private final JButton btnStop = new JButton("Stop");

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {    //для запуска сервера
            @Override
            public void run() {
                new ServerGUI();
            }
        });
    }

    private ServerGUI(){
        Thread.setDefaultUncaughtExceptionHandler(this); // ловим исключения
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(POS_X,POS_Y,WIDTH,HEIGHT);
        setResizable(false);// запрещаем писать в логе
        setTitle("Chat server");
        setAlwaysOnTop(true); // поверх всех окон
        setLayout(new GridLayout(1, 2)); //
        btnStart.addActionListener(this);// устанавливаем слушателя кнопки "Start"
        btnStop.addActionListener(this); // устанавливаем слушателя кнопки "Stop"
        add(btnStart);// добавляем саму кнопку
        add(btnStop);
        setVisible(true); // делаем все видимым
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnStop) {
            chatServer.stop();
        } else if (src == btnStart) {
     //       throw new RuntimeException("Hello from EDT!"); //Пример исключения, сами написали и сами ловим в конструкторе сервера
            chatServer.start(8189);
        } else {
            throw new RuntimeException("Unknown source: " + src);
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        String msg;
        StackTraceElement[] ste = e.getStackTrace();
        msg = "Exception in " + t.getName() + " " + e.getClass().getCanonicalName() + ": " +
                e.getMessage() + "\n\t at " + ste [0];
        JOptionPane.showMessageDialog(this, msg, "Exception", JOptionPane.ERROR_MESSAGE);
        System.exit(1);
    }
}
