package com.example.test.ai.file;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentByCharacterSplitter;
import dev.langchain4j.data.document.splitter.DocumentByParagraphSplitter;
import dev.langchain4j.data.document.splitter.DocumentBySentenceSplitter;
import dev.langchain4j.data.segment.TextSegment;
import java.util.List;

public class DocumentSplitterExample {

    public static List<TextSegment> splitBySentence(Document document) {
        DocumentBySentenceSplitter splitter = new DocumentBySentenceSplitter(50,5);
        return splitter.split(document);
    }

    public static List<TextSegment> splitByParagraph(Document document) {
        DocumentByParagraphSplitter splitter = new DocumentByParagraphSplitter(10,1);
        return splitter.split(document);
    }

    public static List<TextSegment> splitByFixedSize(Document document) {
        // 每块最大字符数 块间重叠字符数
        DocumentByCharacterSplitter splitter = new DocumentByCharacterSplitter(500,50);
        return splitter.split(document);
    }
}