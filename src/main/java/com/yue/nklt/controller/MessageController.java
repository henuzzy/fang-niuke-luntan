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
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /**
     * 查询私信列表
     */
    @GetMapping
    public Result getMessages(@RequestAttribute("userId") Integer userId) {
        List<Message> conversations = messageService.findConversations(userId, 0, 20);
        return Result.ok(conversations);
    }
}

