package ru.sky.myTransfer.divider.client;

import ru.sky.myTransfer.divider.*;

import java.io.*;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    public static  int sizeFile;

    protected String getServerAddress() {
        ConsoleHelper.writeMessage("Enter Server address:");
        return ConsoleHelper.readString();
    }

    protected int getServerPort() {
        ConsoleHelper.writeMessage("Enter Server port:");
        return ConsoleHelper.readInt();
    }

    protected String getUserName() {
        ConsoleHelper.writeMessage("Enter user name:");
        return ConsoleHelper.readString();
    }

    public static int getSizeFile() {
        ConsoleHelper.writeMessage("Enter Size File Chang in Kb:");
        return ConsoleHelper.readInt();
    }
    protected boolean shouldSendTextFromConsole() {
        return this.clientConnected = true;
    }

    protected SocketThread getSocketThread() {
        return new SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            Message message = new Message(MessageType.TEXT, text);
            String data = message.getData();
            Transfer.dividerFile(new File(data), sizeFile);
            this.connection.send(message);
        } catch (IOException exc) {
            System.out.println("Error");
            exc.printStackTrace();
            clientConnected = false;
        }
    }

    public void run() {
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

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }



    public class SocketThread extends Thread {


        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + "connected to the chat");
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage(userName + "disconnected from the chat");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST) {
                    String userName = getUserName();
                    connection.send(new Message(MessageType.USER_NAME, userName));
                } else if (message.getType() == MessageType.NAME_ACCEPTED) {
                    notifyConnectionStatusChanged(true);
                    return;
                } else {
                    throw new IOException("Error message type");
                }
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();

                if (message.getType() == MessageType.TEXT) {
                    processIncomingMessage(message.getData());

                } else if (message.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(message.getData());

                } else if (message.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(message.getData());

                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        public void run() {
            try {
                String serverAddress = getServerAddress();
                int serverPort = getServerPort();
                sizeFile = getSizeFile() *1024;
                //еализовать разбитие можно и здесь
                Socket socket = new Socket(serverAddress, serverPort);
                Client.this.connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException exc) {
                notifyConnectionStatusChanged(false);
            }
        }
    }
}
