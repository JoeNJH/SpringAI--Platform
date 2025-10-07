package com.example.springai;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.FactCheckingEvaluator;
import org.springframework.ai.document.Document;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class SpringAiApplicationTests {



    // 模型RAG 幻觉评估
    @Test
    void testRAG(@Autowired OpenAiChatModel chatModel) {

        FactCheckingEvaluator factCheckingEvaluator = new FactCheckingEvaluator(ChatClient.builder(chatModel));

        // 大模型向量库检索到的上下文
        Document doc = Document.builder().text("测试").build();

        List<Document> documents = List.of(doc);

        // 大模型响应内容
        String response = "测试";

        // 创建评估
        EvaluationRequest evaluationRequest = new EvaluationRequest(documents, response);

        EvaluationResponse evaluate = factCheckingEvaluator.evaluate(evaluationRequest);

        System.out.println(evaluate);

    }

}
