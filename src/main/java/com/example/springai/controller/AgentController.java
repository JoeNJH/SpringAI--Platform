package com.example.springai.controller;


import com.example.springai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import com.example.springai.constants.StudentPrompt;


@RequiredArgsConstructor
@RestController
@RequestMapping("/agent")
public class AgentController {


    private final ChatClient StudentAgentChatClient;

    private final ChatHistoryRepository chatHistoryRepository;

    @RequestMapping(value = "/chat/1",produces = "text/html;charset=utf-8")
    public Flux<String> chat1(@RequestParam("prompt") String prompt,
                             @RequestParam("chatId") String chatId){

        // 1 存储会话ID
        chatHistoryRepository.save("agent",chatId);

        return StudentAgentChatClient.prompt()
                .system(StudentPrompt.PROMPT1)
                .user(prompt)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,chatId))
                .stream()
                .content();

    }

    @RequestMapping(value = "/chat/2",produces = "text/html;charset=utf-8")
    public Flux<String> chat2(@RequestParam("prompt") String prompt,
                             @RequestParam("chatId") String chatId){

        // 1 存储会话ID
        chatHistoryRepository.save("agent",chatId);

        return StudentAgentChatClient.prompt()
                .system(StudentPrompt.PROMPT2)
                .user(prompt)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,chatId))
                .stream()
                .content();

    }

    @RequestMapping(value = "/chat/3",produces = "text/html;charset=utf-8")
    public Flux<String> chat3(@RequestParam("prompt") String prompt,
                             @RequestParam("chatId") String chatId){

        // 1 存储会话ID
        chatHistoryRepository.save("agent",chatId);

        return StudentAgentChatClient.prompt()
                .system(StudentPrompt.PROMPT3)
                .user(prompt)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,chatId))
                .stream()
                .content();

    }


    @RequestMapping(value = "/chat/4",produces = "text/html;charset=utf-8")
    public Flux<String> chat(@RequestParam("prompt") String prompt,
                             @RequestParam("chatId") String chatId){

        // 1 存储会话ID
        chatHistoryRepository.save("agent",chatId);

        return StudentAgentChatClient.prompt()
                .system(StudentPrompt.PROMPT4)
                .user(prompt)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,chatId))
                .stream()
                .content();

    }



}
