package com.example.jsonfetcher.components.adapter.jpclient.internal.resilence;


import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.Post;
import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.PostRepository;
import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.PostRepositoryException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
class Resilence4JPostRepository implements PostRepository {

    private static final String R4J_INSTANCE_NAME = "jpPostClient";

    private final Retry retry;
    private final CircuitBreaker circuitBreaker;
    private final PostRepository delegate;

    public Resilence4JPostRepository(PostRepository delegate,
                                     RetryRegistry retryRegistry,
                                     CircuitBreakerRegistry circuitBreakerRegistry) {
        this.delegate = delegate;
        this.retry = retryRegistry.retry(R4J_INSTANCE_NAME);
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker(R4J_INSTANCE_NAME);
    }

    @Override
    public List<Post> getAllPosts() throws PostRepositoryException {
        try {
            return circuitBreaker.executeCallable(
                    () -> retry.executeCallable(
                            delegate::getAllPosts
                    )
            );
        } catch (Exception e) {
            throw new PostRepositoryException("Failed to fetch all posts");
        }

    }
}
