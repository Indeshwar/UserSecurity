package com.web.student_register.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.web.student_register.Dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisService {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    public <T> T get(String key, Class<T> entityClass)  {
        try{
            Object o = redisTemplate.opsForValue().get(key);
            //Convert object into string and map to entityClass
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(o.toString(), entityClass);
        }catch(Exception e){
            log.error("Exception", e);
        }
        return null;
    }

    public void set(String key, Object value, Long ttl ){
        redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
    }
}
