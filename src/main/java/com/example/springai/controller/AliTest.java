package com.example.springai.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
@RestController
@RequestMapping("/a")
public class AliTest {

    private final ChatClient alibabaChatClient;


    // 加入模型随意切换
    @RequestMapping(value = "/test",produces = "text/html;charset=utf-8")
    public Flux<String> chat1(){

        return alibabaChatClient.prompt().options(ChatOptions.builder().model("deepseek-v3").build())
                .user("你是谁？")
                .stream()
                .content();
    }

}
