package com.example.test.ai.chroma.v2;

public class CreateTenantOrDbRequest {

    private final String name;


    /**
     * Currently, cosine distance is always used as the distance method for chroma implementation
     */
    CreateTenantOrDbRequest(String name) {
        this.name = name;

    }

    public String getName() {
        return name;
    }


}
