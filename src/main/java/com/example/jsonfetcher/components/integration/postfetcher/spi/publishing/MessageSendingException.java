package com.example.jsonfetcher.components.integration.postfetcher.spi.publishing;

import lombok.Getter;

import java.util.Map;

@Getter
public class MessageSendingException extends RuntimeException {

    private final Map<String, Exception> errorDetails;

    public MessageSendingException(Map<String, Exception> errorDetails) {
        this.errorDetails = errorDetails;
    }

}
