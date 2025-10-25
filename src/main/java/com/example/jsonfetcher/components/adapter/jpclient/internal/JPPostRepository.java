package com.example.jsonfetcher.components.adapter.jpclient.internal;


import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.Post;
import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
class JPPostRepository implements PostRepository {

    private final RestClient jpRestClient;

    @Override
    public List<Post> getAllPosts() {
        List<JPPostDto> posts = fetchPosts();
        log.debug("jp posts response: {}", posts);

        return posts.stream()
                .map(this::toPost)
                .toList();
    }


    private List<JPPostDto> fetchPosts(){
        return jpRestClient.get()
                .uri("/posts")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    private Post toPost(JPPostDto jpPostDto){
        return new Post(jpPostDto.id(), jpPostDto.userId(), jpPostDto.title(), jpPostDto.body());
    }


}
