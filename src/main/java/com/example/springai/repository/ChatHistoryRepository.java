package com.example.springai.repository;

import java.util.List;

public interface ChatHistoryRepository {

    //  按照类型存储会话ID
    void save(String type, String chatId);

    //  按照类型查询会话ID
    List<String> getChatIds(String type);

    // 按照agnet对象查询会话ID
    List<String> getAgentChatIds(String agentId);
}
