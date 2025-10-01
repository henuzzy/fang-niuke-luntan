package com.yue.nklt.service;

import com.yue.nklt.dto.PageDTO;
import com.yue.nklt.dto.PostVO;
import com.yue.nklt.entity.DiscussPost;
import com.yue.nklt.entity.User;
import com.yue.nklt.mapper.DiscussPostMapper;
import com.yue.nklt.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 帖子服务层
 */
@Service
public class DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 分页查询帖子列表（包含用户信息）
     */
    public PageDTO<PostVO> findDiscussPosts(int page, int pageSize, String order) {
        int offset = (page - 1) * pageSize;
        List<DiscussPost> postList = discussPostMapper.selectPosts(offset, pageSize, order);
        int total = discussPostMapper.selectPostCount();
        int pages = (int) Math.ceil(total * 1.0 / pageSize);

        // 组装PostVO，包含用户信息
        List<PostVO> voList = new ArrayList<>();
        for (DiscussPost post : postList) {
            PostVO vo = new PostVO();
            BeanUtils.copyProperties(post, vo);
            
            // 查询用户信息
            if (post.getUserId() != null && !post.getUserId().isEmpty()) {
                try {
                    Integer userId = Integer.parseInt(post.getUserId());
                    User user = userMapper.selectById(userId);
                    if (user != null) {
                        vo.setUsername(user.getUsername());
                    }
                } catch (NumberFormatException e) {
                    // 忽略非数字的userId
                }
            }
            
            voList.add(vo);
        }

        return new PageDTO<>((long) total, pages, page, voList);
    }

    /**
     * 根据id查询帖子
     */
    public DiscussPost findDiscussPostById(Integer id) {
        return discussPostMapper.selectPostById(id);
    }

    /**
     * 发布帖子
     */
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }

        post.setType(0);
        post.setStatus(0);
        post.setCreateTime(new Date());
        post.setCommentCount(0);
        post.setScore(0.0);

        return discussPostMapper.insertPost(post);
    }

    /**
     * 更新帖子评论数量
     */
    public int updateCommentCount(Integer id, Integer commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }
}

