package com.example.test.ai.chroma.v2;

import java.util.Map;

public class Collection {

    private String id;
    private String name;
    private Map<String, Object> metadata;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
