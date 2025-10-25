package com.example.jsonfetcher.components.integration.postfetcher.internal;


import com.example.jsonfetcher.components.integration.postfetcher.api.PostFetchResponse;
import com.example.jsonfetcher.components.integration.postfetcher.api.PostFetchingService;
import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.Post;
import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.PostRepository;
import com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.PostMessage;
import com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.PostMessage.PostData;
import com.example.jsonfetcher.components.integration.postfetcher.spi.publishing.PostMessageSink;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
class DefaultPostFetchingService implements PostFetchingService {

    private final PostRepository postRepository;
    private final PostMessageSink postMessageSink;

    @Override
    public PostFetchResponse fetch() {
        List<Post> posts = postRepository.getAllPosts();
        var postMessage = buildPostMessage(posts);
        postMessageSink.accept(postMessage);
        return new PostFetchResponse(posts.size());
    }

    private PostMessage buildPostMessage(List<Post> posts) {
        List<PostData> postsData = posts.stream()
                .map(this::toPostMessage)
                .toList();
        return new PostMessage(postsData);
    }

    private PostData toPostMessage(Post post) {
        return new PostData(post.id(), post.userId(), post.title(), post.content());

    }
}
