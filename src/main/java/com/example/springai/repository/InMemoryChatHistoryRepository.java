package com.example.springai.repository;

import com.example.springai.entity.po.UserChatindex;
import com.example.springai.mapper.UserChatindexMapper;
import com.example.springai.service.IUserChatindexService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class InMemoryChatHistoryRepository implements ChatHistoryRepository{

    private final IUserChatindexService userChatindexService;

    private final UserChatindexMapper userChatindexMapper;


    @Override
    public void save(String type, String chatId) {

        if(userChatindexMapper.selectById(chatId) != null) {
            return;
        }
        UserChatindex userChatindex = new UserChatindex();
        userChatindex.setConversationId(chatId);
        userChatindex.setType(type);
        userChatindex.setUid(1);    // 固定的UserId
        userChatindexService.save(userChatindex);
    }

    @Override
    public List<String> getChatIds(String type) {

         return userChatindexService.lambdaQuery().select(UserChatindex::getConversationId)
                .eq(UserChatindex::getType, type).list()
                 .stream().map(UserChatindex::getConversationId).toList();
    }

    @Override
    public List<String> getAgentChatIds(String agentId) {

        return userChatindexService.lambdaQuery().select(UserChatindex::getConversationId)
                .eq(UserChatindex::getAgentId, agentId).list()
                .stream().map(UserChatindex::getConversationId).toList();
    }

    @Override
    public void saveAgent(String chatId, String agentId) {
        if(userChatindexMapper.selectById(chatId) != null) {
            return;
        }
        UserChatindex userChatindex = new UserChatindex();
        userChatindex.setConversationId(chatId);
        userChatindex.setAgentId(agentId);
        userChatindex.setType("agent");
        userChatindex.setUid(1);    // 固定的UserId
        userChatindexService.save(userChatindex);
    }
}
