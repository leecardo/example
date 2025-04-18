package com.example.test.ai.chroma.v2;

import java.util.HashMap;
import java.util.Map;

public class CreateCollectionRequest {

    private final String name;
    private final Map<String, Object> metadata;

    /**
     * Currently, cosine distance is always used as the distance method for chroma implementation
     */
    CreateCollectionRequest(String name) {
        this.name = name;
        HashMap<String, Object> metadata = new HashMap<>();
        metadata.put("hnsw:space", "cosine");
        this.metadata = metadata;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }
}
