package ru.sky.myTransfer.divider;

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

                File uploadDir = new File(file.getParent() + "\\upload\\");

                if (!uploadDir.exists()){
                    uploadDir.mkdir();
                }

                File newFile = new File(uploadDir + "\\"  , fileChangName);



                try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                    fileOutputStream.write(buf, 0, bytesCount);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException {
        dividerFile(new File("C:\\1\\test.mkv"), 1024 * 1024);
//        dividerFile(new File("C:\\1\\test.exe"), 1024 * 1024);
    }


}
