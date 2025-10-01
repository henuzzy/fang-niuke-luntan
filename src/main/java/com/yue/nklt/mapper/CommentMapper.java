package com.yue.nklt.mapper;

import com.yue.nklt.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论Mapper接口
 */
@Mapper
public interface CommentMapper {
    /**
     * 根据实体类型和实体id查询评论列表
     */
    List<Comment> selectCommentsByEntity(@Param("entityType") Integer entityType,
                                         @Param("entityId") Integer entityId,
                                         @Param("offset") Integer offset,
                                         @Param("limit") Integer limit);

    /**
     * 根据实体类型和实体id查询评论数量
     */
    int selectCountByEntity(@Param("entityType") Integer entityType, @Param("entityId") Integer entityId);

    /**
     * 插入评论
     */
    int insertComment(Comment comment);

    /**
     * 根据id查询评论
     */
    Comment selectCommentById(Integer id);
}

