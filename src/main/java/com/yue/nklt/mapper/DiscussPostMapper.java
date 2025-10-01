package com.yue.nklt.mapper;

import com.yue.nklt.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子Mapper接口
 */
@Mapper
public interface DiscussPostMapper {
    /**
     * 分页查询帖子列表
     */
    List<DiscussPost> selectPosts(@Param("offset") Integer offset, 
                                   @Param("limit") Integer limit,
                                   @Param("orderMode") String orderMode);

    /**
     * 查询帖子总数
     */
    int selectPostCount();

    /**
     * 根据id查询帖子
     */
    DiscussPost selectPostById(Integer id);

    /**
     * 插入帖子
     */
    int insertPost(DiscussPost post);

    /**
     * 更新帖子评论数量
     */
    int updateCommentCount(@Param("id") Integer id, @Param("commentCount") Integer commentCount);

    /**
     * 更新帖子类型
     */
    int updateType(@Param("id") Integer id, @Param("type") Integer type);

    /**
     * 更新帖子状态
     */
    int updateStatus(@Param("id") Integer id, @Param("status") Integer status);
}

