package com.example.jsonfetcher.components.adapter.restcontroller.internal;

import com.example.jsonfetcher.components.adapter.restcontroller.api.PostFetchingApiDoc;
import com.example.jsonfetcher.components.integration.postfetcher.api.PostFetchResponse;
import com.example.jsonfetcher.components.integration.postfetcher.api.PostFetchingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
class PostController implements PostFetchingApiDoc {

    private final PostFetchingService postFetchingService;

    @PostMapping("/fetch")
    public PostFetchResponse savePosts(){
        return postFetchingService.fetch();
    }

}
