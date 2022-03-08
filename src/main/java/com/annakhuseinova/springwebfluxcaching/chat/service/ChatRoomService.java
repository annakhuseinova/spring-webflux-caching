package com.annakhuseinova.springwebfluxcaching.chat.service;

import org.redisson.api.RListReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Service
public class ChatRoomService implements WebSocketHandler {

    @Autowired
    private RedissonReactiveClient reactiveClient;

    /**
     * WebSocketSession object represents the persistent connection between the client
     * and the server.
     *
     * This method will be executed only once per connection
     * */
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String room = getChatRoomName(session);
        RTopicReactive topic = this.reactiveClient.getTopic(room, StringCodec.INSTANCE);
        RListReactive<String> list = this.reactiveClient.getList("history:" + room, StringCodec.INSTANCE);
        session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(msg -> list.add(msg).then(topic.publish(msg)))
                .doOnError(System.out::println)
                .doFinally(s -> System.out.println("Subscriber finally "  + s))
                .subscribe();
        Flux<WebSocketMessage> flux = topic.getMessages(String.class)
                .startWith(list.iterator())
                .map(session::textMessage)
                .doOnError(System.out::println)
                .doFinally(s -> System.out.println("Subscriber finally " + s));
        /**
         * Frontend will subscribe to this publisher
         * */
        return session.send(flux);
    }

    private String getChatRoomName(WebSocketSession session){
        URI uri = session.getHandshakeInfo().getUri();
        return UriComponentsBuilder.fromUri(uri)
                .build()
                .getQueryParams()
                .toSingleValueMap()
                .getOrDefault("room", "default");
    }
}
