package com.yue.nklt.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic topicComment() {
        return new NewTopic("comment", 1, (short) 1);
    }

    @Bean
    public NewTopic topicLike() {
        return new NewTopic("like", 1, (short) 1);
    }

    @Bean
    public NewTopic topicFollow() {
        return new NewTopic("follow", 1, (short) 1);
    }
}


