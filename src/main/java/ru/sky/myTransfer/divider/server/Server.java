package ru.sky.myTransfer.divider.server;

import ru.sky.myTransfer.divider.*;
import ru.sky.myTransfer.divider.utils.*;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();


    public static void sendBroadcastMessage(Message message) {
        try {
            for (Map.Entry<String, Connection> map : connectionMap.entrySet()) {
                map.getValue().send(message);
            }
        } catch (IOException exc) {
            ConsoleHelper.writeMessage("Error");
            exc.printStackTrace();
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }



        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            for (Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
                if (pair.getKey().equals(userName)) {
                    break;
                }
                try {
                    connection.send(new Message(MessageType.USER_ADDED, pair.getKey()));
                } catch (IOException exc) {
                    ConsoleHelper.writeMessage("Error");
                    exc.printStackTrace();
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();

                if (message.getType() == MessageType.TEXT) {
                    //TODO
                    String clientFile = message.getData();
                    File file = new File(clientFile);
                    String parent = file.getParent() + "/" + userName + " upload/" + file.getName();
                    List<File> files = Transfer.fileToList(new File(parent));
                    Collections.sort(files);
                    String tranfertO = file.getParent() + "/RESALT/" + file.getName();
                    File fileOk = new File(new File(tranfertO).getParent());

                    if (!fileOk.exists()) {
                        fileOk.mkdir();
                    }

                    Transfer.collectorFile(files, new File(tranfertO));
                    File fileToDelite = new File(new File(parent).getParent());
                    Transfer.deleteDirectory(fileToDelite);

//                      String s = userName + ": " + message.getData();

                    Message messageUser = new Message(MessageType.TEXT, clientFile + " complete");
                    sendBroadcastMessage(messageUser);
                } else {
                    ConsoleHelper.writeMessage("Error");
                }
            }
        }

        @Override
        public void run() {
            super.run();

            ConsoleHelper.writeMessage("Connected to the address " + socket.getRemoteSocketAddress());
            String userName = null;

            try (Connection connection = new Connection(socket)) {
                ConsoleHelper.writeMessage("Connect to port: " + connection.getRemoteSocketAddress());

            } catch (IOException exc) {
                ConsoleHelper.writeMessage("Error communicating with remote address");


                if (userName != null) {
                    connectionMap.remove(userName);
                    sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                }
                ConsoleHelper.writeMessage("The connection to the remote address closed\n");
            }
        }

        public static void main(String[] args) throws IOException {
            ConsoleHelper.writeMessage("Enter Port");
            int serverPort = ConsoleHelper.readInt();


            try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
                ConsoleHelper.writeMessage("Server started");

                while (true) {
                    Socket socket = null;
                    try {
                        socket = serverSocket.accept();
                    } catch (IOException exc) {
                        exc.printStackTrace();
                        break;
                    }
                    Handler handler = new Handler(socket);
                    handler.start();

                }
            }
        }
    }
}