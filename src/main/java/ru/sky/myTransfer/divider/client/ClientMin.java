package ru.sky.myTransfer.divider.client;

import ru.sky.myTransfer.divider.utils.ConsoleHelper;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientMin {

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.out.println("Error. Usage <filename> <hostname>");
        }
        System.out.println("Enter host name");
        String hostName = ConsoleHelper.readString();
        InetAddress addr = InetAddress.getByName(hostName);
        System.out.println("Connection to: " + addr);
        System.out.println("Enter port");
        int portNmber = ConsoleHelper.readInt();
        Socket socket = new Socket(addr,portNmber );
        String filenName = ConsoleHelper.readString();
        try {
            int readedBytesCount = 0;
            System.out.println("socket = " + socket);
            OutputStream os = socket.getOutputStream();
            PrintWriter out = new PrintWriter(os, true);
            FileInputStream rfile = new FileInputStream(filenName);

            out.println(filenName);

            byte[] buf = new byte[2 * 1024];
            while (true) {
                readedBytesCount = rfile.read(buf);
                if (readedBytesCount == -1) {
                    break;
                }
                if (readedBytesCount > 0) {
                    os.write(buf, 0, readedBytesCount);
                    out.flush();
                }
            }
            rfile.close();
        } finally {
            System.out.println("closing...");
            socket.close();
        }
    }
}
