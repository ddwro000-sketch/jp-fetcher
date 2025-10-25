package com.example.jsonfetcher.components.adapter.filesink.internal;

import com.example.jsonfetcher.components.adapter.filesink.FileSinkComponentConfig;
import com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.MessageSendingException;
import com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.PostMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.example.jsonfetcher.components.adapter.filesink.internal.FilePostMessageSinkComponentTest.TEST_DIR;
import static com.example.jsonfetcher.components.adapter.filesink.internal.FilePostMessageSinkComponentTest.TestConfig;
import static com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.PostMessage.PostData;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(
        classes = {FileSinkComponentConfig.class, TestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "file.posts.path=" + TEST_DIR
        }
)
class FilePostMessageSinkComponentTest {

    static final String TEST_DIR = "build/tmp/test-posts";

    @Autowired
    private FilePostMessageSink filePostMessageSink;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void clear() throws IOException {
        var path = Paths.get(TEST_DIR);
        FileUtils.deleteDirectory(path.toFile());
    }

    @Test
    void shouldSaveSinglePostToFile() throws IOException {
        // given
        var post = new PostData(1, 2, "Title", "content");
        var message = new PostMessage(List.of(post));

        // when
        filePostMessageSink.accept(message);

        // then
        assertFile(post);
    }

    @Test
    void shouldSaveMultiplePostsToSeparateFiles() throws IOException {
        // given
        var post1 = new PostData(1,1, "Title 1", "Body 1");
        var post2 = new PostData(2, 1, "Title 2", "Body 2");
        var post3 = new PostData(3, 1, "Title 3", "Body 3");
        var message = new PostMessage(List.of(post1, post2, post3));

        // when
        filePostMessageSink.accept(message);

        // then
        assertFile(post1);
        assertFile(post2);
        assertFile(post3);
    }

    @Test
    void shouldOverwriteExistingFile() throws IOException {
        // given
        var originalPost = new PostData(1,1, "Title 1", "Body 1");
        var originalMessage = new PostMessage(List.of(originalPost));
        filePostMessageSink.accept(originalMessage);

        var updatedPost = new PostData(1,1, "New title", "Body 2");
        var updatedMessage = new PostMessage(List.of(updatedPost));

        // when
        filePostMessageSink.accept(updatedMessage);

        // then
        assertFile(updatedPost);
    }

    @Test
    void shouldHandleEmptyPostList() {
        // given
        var message = new PostMessage(List.of());

        // when/then - should not throw exception
        filePostMessageSink.accept(message);

        // Verify no files were created
        assertThat(Paths.get(TEST_DIR)).doesNotExist();
    }

    @Test
    void shouldSaveTwoFilesAndReportFailureForSecond() throws IOException {
        // given
        var post1 = new PostData(1,1, "Title 1", "Body 1");
        var post2 = new PostData(2,1, "Title 2", "Body 2");
        var post3 = new PostData(3,1, "Title 3", "Body 3");

        // This will make second save failed
        var failPath = Paths.get(TEST_DIR, "%s.json".formatted(post2.id()));
        Files.createDirectories(failPath);

        var message = new PostMessage(List.of(post1, post2, post3));

        //when
        var exception = assertThrows(MessageSendingException.class, () -> filePostMessageSink.accept(message));

        //then
        assertFile(post1);
        assertFile(post3);
        assertThat(exception.getErrorDetails()).containsKey("2.json");
    }

    private void assertFile(PostData postToSave) throws IOException {
        var expectedFile = Paths.get(TEST_DIR, "%s.json".formatted(postToSave.id()));
        assertThat(expectedFile).exists();

        var savedPost = objectMapper.readValue(expectedFile.toFile(), PostData.class);
        assertThat(savedPost).isEqualTo(postToSave);
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

    }

}
