package com.annakhuseinova.springwebfluxcaching.chat.service;

import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ChatRoomService implements WebSocketHandler {

    @Autowired
    private RedissonReactiveClient reactiveClient;

    /**
     * WebSocketSession object represents the persistent connection between the client
     * and the server
     * */
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String room = "dummy";
        RTopicReactive topic = this.reactiveClient.getTopic(room, StringCodec.INSTANCE);
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(topic::publish)
                .doOnError(System.out::println)
                .doFinally(s -> System.out.println("Subscriber finally "  + s))
                .subscribe();
        Flux<WebSocketMessage> flux = topic.getMessages(String.class)
                .map(session::textMessage)
                .doOnError(System.out::println)
                .doFinally(s -> System.out.println("Subscriber finally " + s));
        /**
         * Frontend will subscribe to this publisher
         * */
        return session.send(flux);
    }
}
