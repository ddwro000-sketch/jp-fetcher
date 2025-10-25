package com.example.jsonfetcher.components.adapter.restcontroller.api;


import com.example.jsonfetcher.components.integration.postfetcher.api.PostFetchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Post Fetching API", description = "API created for fetching and saving Posts")
public interface PostFetchingApiDoc {

    @Operation(summary = "trigger saving posts")
    @ApiResponse(responseCode = "200", description = "Successfully fetched and saved posts")
    @ApiResponse(responseCode = "500", description = "Server side problem occurred")
    PostFetchResponse savePosts();

}
