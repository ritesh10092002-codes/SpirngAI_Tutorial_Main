package com.Ritesh.AI_Powered_Interview_Preparation_Assistant.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.redis.RedisChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import redis.clients.jedis.RedisClient;

@Configuration
public class ChatMemoryConfig {
    @Bean
    public RedisChatMemoryRepository redisChatMemoryRepository(
            RedisClient redisClient) {

        return RedisChatMemoryRepository.builder()
                .jedisClient(redisClient)
                .build();
    }
    @Bean
    public ChatMemory chatMemory(RedisChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();
    }
}
