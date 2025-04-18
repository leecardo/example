package com.example.test.ai.ollama;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OllamaChatExample {

    private static final Logger log = LoggerFactory.getLogger(OllamaChatExample.class);

    static final String OLLAMA_IMAGE = "ollama/ollama:latest";
    static final String TINY_DOLPHIN_MODEL = "tinydolphin";
    static final String DOCKER_IMAGE_NAME = "tc-ollama/ollama:latest-tinydolphin";

    public static void main(String[] args) {
        // Create and start the Ollama container
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
//
//        // Pull the model and create an image based on the selected model.
//        try {
//            log.info("Start pulling the '{}' model ... would take several minutes ...", TINY_DOLPHIN_MODEL);
//            Container.ExecResult r = ollama.execInContainer("ollama", "pull", TINY_DOLPHIN_MODEL);
//            log.info("Model pulling competed! {}", r);
//        } catch (IOException | InterruptedException e) {
//            throw new RuntimeException("Error pulling model", e);
//        }
//        ollama.commitToImage(DOCKER_IMAGE_NAME);

        // Build the ChatLanguageModel
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://127.0.0.1:11434/")
                .modelName("qwen2.5:1.5b")
                .temperature(0.5)

                .logRequests(true)
                .logResponses(true)
                .build();

        // Example usage
        String answer = model.chat("你能帮我解决哪些类型的问题");
        System.out.println(answer);

        // Stop the Ollama container
        //ollama.stop();
    }
}
