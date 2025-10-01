package com.yue.nklt.service;

import com.yue.nklt.entity.Message;
import com.yue.nklt.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 私信服务层
 */
@Service
public class MessageService {

    @Autowired
    private MessageMapper messageMapper;

    /**
     * 查询会话列表
     */
    public List<Message> findConversations(Integer userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    /**
     * 查询会话数量
     */
    public int findConversationCount(Integer userId) {
        return messageMapper.selectConversationCount(userId);
    }

    /**
     * 查询私信列表
     */
    public List<Message> findLetters(String conversationId, int offset, int limit) {
        return messageMapper.selectLetters(conversationId, offset, limit);
    }

    /**
     * 查询私信数量
     */
    public int findLetterCount(String conversationId) {
        return messageMapper.selectLetterCount(conversationId);
    }

    /**
     * 查询未读私信数量
     */
    public int findLetterUnreadCount(Integer userId, String conversationId) {
        return messageMapper.selectLetterUnreadCount(userId, conversationId);
    }

    /**
     * 发送私信
     */
    public int addMessage(Message message) {
        return messageMapper.insertMessage(message);
    }

    /**
     * 更新消息状态
     */
    public int readMessage(List<Integer> ids) {
        return messageMapper.updateStatus(ids, 1);
    }
}

