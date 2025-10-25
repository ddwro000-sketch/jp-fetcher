package com.example.jsonfetcher.components.adapter.restcontroller.internal;

import com.example.jsonfetcher.components.integration.postfetcher.api.PostFetchResponse;
import com.example.jsonfetcher.components.integration.postfetcher.api.PostFetchingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostControllerTest {

    @Mock
    PostFetchingService postFetchingService;

    @InjectMocks
    PostController postController;


    @Test
    void shouldReturnFromFetchingService(){
        //given
        var expectedResponse = new PostFetchResponse(100);
        when(postFetchingService.fetch()).thenReturn(expectedResponse);

        //when
        var result = postController.savePosts();

        //then
        assertThat(result).isEqualTo(expectedResponse);
    }

}
