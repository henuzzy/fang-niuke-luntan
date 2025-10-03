package com.yue.nklt.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yue.nklt.entity.Event;
import com.yue.nklt.entity.Message;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class EventConsumer {

    @Autowired
    private MessageService messageService;

    private final ObjectMapper mapper = new ObjectMapper();

    // 系统用户ID固定为1（结合你的 init_data.sql 的 SYSTEM 记录）
    private static final int SYSTEM_USER_ID = 1;

    @KafkaListener(topics = {"comment", "like", "follow"})
    public void handleEvent(ConsumerRecord<String, String> record) throws Exception {
        if (record == null || record.value() == null) return;
        Event event = mapper.readValue(record.value(), Event.class);

        // 生成站内通知，使用会话ID为 topic
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);
        message.setToId(event.getEntityOwnerId());
        message.setConversationId(event.getTopic());

        // 内容为一个JSON，包含触发者、实体等
        Map<String, Object> content = Map.of(
                "userId", event.getUserId(),
                "entityType", event.getEntityType(),
                "entityId", event.getEntityId(),
                "data", event.getData()
        );
        message.setContent(mapper.writeValueAsString(content));
        message.setStatus(0);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
    }
}


