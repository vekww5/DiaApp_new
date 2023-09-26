package com.example.diaapp.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class TCPConnection  {
    private Socket socket;
    private Thread thread;
    private BufferedReader reader;
    private BufferedWriter writer;
    private boolean isSender;                           // Переменная, которая указывает на то, что соединение является отправителем сообщения
    private boolean isReceiver;                         // Переменная, которая указывает на то, что соединение является получателем сообщения

    public TCPConnection( String ip, int port) throws IOException {
        this.socket = new Socket(ip, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

    }

    public String getMassege(){
        String msg = "";
        try {
            msg = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msg;
    }

    public void sendString(String str) {
        try {
            writer.write(str + "\n");
            //writer.write(str);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }

    }

    public synchronized void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void setReceiverTrue() {
        isReceiver = true;
    }

    public synchronized void setReceiverFalse() {
        isReceiver = false;
    }

    public synchronized boolean isReceiver() {
        return isReceiver;
    }

    public synchronized void setSenderTrue() {
        isSender = true;
    }

    public synchronized void setSenderFalse() {
        isSender = false;
    }

    public synchronized boolean isSender() {
        return isSender;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }

}
