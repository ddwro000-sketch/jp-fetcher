package com.example.jsonfetcher;

import com.example.jsonfetcher.components.integration.postfetcher.api.PostFetchResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import java.io.IOException;
import java.nio.file.Paths;

import static com.example.jsonfetcher.JsonFetcherApplicationIntegrationTest.POST_TESTS_PATH;
import static com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.PostMessage.PostData;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@EnableWireMock
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "file.posts.path="+ POST_TESTS_PATH,
                "jp.connect-timeout=1s",
                "jp.read-timeout=1s",
                "jp.url=${wiremock.server.baseUrl}",
        }
)
public class JsonFetcherApplicationIntegrationTest {

    static final String POST_TESTS_PATH = "build/tmp/it/posts";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectWireMock
    private WireMockServer wireMockServer;

    @BeforeEach
    void setup() {
        wireMockServer.resetAll();
    }

    @AfterEach
    void clear() throws IOException {
        var path = Paths.get(POST_TESTS_PATH);
        FileUtils.deleteDirectory(path.toFile());
    }


    @Test
    void shouldFetchAndSavePostsSuccessfully() throws IOException {
        // given
        String externalApiResponse = """
                [
                    {
                        "id": "1",
                        "userId": "10",
                        "title": "Test Post 1",
                        "body": "Test Body 1"
                    },
                    {
                        "id": "2",
                        "userId": "10",
                        "title": "Test Post 2",
                        "body": "Test Body 2"
                    }
                ]
                """;

        wireMockServer.stubFor(get(urlEqualTo("/posts"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(externalApiResponse)));

        // when
        var response = restTemplate.postForEntity("/api/v1/posts/fetch", null, PostFetchResponse.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(new PostFetchResponse(2));

        assertFile(new PostData(1, 10, "Test Post 1", "Test Body 1"));
        assertFile(new PostData(2, 10, "Test Post 2", "Test Body 2"));
    }

    private void assertFile(PostData postToSave) throws IOException {
        var expectedFile = Paths.get(POST_TESTS_PATH, "%s.json".formatted(postToSave.id()));
        assertThat(expectedFile).exists();

        var savedPost = objectMapper.readValue(expectedFile.toFile(), PostData.class);
        assertThat(savedPost).isEqualTo(postToSave);
    }

}
