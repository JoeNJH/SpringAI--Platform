package com.example.springai.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliConfig {

    @Bean
    public ChatClient alibabaChatClient(DashScopeChatModel dashScopeChatModel){

        return ChatClient.builder(dashScopeChatModel).build();

    }


}
