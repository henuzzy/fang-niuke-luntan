package com.yue.nklt.service;

import com.yue.nklt.entity.Comment;
import com.yue.nklt.mapper.CommentMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 评论服务层
 */
@Service
public class CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private DiscussPostService discussPostService;

    /**
     * 根据实体查询评论列表
     */
    public List<Comment> findCommentsByEntity(Integer entityType, Integer entityId, int offset, int limit) {
        return commentMapper.selectCommentsByEntity(entityType, entityId, offset, limit);
    }

    /**
     * 根据实体查询评论数量
     */
    public int findCommentCount(Integer entityType, Integer entityId) {
        return commentMapper.selectCountByEntity(entityType, entityId);
    }

    /**
     * 添加评论
     */
    @Transactional
    public int addComment(Comment comment) {
        if (comment == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        comment.setStatus(0);
        comment.setCreateTime(new Date());
        int rows = commentMapper.insertComment(comment);

        // 更新帖子评论数量
        if (comment.getEntityType() == 1) {
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(), count);
        }

        return rows;
    }

    /**
     * 根据id查询评论
     */
    public Comment findCommentById(Integer id) {
        return commentMapper.selectCommentById(id);
    }
}

