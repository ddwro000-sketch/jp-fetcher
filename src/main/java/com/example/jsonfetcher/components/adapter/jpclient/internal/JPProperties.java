package com.example.jsonfetcher.components.adapter.jpclient.internal;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "jp")
record JPProperties(String url, Duration readTimeout, Duration connectTimeout) {
}
