package com.Ritesh.AI_Powered_Interview_Preparation_Assistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.repository.redis.RedisChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class AIConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder){
        return builder.build();
    }
    //not needed if the compiler say no redis chat memory
//    @Bean
//    public RedisChatMemoryRepository redisChatMemoryRepository(
//            RedisTemplate<String, Object> redisTemplate) {
//
//        return RedisChatMemoryRepository.builder()
//                .redisTemplate(redisTemplate)
//                .build();
//    }
}
