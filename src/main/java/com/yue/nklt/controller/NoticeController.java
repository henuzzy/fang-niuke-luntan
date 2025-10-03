package com.yue.nklt.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yue.nklt.entity.Message;
import com.yue.nklt.dto.Result;
import com.yue.nklt.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/notices")
public class NoticeController {

    @Autowired
    private MessageService messageService;

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 系统通知概览：评论、点赞、关注三卡片，返回最新一条内容与未读/总数
     */
    @GetMapping("/summary")
    public Result getSummary(@RequestAttribute("userId") Integer userId) throws Exception {
        Map<String, Object> data = new HashMap<>();
        for (String topic : List.of("comment", "like", "follow")) {
            Map<String, Object> card = new HashMap<>();
            List<Message> list = messageService.findLetters(topic, 0, 1);
            int total = messageService.findLetterCount(topic);
            int unread = messageService.findLetterUnreadCount(userId, topic);
            card.put("total", total);
            card.put("unread", unread);
            if (!list.isEmpty()) {
                card.put("latest", mapper.readValue(list.get(0).getContent(), Map.class));
                card.put("time", list.get(0).getCreateTime());
            }
            data.put(topic, card);
        }
        return Result.ok(data);
    }

    /**
     * 某话题的通知列表
     */
    @GetMapping("/{topic}")
    public Result getTopic(@PathVariable String topic,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer pageSize,
                           @RequestAttribute("userId") Integer userId) throws Exception {
        int offset = (page - 1) * pageSize;
        List<Message> list = messageService.findLetters(topic, offset, pageSize);
        List<Map<String, Object>> items = new ArrayList<>();
        for (Message m : list) {
            Map content = mapper.readValue(m.getContent(), Map.class);
            Map<String, Object> vo = new HashMap<>();
            vo.put("content", content);
            vo.put("time", m.getCreateTime());
            items.add(vo);
        }
        int total = messageService.findLetterCount(topic);
        Map<String, Object> data = new HashMap<>();
        data.put("items", items);
        data.put("total", total);
        return Result.ok(data);
    }
}


