package com.yue.nklt.controller;

import com.yue.nklt.dto.*;
import com.yue.nklt.entity.Comment;
import com.yue.nklt.entity.DiscussPost;
import com.yue.nklt.entity.User;
import com.yue.nklt.service.CommentService;
import com.yue.nklt.service.DiscussPostService;
import com.yue.nklt.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 帖子控制器
 */
@RestController
@RequestMapping("/api/posts")
public class DiscussPostController {

    @Autowired
    private DiscussPostService discussPostService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    /**
     * 分页查询帖子列表
     */
    @GetMapping
    public Result getPosts(@RequestParam(defaultValue = "1") Integer page,
                          @RequestParam(defaultValue = "10") Integer pageSize,
                          @RequestParam(defaultValue = "latest") String order) {
        PageDTO<com.yue.nklt.dto.PostVO> pageDTO = discussPostService.findDiscussPosts(page, pageSize, order);
        return Result.ok(pageDTO);
    }

    /**
     * 发布帖子
     */
    @PostMapping
    public Result publishPost(@Valid @RequestBody PostPublishDTO publishDTO,
                             BindingResult bindingResult,
                             @RequestAttribute("userId") Integer userId) {
        // 参数校验
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getFieldError().getDefaultMessage());
        }

        DiscussPost post = new DiscussPost();
        post.setUserId(String.valueOf(userId));
        post.setTitle(publishDTO.getTitle());
        post.setContent(publishDTO.getContent());

        discussPostService.addDiscussPost(post);

        return Result.ok("发布成功", null);
    }

    /**
     * 查询帖子详情
     */
    @GetMapping("/{postId}")
    public Result getPostDetail(@PathVariable Integer postId) {
        // 查询帖子
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null) {
            return Result.fail("帖子不存在");
        }

        // 查询作者
        User author = null;
        try {
            Integer userId = Integer.parseInt(post.getUserId());
            author = userService.findUserById(userId);
        } catch (NumberFormatException e) {
            // 忽略
        }

        // 查询评论列表
        List<Comment> comments = commentService.findCommentsByEntity(1, postId, 0, 100);
        List<Map<String, Object>> commentList = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("id", comment.getId());
            commentMap.put("userId", comment.getUserId());
            User commentUser = userService.findUserById(comment.getUserId());
            commentMap.put("username", commentUser.getUsername());
            commentMap.put("content", comment.getContent());
            commentMap.put("createTime", comment.getCreateTime());
            commentMap.put("likeCount", 0); // TODO: 点赞功能

            // 查询回复列表
            List<Comment> replies = commentService.findCommentsByEntity(2, comment.getId(), 0, 100);
            List<Map<String, Object>> replyList = new ArrayList<>();
            for (Comment reply : replies) {
                Map<String, Object> replyMap = new HashMap<>();
                replyMap.put("id", reply.getId());
                replyMap.put("userId", reply.getUserId());
                User replyUser = userService.findUserById(reply.getUserId());
                replyMap.put("username", replyUser.getUsername());
                replyMap.put("content", reply.getContent());
                replyMap.put("createTime", reply.getCreateTime());
                replyMap.put("likeCount", 0); // TODO: 点赞功能
                replyList.add(replyMap);
            }
            commentMap.put("replies", replyList);

            commentList.add(commentMap);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("post", post);
        data.put("author", author);
        data.put("comments", commentList);

        return Result.ok(data);
    }

    /**
     * 点赞帖子
     */
    @PostMapping("/{postId}/like")
    public Result likePost(@PathVariable Integer postId) {
        // TODO: 实现点赞功能
        Map<String, Object> data = new HashMap<>();
        data.put("likeCount", 12);
        data.put("likeStatus", 1);
        return Result.ok(data);
    }

    /**
     * 评论帖子
     */
    @PostMapping("/{postId}/comments")
    public Result commentPost(@PathVariable Integer postId,
                             @Valid @RequestBody CommentPublishDTO commentDTO,
                             BindingResult bindingResult,
                             @RequestAttribute("userId") Integer userId) {
        // 参数校验
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getFieldError().getDefaultMessage());
        }

        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setEntityType(1); // 帖子
        comment.setEntityId(postId);
        comment.setContent(commentDTO.getContent());

        commentService.addComment(comment);

        Map<String, Object> data = new HashMap<>();
        data.put("commentId", comment.getId());
        data.put("content", comment.getContent());
        data.put("createTime", comment.getCreateTime());

        return Result.ok("评论发布成功", data);
    }
}

