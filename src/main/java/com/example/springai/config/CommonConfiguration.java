package com.example.springai.config;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.example.springai.constants.StudentPrompt;
import com.example.springai.constants.SystemConstants;
import com.example.springai.tools.CourseTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfiguration {


    // 数据库持久化存储配置
    @Autowired
    JdbcChatMemoryRepository chatMemoryRepository;

    @Bean
    public ChatMemory chatMemory(){

       //设置一个最大的消息存储数量
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20).build();
    }

    // 多模态对话客户端配置

    @Bean
    public ChatClient chatClient(OpenAiChatModel model, ChatMemory chatMemory, ToolCallbackProvider toolCallbackProvider){

        return ChatClient.builder(model)
                .defaultSystem("你是一个智能助手,名字叫小灰灰,请以小灰灰的身份回答问题,遇到需要联网搜索的问题请使用Jina")
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                 // 敏感词校验
                        new SafeGuardAdvisor(SystemConstants.SENSITIVE_WORDS),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                // MCP功能调用
                .defaultToolCallbacks(toolCallbackProvider)
                .build();
    }

    //   模拟女友游戏客户端配置

    @Bean
    public ChatClient gameChatClient(OpenAiChatModel model, ChatMemory chatMemory){

        return ChatClient.builder(model)
                .defaultSystem(SystemConstants.SYSTEM_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    //   AI客服客户端配置

    @Bean
    public ChatClient serviceChatClient(OpenAiChatModel model, ChatMemory chatMemory, CourseTools courseTools){

        return ChatClient.builder(model)
                .defaultSystem(SystemConstants.SERVICE_SYSTEM_PROMPT)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultTools(courseTools)
                .build();
    }


    //  RAG信息检索增强生成器客户端配置

    @Bean
    public ChatClient pdfChatClient(OpenAiChatModel model, ChatMemory chatMemory, VectorStore vectorStore){

        return ChatClient.builder(model)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),

//                        QuestionAnswerAdvisor.builder(vectorStore)
//                                .searchRequest(SearchRequest.builder().similarityThreshold(0.5).topK(8).build())
//                                .build()

                       //  增强版RAG顾问机制
                        RetrievalAugmentationAdvisor.builder()
                                .documentRetriever(VectorStoreDocumentRetriever.builder()
                                        .similarityThreshold(0.50)
                                        .topK(8)
                                        .vectorStore(vectorStore)
                                        .build())
                                // 查询重写
                                .queryTransformers(RewriteQueryTransformer.builder()
                                        .chatClientBuilder(ChatClient.builder(model))
                                        .build())
                                .build()
                )
                .build();
    }



    /// 模拟学生的客户端配置 4种不同学生

    //   模拟学生的客户端配置1

    @Bean
    public ChatClient Agent1ChatClient(OpenAiChatModel model, ChatMemory chatMemory){

        return ChatClient.builder(model)
                .defaultSystem(StudentPrompt.PROMPT1)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    //   模拟学生的客户端配置2

    @Bean
    public ChatClient Agent2ChatClient(OpenAiChatModel model, ChatMemory chatMemory){

        return ChatClient.builder(model)
                .defaultSystem(StudentPrompt.PROMPT2)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    //   模拟学生的客户端配置3

    @Bean
    public ChatClient Agent3ChatClient(OpenAiChatModel model, ChatMemory chatMemory){

        return ChatClient.builder(model)
                .defaultSystem(StudentPrompt.PROMPT3)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    //   模拟学生的客户端配置4

    @Bean
    public ChatClient Agent4ChatClient(OpenAiChatModel model, ChatMemory chatMemory){

        return ChatClient.builder(model)
                .defaultSystem(StudentPrompt.PROMPT4)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor(),
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }


    //  引入阿里百炼平台

    @Bean
    public ChatClient alibabaChatClient(DashScopeChatModel dashScopeChatModel){

        return ChatClient.builder(dashScopeChatModel).build();

    }


}
