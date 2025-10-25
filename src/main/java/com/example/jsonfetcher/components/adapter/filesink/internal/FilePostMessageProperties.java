package com.example.jsonfetcher.components.adapter.filesink.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "file.posts")
record FilePostMessageProperties(String path) {
}
