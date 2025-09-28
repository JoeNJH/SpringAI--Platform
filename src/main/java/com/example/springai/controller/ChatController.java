package com.example.springai.controller;

import com.example.springai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.content.Media;
import org.springframework.util.MimeType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai")
public class ChatController {

    private final ChatClient chatClient;

    private final ChatHistoryRepository chatHistoryRepository;

    @RequestMapping(value = "/chat",produces = "text/html;charset=utf-8")
    public Flux<String> chat(@RequestParam("prompt") String prompt,
                             @RequestParam("chatId") String chatId,
                             @RequestParam("files") List<MultipartFile> files){

        // 1 存储会话ID
        chatHistoryRepository.save("chat",chatId);

        if (files == null || files.isEmpty() ){
            // 没有文件上传 普通对话
            return textChat(prompt,chatId);
        }else {
            return fileChat(prompt,chatId,files);
        }
    }

    private Flux<String> fileChat(String prompt, String chatId, List<MultipartFile> files) {

        // 转为媒体
        List<Media> mediaList = files.stream()
                .map(file
                        -> new Media(MimeType.valueOf(Objects.requireNonNull(file.getContentType())),
                        file.getResource())).toList();


        return chatClient.prompt()
              // 多媒体+提示词处理
                .user(p -> p.text(prompt).media(mediaList.toArray(Media[]::new)))
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,chatId))
                .stream()
                .content();

    }

    private Flux<String> textChat(String prompt, String chatId) {

        return chatClient.prompt()
                .user(prompt)
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,chatId))
                .stream()
                .content();

    }

}
