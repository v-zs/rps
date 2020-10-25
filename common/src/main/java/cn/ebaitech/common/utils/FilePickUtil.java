package cn.ebaitech.common.utils;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.utils.HttpClientUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FilePickUtil {

    public static void main(String[] args) throws IOException {
        HashMap<String, String[]> map = new HashMap();
        Scanner scanner = new Scanner(System.in);
        System.out.println("输入源文件夹：");
        String sourceDir = scanner.nextLine();
        File file = new File(sourceDir);
        System.out.println("输入目标文件夹：");
        String targetDir = scanner.nextLine();
        findFile(file, new File(sourceDir).getAbsolutePath(), targetDir, map);
        System.out.println("扫描文件数量：" + map.size());
        while (true) {
            System.out.println("输入需要发布文件：");
            String s = scanner.nextLine();
            if (s == null) {
                continue;
            } else if ("exit".equals(s.trim())) {
                break;
            } else {
                fileUnloading(map, s);
            }
        }
    }

    public static void fileUnloading(HashMap<String, String[]> map, String input) throws IOException {
        String[] paths = map.get(input);
        if (paths == null || paths.length == 0) {
            System.out.println("文件 " + input + " 不存在！");
        } else {
            FileInputStream inputStream = new FileInputStream(paths[0]);
            File targetFile = new File(paths[1]);
            File parentFile = targetFile.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            targetFile.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(targetFile);

            byte[] b = new byte[1024];
            while (inputStream.read(b) != -1) {
                outputStream.write(b);
            }
            outputStream.close();
            inputStream.close();
        }
    }

    public static void findFile(File file, String sourceDir, String targetDir, Map map) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                findFile(f, sourceDir, targetDir, map);
            }
        } else {
            String absolutePath = file.getAbsolutePath();
            boolean flag = true;
            for (Ignore value : Ignore.values()) {
                if (absolutePath.endsWith(value.getVal())) {
                    flag = false;
                }
            }
            if (flag) {
                String key = absolutePath.substring(absolutePath.lastIndexOf("\\") + 1);
                String value = absolutePath.replace(sourceDir, targetDir);
                map.put(key, new String[]{absolutePath, value});
            }
        }
    }
}

enum Ignore {
    XML(".xml"),
    JAR(".jar"),
    PROP(".properties");
    private String val;
    Ignore(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }
}