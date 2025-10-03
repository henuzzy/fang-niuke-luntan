package com.yue.nklt.controller;

import com.yue.nklt.dto.Result;
import com.yue.nklt.entity.Message;
import com.yue.nklt.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 私信控制器
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 查询私信会话列表
     */
    @GetMapping
    public Result getMessages(@RequestParam Integer userId,
                              @RequestParam(defaultValue = "1") Integer page,
                              @RequestParam(defaultValue = "20") Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Message> conversations = messageService.findConversations(userId, offset, pageSize);
        return Result.ok(conversations);
    }

    /**
     * 查询某一会话的私信列表
     */
    @GetMapping("/letters")
    public Result getLetters(@RequestParam String conversationId,
                             @RequestParam(defaultValue = "1") Integer page,
                             @RequestParam(defaultValue = "20") Integer pageSize) {
        int offset = (page - 1) * pageSize;
        List<Message> letters = messageService.findLetters(conversationId, offset, pageSize);
        return Result.ok(letters);
    }

    /**
     * 发送私信
     */
    @PostMapping
    public Result sendMessage(@RequestParam Integer fromId,
                              @RequestParam Integer toId,
                              @RequestParam String content) {
        Message m = new Message();
        m.setFromId(fromId);
        m.setToId(toId);
        String cid = fromId < toId ? fromId + "_" + toId : toId + "_" + fromId;
        m.setConversationId(cid);
        m.setContent(content);
        m.setStatus(0);
        m.setCreateTime(new java.util.Date());
        messageService.addMessage(m);
        return Result.ok("发送成功", null);
    }
}

