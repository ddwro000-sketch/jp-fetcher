package com.example.jsonfetcher.components.integration.postfetcher.internal;

import com.example.jsonfetcher.components.integration.postfetcher.api.PostFetchResponse;
import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.Post;
import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.PostRepository;
import com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.PostMessage;
import com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.PostMessageSink;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.PostMessage.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DefaultPostFetchingServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private PostMessageSink postMessageSink;

    @InjectMocks
    private DefaultPostFetchingService defaultPostFetchingService;

    @Test
    void shouldFetchPostsAndPublishMessages() {
        // given
        var posts = List.of(new Post(1, 1, "Title", "Content"));
        var expectedMessage = new PostMessage(List.of(new PostData(1, 1, "Title", "Content")));

        when(postRepository.getAllPosts()).thenReturn(posts);

        //when
        var result = defaultPostFetchingService.fetch();

        //expect
        verify(postMessageSink, times(1)).accept(expectedMessage);
        assertThat(result).isEqualTo(new PostFetchResponse(1));
    }

}
