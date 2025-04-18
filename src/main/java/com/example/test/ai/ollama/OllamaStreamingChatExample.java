package com.example.test.ai.ollama;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

public class OllamaStreamingChatExample {

    private static final Logger log = LoggerFactory.getLogger(OllamaStreamingChatExample.class);

    static final String OLLAMA_IMAGE = "ollama/ollama:latest";
    static final String MODEL = "qwen2.5:1.5b";
    static final String BASE_URL = "http://localhost:11434";
    static final String DOCKER_IMAGE_NAME = "tc-ollama/ollama:latest-tinydolphin";

    public static void main(String[] args) {
//        DockerImageName dockerImageName = DockerImageName.parse(OLLAMA_IMAGE);
//        DockerClient dockerClient = DockerClientFactory.instance().client();
//        List<Image> images = dockerClient.listImagesCmd().withReferenceFilter(DOCKER_IMAGE_NAME).exec();
//        OllamaContainer ollama;
//        if (images.isEmpty()) {
//            ollama = new OllamaContainer(dockerImageName);
//        } else {
//            ollama = new OllamaContainer(DockerImageName.parse(DOCKER_IMAGE_NAME).asCompatibleSubstituteFor(OLLAMA_IMAGE));
//        }
//        ollama.start();
//        try {
//            log.info("Start pulling the '{}' model ... would take several minutes ...", TINY_DOLPHIN_MODEL);
//            Container.ExecResult r = ollama.execInContainer("ollama", "pull", TINY_DOLPHIN_MODEL);
//            log.info("Model pulling competed! {}", r);
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException("Error pulling model", e);
//        }
//        ollama.commitToImage(DOCKER_IMAGE_NAME);

        StreamingChatLanguageModel model = OllamaStreamingChatModel.builder()
                .baseUrl(BASE_URL)
                .temperature(0.0)
                .logRequests(true)
                .logResponses(true)
                .modelName(MODEL)
                .build();

        String userMessage = "写一首关于java和ai的100字的诗";

        CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();
        model.chat(userMessage, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.print(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                futureResponse.complete(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                futureResponse.completeExceptionally(error);
            }
        });

        futureResponse.join();
        //ollama.stop();
    }

}