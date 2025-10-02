package com.example.springai.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class LocalPdfFileRepository implements FileRepository{

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean save(String chatId, Resource resource) {
        String filename = resource.getFilename();
        File target = new File(Objects.requireNonNull(filename));
        if (!target.exists()){
            try {
                Files.copy(resource.getInputStream(),target.toPath());
            } catch (IOException e) {
                log.error("Failed to save PDF resource",e);
                return  false;
            }
        }
        String path = target.getAbsolutePath();
        redisTemplate.opsForHash().put("chat-pdf",chatId,path);
        return true;
    }

    @Override
    public Resource getFile(String chatId) {
        Object path = redisTemplate.opsForHash().get("chat-pdf", chatId);
        String filePath = Objects.requireNonNull(path,"No file found for chatId"+chatId).toString();
        return new FileSystemResource(filePath);
    }

}
