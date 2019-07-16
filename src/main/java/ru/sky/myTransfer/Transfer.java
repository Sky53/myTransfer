package ru.sky.myTransfer;

import java.io.*;
import java.nio.file.Files;

public class Transfer {

    public static void dividerFile(File file, Integer filreChangSize) throws IOException {
        int changCounter = 0;

        byte[] buf = new byte[filreChangSize];

        //
        String fileName = file.getName();

        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)
        ) {

            int bytesCount = 0;

            while ((bytesCount = bufferedInputStream.read(buf)) > 0) {
                String fileChangName = String.format("%s.%03d", fileName, changCounter++);


                File newFile = new File(file.getParent() , fileChangName);


                try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                    fileOutputStream.write(buf, 0, bytesCount);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        dividerFile(new File("/home/sky/Музыка/test.mkv"), 1024 * 1024);
    }


}
