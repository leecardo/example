package com.example.test.ai.file;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class CustomPDFParser {
    private static final int writeLimit  = 10000;

    public static String parseLargePDF(Path filePath) throws Exception {
        PDFParserConfig config = new PDFParserConfig();
        config.setMaxMainMemoryBytes(1 * 1024 * 1024); // 允许 1MB 内存
        config.setOcrStrategy(PDFParserConfig.OCR_STRATEGY.NO_OCR);       // 禁用 OCR（若无需）

        PDFParser parser = new PDFParser();
        parser.setPDFParserConfig(config);

        BodyContentHandler handler = new BodyContentHandler(writeLimit);
        Metadata metadata = new Metadata();
        ParseContext context = new ParseContext();
        context.set(PDFParserConfig.class, config);

        try (InputStream stream = Files.newInputStream(filePath)) {
            parser.parse(stream, handler, metadata, context);
            return handler.toString();
        }
    }
}