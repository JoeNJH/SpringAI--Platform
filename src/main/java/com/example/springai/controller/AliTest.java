package com.example.springai.controller;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/a")
public class AliTest {

    private final ChatClient alibabaChatClient;

    @Resource
    private SpeechSynthesisModel speechSynthesisModel;

    public static final String BAILIAN_VOICE_MODEL = "cosyvoice-v2";

    public static final String BAILIAN_VOICE_TIMBER = "longyingxiao";


    // 加入模型随意切换
    @RequestMapping(value = "/test",produces = "text/html;charset=utf-8")
    public Flux<String> chat1(){

        return alibabaChatClient.prompt().options(ChatOptions.builder().model("deepseek-v3").build())
                .user("你是谁？")
                .stream()
                .content();
    }

    // 语音合成
    @RequestMapping(value = "/voice")
    public String voice(){
        String filepath = UUID.randomUUID()+ ".mp3";

        DashScopeSpeechSynthesisOptions options = DashScopeSpeechSynthesisOptions.builder()
                .model(BAILIAN_VOICE_MODEL)
                .voice(BAILIAN_VOICE_TIMBER)
                .build();

        SpeechSynthesisResponse response = speechSynthesisModel.call(new SpeechSynthesisPrompt("你好，欢迎使用阿里百炼！", options));

        ByteBuffer audio = response.getResult().getOutput().getAudio();

        try (FileOutputStream fileOutputStream = new FileOutputStream(filepath))
        {
            fileOutputStream.write(audio.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return filepath;

    }


}
