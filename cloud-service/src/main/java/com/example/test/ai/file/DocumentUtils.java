package com.example.test.ai.file;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;

import java.io.File;

public class DocumentUtils {

    public static Document loadDocument(File file) throws Exception {
        String text = null;
        if(file.getName().endsWith("pdf")){
            text = CustomPDFParser.parseLargePDF(file.toPath());
        }else {
            text = DocumentReader.parseDocument(file);
        }
        Metadata metadata = Metadata.from("file_name", file.getName());
        return Document.from(text, metadata);
    }
}