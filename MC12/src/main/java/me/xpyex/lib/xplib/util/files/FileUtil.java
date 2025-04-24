package me.xpyex.lib.xplib.util.files;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import me.xpyex.lib.xplib.util.RootUtil;

public class FileUtil extends RootUtil {
    /**
     * 读取目标文本文件
     *
     * @param target 目标文本文件
     * @return 目标文件的文本
     * @throws IOException 文件异常
     */
    public static String readFile(File target) throws IOException {
        List<String> content = Files.readAllLines(target.toPath(), StandardCharsets.UTF_8);
        if (content.isEmpty()) {
            return "";
        }
        return String.join("\n", content);
    }

    /**
     * 向目标文件写出文本
     *
     * @param target  目标文本
     * @param content 要写出的内容
     * @throws IOException 文件异常
     */
    public static void writeFile(File target, String content) throws IOException {
        if (content == null || content.isEmpty()) return;
        if (!target.exists()) {
            target.createNewFile();
        }
        Files.write(target.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }
}
