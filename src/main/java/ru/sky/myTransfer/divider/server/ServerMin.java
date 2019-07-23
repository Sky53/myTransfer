package ru.sky.myTransfer.divider.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMin {

    static final int PORT = 1457;

    public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Server Started");
        try {
            while (true) {
                Socket socket = s.accept();
                try {
                    new TCPThread(socket);
                } catch (IOException e) {
                    socket.close();
                }
            }
        } finally {
            s.close();
        }
    }
}

class TCPThread extends Thread {
    private Socket socket;
    private InputStream is;
    private BufferedReader in;
    private FileOutputStream wfile;

    public TCPThread(Socket s) throws IOException {
        socket = s;
        is = socket.getInputStream();
        in = new BufferedReader(new InputStreamReader(is));
        start();
    }

    public void run() {
        try {
            int readedBytesCount = 0;
            byte[] buf = new byte[2 * 1024];

            wfile = new FileOutputStream(in.readLine());
            while (true) {
                readedBytesCount = is.read(buf);
                if (readedBytesCount == -1) {
                    break;
                }
                if (readedBytesCount > 0) {
                    wfile.write(buf, 0, readedBytesCount);
                }
            }

            wfile.close();
            System.out.println("Closing...");
        } catch (IOException e) {
            System.out.println("IO Exception");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Socket not closed");
            }
        }
    }
}


