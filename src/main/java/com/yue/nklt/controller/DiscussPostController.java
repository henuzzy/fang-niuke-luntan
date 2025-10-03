package com.yue.nklt.controller;

import com.yue.nklt.dto.*;
import com.yue.nklt.entity.Comment;
import com.yue.nklt.entity.DiscussPost;
import com.yue.nklt.entity.User;
import com.yue.nklt.service.CommentService;
import com.yue.nklt.service.DiscussPostService;
import com.yue.nklt.service.UserService;
import com.yue.nklt.service.EventProducer;
import com.yue.nklt.entity.Event;
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

    @Autowired
    private com.yue.nklt.service.LikeService likeService;

    @Autowired
    private EventProducer eventProducer;

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
    public Result getPostDetail(@PathVariable Integer postId,
                                @RequestAttribute(value = "userId", required = false) Integer currentUserId,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "10") Integer pageSize) {
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

        // 帖子点赞信息
        long likeCount = likeService.findEntityLikeCount(1, postId);
        int likeStatus = currentUserId == null ? 0 : likeService.findEntityLikeStatus(currentUserId, 1, postId);

        // 评论分页
        if (page == null || page < 1) page = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int total = commentService.findCommentCount(1, postId);
        int offset = (page - 1) * pageSize;
        List<Comment> comments = commentService.findCommentsByEntity(1, postId, offset, pageSize);
        List<Map<String, Object>> commentList = new ArrayList<>();
        for (Comment comment : comments) {
            Map<String, Object> commentMap = new HashMap<>();
            commentMap.put("id", comment.getId());
            commentMap.put("userId", comment.getUserId());
            User commentUser = userService.findUserById(comment.getUserId());
            commentMap.put("username", commentUser.getUsername());
            commentMap.put("content", comment.getContent());
            commentMap.put("createTime", comment.getCreateTime());
            long cLikeCount = likeService.findEntityLikeCount(2, comment.getId());
            int cLikeStatus = currentUserId == null ? 0 : likeService.findEntityLikeStatus(currentUserId, 2, comment.getId());
            commentMap.put("likeCount", cLikeCount);
            commentMap.put("likeStatus", cLikeStatus);

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
                long rLikeCount = likeService.findEntityLikeCount(2, reply.getId());
                int rLikeStatus = currentUserId == null ? 0 : likeService.findEntityLikeStatus(currentUserId, 2, reply.getId());
                replyMap.put("likeCount", rLikeCount);
                replyMap.put("likeStatus", rLikeStatus);
                replyList.add(replyMap);
            }
            commentMap.put("replies", replyList);

            commentList.add(commentMap);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("post", post);
        data.put("author", author);
        data.put("comments", commentList);
        data.put("likeCount", likeCount);
        data.put("likeStatus", likeStatus);
        Map<String, Object> commentPage = new HashMap<>();
        commentPage.put("page", page);
        commentPage.put("pageSize", pageSize);
        commentPage.put("total", total);
        int totalPages = (int) Math.ceil(total * 1.0 / pageSize);
        commentPage.put("totalPages", totalPages);
        data.put("commentPage", commentPage);

        return Result.ok(data);
    }

    /**
     * 点赞帖子
     */
    @PostMapping("/{postId}/like")
    public Result likePost(@PathVariable Integer postId,
                           @RequestAttribute("userId") Integer currentUserId) {
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        if (post == null) {
            return Result.fail("帖子不存在");
        }
        int ownerId = 0;
        try { ownerId = Integer.parseInt(post.getUserId()); } catch (Exception ignored) {}
        likeService.like(currentUserId, 1, postId, ownerId);
        if (ownerId > 0 && !ownerId.equals(currentUserId)) {
            Event ev = new Event();
            ev.setTopic("like");
            ev.setUserId(currentUserId);
            ev.setEntityType(1);
            ev.setEntityId(postId);
            ev.setEntityOwnerId(ownerId);
            ev.setData("postId", postId);
            eventProducer.fireEvent(ev);
        }
        Map<String, Object> data = new HashMap<>();
        data.put("likeCount", likeService.findEntityLikeCount(1, postId));
        data.put("likeStatus", likeService.findEntityLikeStatus(currentUserId, 1, postId));
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

        // 发送评论通知
        DiscussPost post = discussPostService.findDiscussPostById(postId);
        int ownerId = 0;
        try { ownerId = Integer.parseInt(post.getUserId()); } catch (Exception ignored) {}
        if (ownerId > 0 && !ownerId.equals(userId)) {
            Event ev = new Event();
            ev.setTopic("comment");
            ev.setUserId(userId);
            ev.setEntityType(1);
            ev.setEntityId(postId);
            ev.setEntityOwnerId(ownerId);
            ev.setData("postId", postId);
            eventProducer.fireEvent(ev);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("commentId", comment.getId());
        data.put("content", comment.getContent());
        data.put("createTime", comment.getCreateTime());

        return Result.ok("评论发布成功", data);
    }

    /**
     * 回复评论
     */
    @PostMapping("/{postId}/comments/{commentId}/replies")
    public Result replyComment(@PathVariable Integer postId,
                               @PathVariable Integer commentId,
                               @Valid @RequestBody ReplyPublishDTO replyDTO,
                               BindingResult bindingResult,
                               @RequestAttribute("userId") Integer userId) {
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getFieldError().getDefaultMessage());
        }
        Comment reply = new Comment();
        reply.setUserId(userId);
        reply.setEntityType(2); // 2-评论
        reply.setEntityId(commentId);
        reply.setTargetId(replyDTO.getTargetId());
        reply.setContent(replyDTO.getContent());
        commentService.addComment(reply);

        Map<String, Object> data = new HashMap<>();
        data.put("replyId", reply.getId());
        data.put("content", reply.getContent());
        data.put("createTime", reply.getCreateTime());
        return Result.ok("回复成功", data);
    }

    /**
     * 点赞评论/回复
     */
    @PostMapping("/comments/{commentId}/like")
    public Result likeComment(@PathVariable Integer commentId,
                              @RequestAttribute("userId") Integer userId) {
        Comment c = commentService.findCommentById(commentId);
        if (c == null) return Result.fail("评论不存在");
        int ownerId = c.getUserId();
        likeService.like(userId, 2, commentId, ownerId);
        Map<String, Object> data = new HashMap<>();
        data.put("likeCount", likeService.findEntityLikeCount(2, commentId));
        data.put("likeStatus", likeService.findEntityLikeStatus(userId, 2, commentId));
        return Result.ok(data);
    }
}

