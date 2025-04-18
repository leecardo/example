package com.example.test.ai.file;

import com.alibaba.fastjson2.JSONObject;
import com.example.test.ai.chroma.ChromaService;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class BatchDocumentImporter {
    private static final String CHROMA_PERSIST_DIR = "f:\\vcom";
    private static final String COLLECTION_NAME = "documents";
    private static final int CHUNK_SIZE = 500;  // 分块大小
    private static final int STEP = 300;        // 分块步长

    public static void main(String[] args) {
        // 1. 向量化存储到 chroma
        ChromaService chroma = new ChromaService();

        // 2. 遍历文件夹中的所有文件
        Path directory = Paths.get("f:\\vcom");
        try (Stream<Path> paths = Files.walk(directory)) {
            paths.filter(Files::isRegularFile)
                    .forEach(file -> importFile(chroma, file));
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static List<dev.langchain4j.data.segment.TextSegment> loadAndChunk(String filePath) throws Exception {
        // 1. 加载文档（示例：PDF 文件）
        File file = new File(filePath);
        Document document = DocumentUtils.loadDocument(file);


        // 2. 分割文档（选择任一分割器）
        List<TextSegment> segments = DocumentSplitterExample.splitByFixedSize(document);

        // 3. 输出结果
//        System.out.println("分块数量: " + segments.size());
//        segments.forEach(segment -> {
//            System.out.println("内容: " + segment.text() );
//            System.out.println("元数据: " + segment.metadata());
//        });
        return segments;
    }


    // 导入单个文件
    private static void importFile(ChromaService chroma, Path filePath) {
        try {
            if (FileTypeChecker.isWordDocument(filePath)) {
                System.out.println("这是一个 Word 文档");
            } else if (FileTypeChecker.isExcelDocument(filePath)) {
                System.out.println("这是一个 Excel 文档");
            } else if (FileTypeChecker.isPdf(filePath)) {
                System.out.println("这是一个 PDF 文档");
            } else {
                return;
            }
            String fileName = filePath.getFileName().toString();
            if(!FileTypeChecker.isPdf(fileName) && !FileTypeChecker.isWordDocument(fileName) && !FileTypeChecker.isExcelDocument(fileName)){
                return;
            }
            //分割文件
            List<TextSegment> segments = loadAndChunk(fileName);
            // 异步插入
            ExecutorService executor = Executors.newFixedThreadPool(4);
            List<List<TextSegment>> lists = splitList(segments, 4);
            lists.forEach(segment -> executor.submit(() -> {
                // 5. 生成向量并存入 Chroma
                List<String> ids = chroma.storeDocument(segment);
                System.out.println(JSONObject.toJSONString(ids));
            }));
            executor.shutdown();


        } catch (Exception e) {
            System.err.println("导入文件失败：" + filePath);
            e.printStackTrace();
        }
    }

    // 使用 Tika 提取文本
    private static String extractText(File file) throws IOException, TikaException, SAXException {
        BodyContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();
        AutoDetectParser parser = new AutoDetectParser();
        ParseContext parseContext = new ParseContext();

        try (InputStream inputStream = new FileInputStream(file)) {
            parser.parse(inputStream, handler, metadata, parseContext);
            return handler.toString();
        }
    }

    public static <E> List<List<E>> splitList(List<E> list, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n 必须大于 0");
        }
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        List<List<E>> result = new ArrayList<>();
        int size = list.size();
        int partSize = size / n;
        int remainder = size % n;

        int startIndex = 0;
        for (int i = 0; i < n; i++) {
            // 前 remainder 个部分多一个元素
            int currentPartSize = partSize + (i < remainder ? 1 : 0);
            if (startIndex >= size) {
                break;
            }

            int endIndex = Math.min(startIndex + currentPartSize, size);
            List<E> sublist = list.subList(startIndex, endIndex);
            result.add(new ArrayList<>(sublist));  // 避免返回原列表的视图

            startIndex = endIndex;
        }
        return result;
    }
}