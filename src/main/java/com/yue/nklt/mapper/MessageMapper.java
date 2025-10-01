package com.yue.nklt.mapper;

import com.yue.nklt.entity.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 私信Mapper接口
 */
@Mapper
public interface MessageMapper {
    /**
     * 查询会话列表（针对每个会话只返回最新的一条私信）
     */
    List<Message> selectConversations(@Param("userId") Integer userId,
                                      @Param("offset") Integer offset,
                                      @Param("limit") Integer limit);

    /**
     * 查询会话数量
     */
    int selectConversationCount(Integer userId);

    /**
     * 查询某个会话包含的私信列表
     */
    List<Message> selectLetters(@Param("conversationId") String conversationId,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);

    /**
     * 查询某个会话包含的私信数量
     */
    int selectLetterCount(String conversationId);

    /**
     * 查询未读私信数量
     */
    int selectLetterUnreadCount(@Param("userId") Integer userId, @Param("conversationId") String conversationId);

    /**
     * 插入私信
     */
    int insertMessage(Message message);

    /**
     * 更新私信状态
     */
    int updateStatus(@Param("ids") List<Integer> ids, @Param("status") Integer status);
}

