package com.example.test.ai.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileWalkerNio {

    public static List<String> getAllFilePaths(String directoryPath) throws IOException {
        Path startPath = Paths.get(directoryPath);

        if (!Files.exists(startPath)) {
            throw new IllegalArgumentException("路径不存在: " + directoryPath);
        }

        try (Stream<Path> paths = Files.walk(startPath)) {
            return paths
                    .filter(Files::isRegularFile) // 仅保留文件
                    .map(Path::toAbsolutePath)    // 转换为绝对路径
                    .map(Path::toString)           // 转为字符串
                    .collect(Collectors.toList());
        }
    }

    public static void main(String[] args) throws IOException {
        String directoryPath = "C:\\Users\\YourName\\Documents";
        List<String> allFiles = getAllFilePaths(directoryPath);
        allFiles.forEach(System.out::println);
    }
}
