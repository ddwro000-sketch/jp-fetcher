package com.example.jsonfetcher.components.adapter.jpclient.internal.resilence;

import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.Post;
import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.PostRepository;
import com.example.jsonfetcher.components.integration.postfetcher.spi.fetching.PostRepositoryException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class Resilence4JPostRepositoryTest {

    @Mock
    private PostRepository delegateRepository;

    private RetryRegistry retryRegistry;
    private CircuitBreakerRegistry circuitBreakerRegistry;
    private Resilence4JPostRepository repository;

    @BeforeEach
    void setUp() {
        RetryConfig retryConfig = RetryConfig.custom().maxAttempts(2).build();
        retryRegistry = RetryRegistry.of(retryConfig);

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .minimumNumberOfCalls(3)
                .slidingWindowSize(3)
                .build();
        circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);

        repository = new Resilence4JPostRepository(
                delegateRepository,
                retryRegistry,
                circuitBreakerRegistry
        );
    }

    @Test
    void shouldReturnPostsWhenDelegateSucceeds() {
        // given
        var expectedPosts = List.of(
                new Post(1, 1, "Title 1", "Body 1"),
                new Post(2, 1, "Title 2", "Body 2")
        );
        when(delegateRepository.getAllPosts()).thenReturn(expectedPosts);

        // when
        var result = repository.getAllPosts();

        // then
        assertThat(result).isEqualTo(expectedPosts);
    }


    @Test
    void shouldRetryOnFailureAndSucceedOnSecondAttempt() {
        // given
        var expectedPosts = List.of(new Post(1, 1, "Title 1", "Body 1"));
        when(delegateRepository.getAllPosts())
                .thenThrow(new PostRepositoryException("Temporary failure"))
                .thenReturn(expectedPosts);

        // when
        var result = repository.getAllPosts();

        // then
        assertThat(result).isEqualTo(expectedPosts);
        verify(delegateRepository, times(2)).getAllPosts();
    }

    @Test
    void shouldThrowExceptionAfterMaxRetries() throws PostRepositoryException {
        // given
        when(delegateRepository.getAllPosts())
                .thenThrow(new PostRepositoryException("Persistent failure"));

        // when/then
        assertThatThrownBy(() -> repository.getAllPosts())
                .isInstanceOf(PostRepositoryException.class)
                .hasMessage("Failed to fetch all posts");

        verify(delegateRepository, times(2)).getAllPosts();
    }

    @Test
    void shouldOpenCircuitBreakerAfterMultipleFailures() throws PostRepositoryException {
        // given
        when(delegateRepository.getAllPosts())
                .thenThrow(new PostRepositoryException("Failure"));

        // when - trigger multiple failures to open circuit breaker
        for (int i = 0; i < 3; i++) {
            try {
                repository.getAllPosts();
            } catch (PostRepositoryException e) {
                // Expected - each exception must be caught individually
            }
        }

        // then - circuit breaker should be open
        var circuitBreaker = circuitBreakerRegistry.circuitBreaker("jpPostClient");
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }

    @Test
    void shouldWrapRuntimeExceptionsInPostRepositoryException(){
        // given
        when(delegateRepository.getAllPosts())
                .thenThrow(new RuntimeException("Unexpected error"));

        // expect
        assertThatThrownBy(() -> repository.getAllPosts())
                .isInstanceOf(PostRepositoryException.class)
                .hasMessage("Failed to fetch all posts");
    }

}