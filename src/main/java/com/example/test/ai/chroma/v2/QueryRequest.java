package com.example.test.ai.chroma.v2;

import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

class QueryRequest {

    //where 过滤器用于通过 metadata 进行过滤，where_document 过滤器用于通过 document 内容进行过滤。
    private final Map<String, Object> where;
    private final List<List<Float>> queryEmbeddings;
    private final int nResults;
    private final List<String> include;

    private QueryRequest(Builder builder) {
        this.where = builder.where;
        this.queryEmbeddings = builder.queryEmbeddings;
        this.nResults = builder.nResults;
        this.include = builder.include;
    }

    public Map<String, Object> getWhere() {
        return where;
    }

    public List<List<Float>> getQueryEmbeddings() {
        return queryEmbeddings;
    }

    public int getnResults() {
        return nResults;
    }

    public List<String> getInclude() {
        return include;
    }

    public static class Builder {

        private Map<String, Object> where;
        private List<List<Float>> queryEmbeddings;
        private int nResults;
        private List<String> include = asList("metadatas", "documents", "distances", "embeddings");

        public Builder where(Map<String, Object> where) {
            this.where = where;
            return this;
        }

        public Builder queryEmbeddings(List<Float> queryEmbeddings) {
            this.queryEmbeddings = singletonList(queryEmbeddings);
            return this;
        }

        public Builder nResults(int nResults) {
            this.nResults = nResults;
            return this;
        }

        public Builder include(List<String> include) {
            this.include = include;
            return this;
        }

        public QueryRequest build() {
            return new QueryRequest(this);
        }
    }
}
