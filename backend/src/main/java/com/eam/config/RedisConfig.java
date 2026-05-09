package com.eam.config;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Redis配置类
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * 配置RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用StringRedisSerializer作为key的序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // 使用FastJSON2作为value的序列化器
        FastJson2JsonRedisSerializer<Object> jsonSerializer = new FastJson2JsonRedisSerializer<>(Object.class);
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置CacheManager
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 配置默认的缓存配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                // 设置缓存有效期为60分钟
                .entryTtl(Duration.ofMinutes(60))
                // 使用String类型序列化key
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                // 使用JSON类型序列化value
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new FastJson2JsonRedisSerializer<>(Object.class)))
                // 不缓存null值
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .build();
    }

    /**
     * FastJSON2序列化器
     */
    public static class FastJson2JsonRedisSerializer<T> implements RedisSerializer<T> {
        private final Class<T> clazz;

        public FastJson2JsonRedisSerializer(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public byte[] serialize(T t) throws SerializationException {
            if (t == null) {
                return new byte[0];
            }
            return JSON.toJSONString(t, JSONWriter.Feature.WriteClassName).getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public T deserialize(byte[] bytes) throws SerializationException {
            if (bytes == null || bytes.length == 0) {
                return null;
            }
            String str = new String(bytes, StandardCharsets.UTF_8);
            return JSON.parseObject(str, clazz, JSONReader.Feature.SupportAutoType);
        }
    }
}
