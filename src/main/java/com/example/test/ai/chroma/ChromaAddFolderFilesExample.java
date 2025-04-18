package com.example.test.ai.chroma;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import com.example.test.ai.file.DocumentSplitterExample;
import com.example.test.ai.file.DocumentUtils;
import com.example.test.ai.file.FileTypeChecker;
import com.example.test.ai.file.FileWalkerNio;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.segment.TextSegment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChromaAddFolderFilesExample {
    private static final String CHROMA_PERSIST_DIR = "f:\\vcom\\公司文件";

    public static void main(String[] args) {
        // 1. 向量化存储到 chroma
        ChromaService chroma = new ChromaService();

        try {
            List<String> allFiles = FileWalkerNio.getAllFilePaths(CHROMA_PERSIST_DIR);
            for(String fileName:allFiles){
                if(!FileTypeChecker.isPdf(fileName) && !FileTypeChecker.isWordDocument(fileName) && !FileTypeChecker.isExcelDocument(fileName)){
                    continue;
                }
                System.out.println("向量化文件："+ fileName + "-------");
                //分割文件
                List<TextSegment> segments = loadAndChunk(fileName);
                if(CollectionUtil.isNotEmpty(segments)){
                    // 异步插入
                    ExecutorService executor = Executors.newFixedThreadPool(4);
                    List<List<TextSegment>> lists = splitList(segments, 4);
                    lists.forEach(segment -> executor.submit(() -> {
                        // 5. 生成向量并存入 Chroma
                        List<String> ids = chroma.storeDocument(segment);
                        System.out.println(JSONObject.toJSONString(ids));
                    }));
                    executor.shutdown();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static List<TextSegment> loadAndChunk(String filePath)  {
        // 1. 加载文档（示例：PDF 文件）
        File file = new File(filePath);
        try {
            Document document = DocumentUtils.loadDocument(file);
            // 2. 分割文档（选择任一分割器）
            List<TextSegment> segments = DocumentSplitterExample.splitByFixedSize(document);

            // 3. 输出结果
            System.out.println("分块数量: " + segments.size());
            segments.forEach(segment -> {
                System.out.println("内容: " + segment.text() );
                System.out.println("元数据: " + segment.metadata());
            });
            return segments;
        }catch (Exception e){

        }
        return null;
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
