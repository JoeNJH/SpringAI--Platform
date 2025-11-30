package com.example.springai.controller;


import com.example.springai.entity.po.Agent;
import com.example.springai.repository.ChatHistoryRepository;
import com.example.springai.service.IAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import com.example.springai.constants.StudentPrompt;

import java.util.List;


@RequiredArgsConstructor
@RestController
@RequestMapping("/agent")
public class AgentController {


    private final ChatClient StudentAgentChatClient;

    private final ChatHistoryRepository chatHistoryRepository;

    private final IAgentService iAgentService;

    @GetMapping("/all")
    public List<Agent> getChatIds(){

        return iAgentService.lambdaQuery().list();
    }

    @RequestMapping(value = "/chat/{agentId}",produces = "text/html;charset=utf-8")
    public Flux<String> chat(
            @PathVariable("agentId") String agentId,
            @RequestParam("prompt") String prompt,
            @RequestParam("chatId") String chatId){

        // 1 存储会话ID
        chatHistoryRepository.saveAgent(chatId,agentId);

        return StudentAgentChatClient.prompt()
                .system(StudentPrompt.PROMPT1)
                .user(prompt)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,chatId))
                .stream()
                .content();

    }

}
