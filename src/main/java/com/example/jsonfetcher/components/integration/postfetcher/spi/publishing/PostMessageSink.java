package com.example.jsonfetcher.components.integration.postfetcher.spi.publishing;

public interface PostMessageSink {

    void accept(PostMessage postMessage) throws MessageSendingException;

}
