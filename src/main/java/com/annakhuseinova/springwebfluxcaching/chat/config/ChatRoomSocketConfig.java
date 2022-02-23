package com.annakhuseinova.springwebfluxcaching.chat.config;

import com.annakhuseinova.springwebfluxcaching.chat.service.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.Map;

@Configuration
public class ChatRoomSocketConfig {

    @Autowired
    private ChatRoomService chatRoomService;

    @Bean
    public HandlerMapping handlerMapping(){
        Map<String, WebSocketHandler> map = Map.of(
            "/chat", chatRoomService
        );
        return new SimpleUrlHandlerMapping(map);
    }
}
