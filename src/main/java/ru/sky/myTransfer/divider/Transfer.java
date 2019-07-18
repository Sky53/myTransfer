package ru.sky.myTransfer.divider;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Transfer {

    public static void dividerFile(File file, Integer filreChangSize) throws IOException {
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

//                File uploadDir = new File(file.getParent() + "\\upload\\");
                File uploadDir = new File(file.getParent() + "/upload/");

                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }

//                File newFile = new File(uploadDir + "\\"  , fileChangName);
                File newFile = new File(uploadDir + "/", fileChangName);


                try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                    fileOutputStream.write(buf, 0, bytesCount);
                }
                fileNewDir =newFile;
            }

        }

        assert fileNewDir != null;
        List<File> files = fileToList(fileNewDir);

        Collections.sort(files);

        System.out.println(files);
        String newFileNameforString = fileNewDir.getParent() + "/NEW___" + file.getName();
        System.out.println(newFileNameforString);
//        collectorFile(files,new File(fileNewDir.getParent() + "/NEW___" + fileNewDir.getName()));
        collectorFile(files,new File(newFileNameforString));

    }

    public static void collectorFile(List<File> fileList, File newFile) throws IOException {
        try(FileOutputStream fileOutputStream = new FileOutputStream(newFile);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)) {
            for (File file : fileList){
                Files.copy(file.toPath(), bufferedOutputStream);
            }

        }

    }

    public static List<File> fileToList(File path) throws IOException {

        return Files.walk(Paths.get(path.getParent())).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
    }

    public static void main(String[] args) throws IOException {
//        dividerFile(new File("C:\\1\\test.mkv"), 1024 * 1024);
//        dividerFile(new File("C:\\1\\test.exe"), 1024 * 1024);
        dividerFile(new File("/home/sky/test/test.mkv"), 1024 * 1024);
//        dividerFile(new File("/home/sky/test/test.mp3"), 1024 * 1024);
    }


}
