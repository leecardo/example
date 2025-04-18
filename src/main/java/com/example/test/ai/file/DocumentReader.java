package com.example.test.ai.file;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class DocumentReader {
    private static final int writeLimit  = 10000;

    private static final Tika tika = new Tika();

    public static String parseDocument(File file) throws IOException, TikaException, SAXException {
        // 使用流式 ContentHandler（限制内容长度，避免 OOM）
        ContentHandler handler = new BodyContentHandler(writeLimit); // -1 表示不限制大小
        AutoDetectParser parser = new AutoDetectParser();
        ParseContext context = new ParseContext();

        try (InputStream stream = Files.newInputStream(file.toPath())) {
            parser.parse(stream, handler, new Metadata(), context);
            return handler.toString();
        }

//        try (InputStream stream = Files.newInputStream(file.toPath())) {
//            // 自动检测文件类型并解析内容
//            return tika.parseToString(stream);
//        }
    }
}