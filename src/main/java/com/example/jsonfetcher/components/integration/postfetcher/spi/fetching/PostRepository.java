package com.example.jsonfetcher.components.integration.postfetcher.spi.fetching;

import java.util.List;

public interface PostRepository {

    List<Post> getAllPosts() throws PostRepositoryException;

}
