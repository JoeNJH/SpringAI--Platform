package com.example.springai.controller;

import com.alibaba.cloud.ai.dashscope.audio.DashScopeSpeechSynthesisOptions;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisModel;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisPrompt;
import com.alibaba.cloud.ai.dashscope.audio.synthesis.SpeechSynthesisResponse;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AliApiController {

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

    // 阿里百炼语音合成

    public byte[] TextToSpeechBytes(String prompt) {
        DashScopeSpeechSynthesisOptions options = DashScopeSpeechSynthesisOptions.builder()
                .model(BAILIAN_VOICE_MODEL)
                .voice(BAILIAN_VOICE_TIMBER)
                .build();

        SpeechSynthesisResponse resp =
                speechSynthesisModel.call(new SpeechSynthesisPrompt(prompt, options));

        ByteBuffer audio = resp.getResult().getOutput().getAudio();
        return audio.array(); // 直接返回二进制
    }

    @RequestMapping(value="/tts-bytes", consumes="text/plain", produces="audio/mpeg")
    public @ResponseBody byte[] ttsReturnBytes(@RequestBody String text) {
        return TextToSpeechBytes(text);
    }


}
