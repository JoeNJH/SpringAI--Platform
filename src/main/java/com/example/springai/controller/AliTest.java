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

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AliTest {

    private final ChatClient alibabaChatClient;

    @Resource
    private SpeechSynthesisModel speechSynthesisModel;

    public static final String BAILIAN_VOICE_MODEL = "cosyvoice-v2";

    public static final String BAILIAN_VOICE_TIMBER = "longyingxiao";


    // 加入模型随意切换
    @RequestMapping(value = "/test",produces = "text/html;charset=utf-8")
    public Flux<String> chat1(){

        // 用于累积整段回复（线程安全这里用 AtomicReference<StringBuilder> 简单处理）
        AtomicReference<StringBuilder> bufferRef = new AtomicReference<>(new StringBuilder(2048));


        return alibabaChatClient.prompt().options(ChatOptions.builder().model("deepseek-v3").build())
                .user("你是谁？")
                .stream()
                .content()
                .doOnNext(chunk -> bufferRef.get().append(chunk))
                .doOnComplete(() -> {
                    StringBuilder buffer = bufferRef.get();
                    String result = buffer.toString();
                    System.out.println(result);
                    String textToSpeech = TextToSpeech(result);
                    System.out.println(textToSpeech);
                });
    }

    // 语音合成
    public String TextToSpeech(String prompt){
        String filepath = "Voice/"+UUID.randomUUID()+ ".mp3";

        DashScopeSpeechSynthesisOptions options = DashScopeSpeechSynthesisOptions.builder()
                .model(BAILIAN_VOICE_MODEL)
                .voice(BAILIAN_VOICE_TIMBER)
                .build();

        SpeechSynthesisResponse response = speechSynthesisModel.call(new SpeechSynthesisPrompt(prompt, options));

        // 获取到音频数据
        ByteBuffer audio = response.getResult().getOutput().getAudio();

        try (FileOutputStream fileOutputStream = new FileOutputStream(filepath))
        {
            fileOutputStream.write(audio.array());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return filepath;
    }

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

    @PostMapping(value="/tts-bytes", consumes="text/plain", produces="audio/mpeg")
    public @ResponseBody byte[] ttsReturnBytes(@RequestBody String text) {
        return TextToSpeechBytes(text);
    }


}
