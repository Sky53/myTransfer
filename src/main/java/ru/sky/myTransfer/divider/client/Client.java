package ru.sky.myTransfer.divider.client;

import ru.sky.myTransfer.divider.utils.*;

import java.io.*;
import java.net.Socket;

public class Client {
    private Connection connection;
    private volatile boolean clientConnected = false;

    private static  int sizeFile;

    private String getServerAddress() {
        ConsoleHelper.writeMessage("Enter Server address:");
        return ConsoleHelper.readString();
    }

    private int getServerPort() {
        ConsoleHelper.writeMessage("Enter Server port:");
        return ConsoleHelper.readInt();
    }


    private static int getSizeFile() {
        ConsoleHelper.writeMessage("Enter Size File Chang in Kb:");
        return ConsoleHelper.readInt();
    }
    private boolean shouldSendTextFromConsole() {
        return this.clientConnected = true;
    }

    private SocketThread getSocketThread() {
        return new SocketThread();
    }

    private void sendTextMessage(String text) {
        try {
            Message message = new Message(MessageType.FILE, text);
            String data = message.getData();
//            Transfer.dividerFile(new File(data), sizeFile);

            this.connection.send(message);
        } catch (IOException exc) {
            System.out.println("Error");
            exc.printStackTrace();
            clientConnected = false;
        }
    }

    private void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();

        try {
            synchronized (this) {
                this.wait();
            }
        } catch (InterruptedException exc) {
            ConsoleHelper.writeMessage("Error");
            exc.printStackTrace();
            return;
        }

        if (clientConnected) {
            //System.out.println();
            ConsoleHelper.writeMessage("Connect SUCCESS");
        } else {
            ConsoleHelper.writeMessage("Error to client");
        }

        while (clientConnected) {
            String message = ConsoleHelper.readString();

            if (message.equals("exit")) {
                //break;
                this.clientConnected = false;
            }
            if (shouldSendTextFromConsole()) {
                sendTextMessage(message);
            }
        }
    }



    public class SocketThread extends Thread {


        void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + "connected to the chat");
        }

        void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + "disconnected from the chat");
        }

        void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }




        public void run() {
            try {
                String serverAddress = getServerAddress();
                int serverPort = getServerPort();
//                sizeFile = getSizeFile() *1024;
                //еализовать разбитие можно и здесь
                Socket socket = new Socket(serverAddress, serverPort);
                Client.this.connection = new Connection(socket);
            } catch (IOException exc) {
                notifyConnectionStatusChanged(false);
            }
        }

    }




    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
