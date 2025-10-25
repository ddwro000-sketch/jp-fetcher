package com.example.jsonfetcher.components.adapter.filesink.internal;

import com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.MessageSendingException;
import com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.PostMessage;
import com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.PostMessage.PostData;
import com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.PostMessageSink;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
class FilePostMessageSink implements PostMessageSink {

    private final ObjectMapper objectMapper;
    private final FilePostMessageProperties filePostMessageProperties;

    @Override
    public void accept(PostMessage postMessage) throws MessageSendingException {
        Map<String, Exception> failures = new HashMap<>();
        postMessage.posts().parallelStream().forEach(post -> saveToFile(post, failures));

        if (!failures.isEmpty()) {
            throw new MessageSendingException(failures);
        }
    }

    private void saveToFile(PostData post, Map<String, Exception> failures){
        String fileName = "%s.json".formatted(post.id());
        try {
            Path filePath = Paths.get(filePostMessageProperties.path(), fileName);
            Files.createDirectories(filePath.getParent());
            objectMapper.writeValue(filePath.toFile(), post);
        } catch (IOException e){
            log.error("Failed to save file from: {}", post);
            failures.put(fileName, e);
        }
    }
}