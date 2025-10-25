package com.example.jsonfetcher.components.adapter.jpclient.internal;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(JPProperties.class)
class JPConfig {


    @Bean
    RestClient jpRestClient(RestClient.Builder builder, JPProperties jpProperties) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(jpProperties.readTimeout());
        requestFactory.setConnectTimeout(jpProperties.connectTimeout());
        return builder
                .baseUrl(jpProperties.url())
                .requestFactory(requestFactory)
                .build();
    }

}
