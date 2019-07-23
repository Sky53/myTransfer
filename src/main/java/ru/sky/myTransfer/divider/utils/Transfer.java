package ru.sky.myTransfer.divider.utils;

import ru.sky.myTransfer.divider.client.Client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class Transfer {

    private Client client;

    public static void dividerFile(File file, int filreChangSize) throws IOException {
        int changCounter = 0;

        File fileNewDir = null;

        byte[] buf = new byte[filreChangSize];

        //
        String fileName = file.getName();

        try (FileInputStream fileInputStream = new FileInputStream(file);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream)
        ) {

            int bytesCount = 0;

            while ((bytesCount = bufferedInputStream.read(buf)) > 0) {
                String fileChangName = String.format("%s.%03d", fileName, changCounter++);

                File uploadDir = new File(file.getParent() + "/upload/");

                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

                File newFile = new File(uploadDir + "/", fileChangName);


                try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                    fileOutputStream.write(buf, 0, bytesCount);
                }
            }

        }



    }

    public static void collectorFile(List<File> fileList, File newFile) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(newFile);
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
            for (File file : fileList) {
                Files.copy(file.toPath(), bufferedOutputStream);
            }

        }

    }

    public static List<File> fileToList(File path) throws IOException {

        return Files.walk(Paths.get(path.getParent())).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
    }

    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }


}
