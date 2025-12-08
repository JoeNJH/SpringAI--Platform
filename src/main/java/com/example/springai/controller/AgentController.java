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
import java.util.Map;
import java.util.Random;
import java.util.UUID;


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
            You are an AI assistant tasked with creating a balanced, moderately constrained agent persona prompt for a student writer.
            The persona should feel realistic and consistent, but not overly restricted.
            
            Use the following characteristics to construct the system prompt:
            
            Agent Name: {name}
            Age: {age}
            Personality Traits: {personality}
            Writing Style: {writingStyle}
            Speaking Style: {speakingStyle}
            Current State: {currentState}
            Goal: {goal}
            
            Your task:
            Create a system prompt that defines the student's identity, tone, and general interaction tendencies.
            Keep the constraints light—describe how the student usually behaves rather than prescribing strict rules.
            
            Guidelines:
            - Emphasize the student's personality, writing habits, and communication style.
            - Describe natural tendencies (e.g., “often”, “usually”, “tends to”) instead of rigid rules or If/Then structures.
            - Keep the persona consistent with age and student role.
            - Ensure replies stay within the scope of a writing student in a teacher–student conference (no expert-level or adult-professional responses).
            - Allow some spontaneity and variation, as long as the character remains coherent.
            - Avoid meta or AI self-references.
            
            Output:
            Produce a clean, cohesive **System Prompt** that captures the agent’s identity,voice, and typical ways of responding, suitable for use in a teaching simulation environment.
            """;

        return new PromptTemplate(template);
    }

    @PostMapping("/save")
    public Result saveAgent(@RequestBody Map<String, String> requestData) {
        String name = requestData.get("name");
        String prompt = requestData.get("prompt");
    try {
        // 创建Agent对象
        Agent agent = new Agent();
        // 生成随机四位数字并转为字符串
        agent.setAgentId(String.format("%04d", new Random().nextInt(10000)));
        agent.setName(name);
        agent.setPrompt(prompt);

        String description = StudentAgentChatClient.prompt()
                .system("You generate a concise description (under 15 words) that must start with \"A student\" and summarize the given prompt. " +
                        "Output only the description with no extra text, symbols, or explanations.")
                .user(prompt)
                .call()
                .content();

        // 生成描述
        agent.setDescription(description);

        // 保存到数据库
        boolean saved = iAgentService.save(agent);

        if (saved) {
            return Result.ok();
        } else {
            return Result.fail("Failed to save agent");
        }
    } catch (Exception e) {
        return Result.fail("Error saving agent: " + e.getMessage());
    }
}

}
