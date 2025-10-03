package com.yue.nklt.service;

import com.yue.nklt.entity.Message;
import com.yue.nklt.entity.User;
import com.yue.nklt.mapper.MessageMapper;
import com.yue.nklt.mapper.UserMapper;
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

    @Autowired
    private UserMapper userMapper;

    /**
     * 查询会话列表
     */
    public List<Message> findConversations(Integer userId, int offset, int limit) {
        return messageMapper.selectConversations(userId, offset, limit);
    }

    public List<Message> attachPeerInfo(Integer userId, List<Message> conversations) {
        if (conversations == null || conversations.isEmpty()) return conversations;
        for (Message m : conversations) {
            if (m.getConversationId() == null) continue;
            String[] parts = m.getConversationId().split("_");
            if (parts.length != 2) continue;
            int id1 = Integer.parseInt(parts[0]);
            int id2 = Integer.parseInt(parts[1]);
            int peerId = (userId.equals(id1) ? id2 : id1);
            User peer = userMapper.selectById(peerId);
            // 复用字段：临时把content前面不变，展示时前端读取 peerUsername/headerUrl
            // 这里不改变实体，改为在Controller里组装VO更规范，先直接返回Message列表，前端将用额外接口获取用户名
        }
        return conversations;
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

