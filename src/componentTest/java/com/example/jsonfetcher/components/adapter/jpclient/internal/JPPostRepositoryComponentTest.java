package com.example.jsonfetcher.components.adapter.jpclient.internal;

import com.example.jsonfetcher.components.adapter.jpclient.JPClientComponentConfig;
import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.Post;
import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {JPClientComponentConfig.class, },
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "jp.connect-timeout=1s",
                "jp.read-timeout=1s",
                "jp.url=${wiremock.server.baseUrl}",
                "resilience4j.enabled=false"
        }
)
@EnableWireMock
class JPPostRepositoryComponentTest {

    @Autowired
    private JPPostRepository jpPostRepository;

    @InjectWireMock
    private WireMockServer wireMockServer;

    @BeforeEach
    void setup() {
        wireMockServer.resetAll();
    }

    @Test
    void shouldFetchPostsFromJpService() {
        //given
        wireMockServer.stubFor(get(urlEqualTo("/posts"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                  {"id":1,"userId":10,"title":"T1","body":"B1"},
                                  {"id":2,"userId":20,"title":"T2","body":"B2"}
                                ]
                                """)
                        .withStatus(200)));

        //when
        var posts = jpPostRepository.getAllPosts();

        //then
        assertThat(posts).hasSize(2);
        assertThat(posts.get(0)).isEqualTo(new Post(1, 10, "T1", "B1"));
        assertThat(posts.get(1)).isEqualTo(new Post(2, 20, "T2", "B2"));

    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public RestClient.Builder restClientBuilder() {
            return RestClient.builder();
        }

    }

}
