package com.example.test.ai.file;

import org.apache.tika.Tika;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileTypeChecker {

    private static final Tika tika = new Tika();

    public static boolean isPdf(Path filePath) throws IOException {
        return detectMimeType(filePath).equals("application/pdf");
    }

    public static boolean isWordDocument(Path filePath) throws IOException {
        String mimeType = detectMimeType(filePath);
        return mimeType.equals("application/msword") ||          // DOC
                mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document"); // DOCX
    }

    public static boolean isExcelDocument(Path filePath) throws IOException {
        String mimeType = detectMimeType(filePath);
        return mimeType.equals("application/vnd.ms-excel") ||    // XLS
                mimeType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // XLSX
    }

    private static String detectMimeType(Path filePath) throws IOException {
        try (InputStream stream = Files.newInputStream(filePath)) {
            // Tika 会分析文件内容和扩展名，返回 MIME 类型
            return tika.detect(stream);
        }
    }
    public static boolean isPdf(String fileName) {
        return fileName.toLowerCase().endsWith(".pdf");
    }

    public static boolean isWordDocument(String fileName) {
        String lowerName = fileName.toLowerCase();
        return lowerName.endsWith(".doc") || lowerName.endsWith(".docx");
    }

    public static boolean isExcelDocument(String fileName) {
        String lowerName = fileName.toLowerCase();
        return lowerName.endsWith(".xls") || lowerName.endsWith(".xlsx");
    }
}