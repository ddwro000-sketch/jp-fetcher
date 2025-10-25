package com.example.jsonfetcher.components.integration.postfetcher.spi.publishing;

import java.util.List;

public record PostMessage(List<PostData> posts) {

    public record PostData(long id, long userId, String title, String body) {

    }
}
