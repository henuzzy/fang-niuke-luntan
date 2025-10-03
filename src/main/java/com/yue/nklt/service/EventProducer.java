package com.yue.nklt.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yue.nklt.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class EventProducer {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    public void fireEvent(Event event) {
        try {
            String json = mapper.writeValueAsString(event);
            kafkaTemplate.send(event.getTopic(), json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialize event failed", e);
        }
    }
}


