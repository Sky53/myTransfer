package ru.sky.myTransfer.divider.server;

import ru.sky.myTransfer.divider.*;

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

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                // запрос имени
                connection.send(new Message(MessageType.NAME_REQUEST));
                // ответ клиента
                Message message = connection.receive();

                if (message.getType() == MessageType.USER_NAME) { // Проверить, что получена команда с именем пользователя
                    if (!message.getData().isEmpty()) { // проверка имени на пустоту
                        if (connectionMap.get(message.getData()) == null) { // на совпадение
                            connectionMap.put(message.getData(), connection); // добавление новго пользователя
                            connection.send(new Message(MessageType.NAME_ACCEPTED));// ответ клиенту
                            return message.getData();
                        }
                    }
                }
            }
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
                    String clienMes = message.getData();
                    File file = new File(clienMes);
                    String parent = file.getParent() + "/upload/" + file.getName();
                    List<File> files = Transfer.fileToList(new File(parent));
                    Collections.sort(files);
                    String newFileName = new File(parent).getParent() +  "/New__" + file.getName();
                    Transfer.collectorFile(files,new File(newFileName));
//                    String s = userName + ": " + message.getData();

                    Message messageUser = new Message(MessageType.TEXT, clienMes + " complete");
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
                userName = serverHandshake(connection);
                sendListOfUsers(connection,userName);
                serverMainLoop(connection,userName);
            }catch (IOException exc){
                ConsoleHelper.writeMessage("Error communicating with remote address");
            }catch (ClassNotFoundException wxc) {
                ConsoleHelper.writeMessage("Error communicating with remote address");
                wxc.printStackTrace();
            }

            if ( userName != null){
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED,userName));
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