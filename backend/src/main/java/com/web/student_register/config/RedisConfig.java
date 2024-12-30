package com.web.student_register.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory){
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        //Configure the key serialization to convert the key into readable String
        template.setKeySerializer(new StringRedisSerializer());
        //Configure the value serialization to convert the object into JSON Format
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        //Configure the hashKey serialization to convert the key into readable String
        template.setHashKeySerializer(new StringRedisSerializer());
        //Configure the hashValue serialization to convert the object into JSON Format
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return template;



    }
}
