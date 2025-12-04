package com.example.springai.controller;


import com.example.springai.entity.po.Agent;
import com.example.springai.entity.vo.Result;
import com.example.springai.entity.vo.persona;
import com.example.springai.repository.ChatHistoryRepository;
import com.example.springai.service.IAgentService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

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

    @RequestMapping(value = "/del/{agentId}")
    public Result delete(@PathVariable("agentId") String agentId){
        iAgentService.removeById(agentId);
        return Result.ok();
    }



    @RequestMapping(value = "/chat/{agentId}",produces = "text/html;charset=utf-8")
    public Flux<String> chat(
            @PathVariable("agentId") String agentId,
            @RequestParam("prompt") String prompt,
            @RequestParam("chatId") String chatId){

        // 1 存储会话ID
        chatHistoryRepository.saveAgent(chatId,agentId);

        Agent agentDiy = iAgentService.lambdaQuery()
                .select(Agent::getPrompt).eq(Agent::getAgentId,agentId).one();

        return StudentAgentChatClient.prompt()
                .system(agentDiy.getPrompt())
                .user(prompt)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,chatId))
                .stream()
                .content();

    }


    @PostMapping("/prompt")
    public String generateAgentPrompt(@RequestBody persona personaData) {
        PromptTemplate promptTemplate = getPromptTemplate();
        String renderedPrompt = promptTemplate.render(java.util.Map.of(
                "name", personaData.getName(),
                "age", personaData.getAge(),
                "personality", personaData.getPersonality(),
                "writingStyle", personaData.getWritingStyle(),
                "speakingStyle", personaData.getSpeakingStyle(),
                "currentState", personaData.getCurrentState(),
                "goal", personaData.getGoal()
        ));

        // Generate the prompt using the AI model
        return StudentAgentChatClient.prompt()
                .system("You are an expert at creating AI agent system prompts.")
                .user(renderedPrompt)
                .call()
                .content();
    }

    @NotNull
    private static PromptTemplate getPromptTemplate() {
        String template = """
            You are an AI assistant tasked with creating a detailed agent profile prompt based on specific characteristics.
            
            Agent Name: {name}
            Age: {age}
            Personality Traits: {personality}
            Writing Style: {writingStyle}
            Speaking Style: {speakingStyle}
            Current State: {currentState}
            Goal: {goal}
            
            Based on these characteristics, create a comprehensive system prompt that defines how an AI agent with these traits should behave, respond, and interact. The prompt should be detailed and include specific instructions on how to embody these characteristics in all interactions.
            
            System Prompt:
            """;

        return new PromptTemplate(template);
    }

}
