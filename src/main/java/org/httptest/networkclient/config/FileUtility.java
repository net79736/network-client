package org.httptest.networkclient.config;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class FileUtility {

    /**
     * 디렉토리가 존재하는지 체크한다.
     *
     * @param directory
     * @return
     */
    public static boolean isDirectory(String directory) {
        File file = new File(directory);
        if (file.isDirectory())
            return true;
        else
            return false;
    }

    /**
     * 폴더 안에 들어있는 파일의 절대경로를 List 형태로 반환한다.
     *
     * @param directory
     * @return
     */
    public static List<String> fileList(String directory) {
        List<String> fileList = new ArrayList<String>();
        File dir = new File(directory);
        File files[] = dir.listFiles(); // 디렉토리의 파일 내용을 추출한다.
        for (int i = 0; i < files.length; i++) {
            fileList.add(files[i].getAbsolutePath()); // 절대 경로 추출
        }
        return fileList;
    }

    /**
     * 폴더가 존재하지 않으면 폴더를 생성한다.
     *
     * @param directory
     * @return
     */
    public static boolean makeDirectory(String directory) {
        File file = new File(directory);
        // 폴더가 존재하지 않는 경우 폴더를 생성한다.
        if (!file.mkdirs()) {
            log.info("directory make : {}", directory);
            return false;
        }
        // 이미 해당 경로 폴더가 존재하는 경우 바로 아래를 떨어짐
        return true;
    }

    public static boolean storeDirectory(String directory) {
        // 폴더가 존재하는 이미 존재하는 경우 함수 종료
        if (isDirectory(directory)) {
            return true;
        }
        // 존재하지 않는 폴더 경우 생성
        makeDirectory(directory);
        return true;
    }

    // 폴더를 삭제한다.
    public static boolean deleteDirectory(String directory) {
        File file = new File(directory);
        if (!file.delete()) {
            log.info("directory delete fail");
            return false;
        }
        return true;
    }

    // 파일을 삭제한다.
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        if (!file.delete()) {
            return false;
        }
        return true;
    }

    // 파일이 존재하는지 체크
    public static boolean isFile(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }

    public static boolean copyFile(String sourceFile, String destinationFile) {
        try {
            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(destinationFile);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            // sourceFile 파일의 내용을 읽어서 destinationFile 에 쓴다.
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        } catch (IOException ioe) {
            log.error("moveFile fail ioe = {}", ioe.toString());
            return false;
        } catch (Exception e) {
            log.error("moveFile fail e = {}", e.toString());
            return false;
        }
        return true;
    }

    // 파일을 이동시킨다.
    public static boolean moveFile(String sourceFile, String destinationFile) {
        if (isFile(sourceFile)) {
            if (copyFile(sourceFile, destinationFile)) {
                deleteFile(sourceFile);
            } else {
                System.out.println("copyFile fail");
                return false;
            }
        } else {
            System.out.println("isFile(sourceFile) fail");
            return false;
        }
        return true;
    }

    /**
     * 파일의 이름을 반환한다.
     *
     * @param filePath 파일 절대 경로
     * @return
     */
    public static String fileName(String filePath) {
        File f = new File(filePath);
        if(f != null)
            return f.getName();
        return null;
    }

    // 확장자를 제외한 파일을 이름을 반환한다.
    public static String prefixFileName(String fileFullName) {
        int index = fileFullName.lastIndexOf(".");
        String fileName = fileFullName.substring(0, index);
        return fileName;
    }

    // 파일의 확장자를 반환한다.
    public static String sufixFileName(String fileFullName) {
        int index = fileFullName.lastIndexOf(".");
        String fileExtension = fileFullName.substring(index + 1);
        return fileExtension;
    }

    // 파일을 읽어 문자열 형태의 문자열로 반환한다.
    public static String readFile(String readFileAbsPath, String characterSet) {
        try {
            return new String(Files.readAllBytes(Paths.get(readFileAbsPath)), characterSet);
        } catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
            return "";
        } catch (IOException e) {
//			e.printStackTrace();
            return "";
        }
    }

    // 파일에 내용을 쓴다
    public static void writeFile(String writeFileAbsPath, String characterSet, String content) {
        try {
            File f = new File(writeFileAbsPath);
            storeDirectory(f.getParentFile().toString());

            deleteFile(writeFileAbsPath);
            Files.write(Paths.get(writeFileAbsPath), content.getBytes(characterSet), StandardOpenOption.CREATE);
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }
    }

    // 파일의 내용을 쓴다.
    // 1. 기존 내용이 존재하는 경우 기존 내용에 더하며
    // 2. 파일이 존재하지 않는 경우 새로 내용을 쓴다
    public static void writeAppendFile(String writeFileAbsPath, String characterSet, String content) {
        try {
            File f = new File(writeFileAbsPath);
            storeDirectory(f.getParentFile().toString());

            if(isFile(writeFileAbsPath))
                Files.write(Paths.get(writeFileAbsPath), content.getBytes(characterSet),StandardOpenOption.APPEND);
            else
                Files.write(Paths.get(writeFileAbsPath), content.getBytes(characterSet),StandardOpenOption.CREATE);
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
        }
    }
}
