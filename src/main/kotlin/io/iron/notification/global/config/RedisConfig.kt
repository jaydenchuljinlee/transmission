package io.iron.notification.global.config

import io.iron.notification.domain.log.service.RedisMessageSubscriber
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.GenericToStringSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    @Value("\${spring.data.redis.host}")
    private lateinit var host: String

    @Value("\${spring.data.redis.port}")
    private lateinit var port: String

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory(host, port.toInt())
    }

    @Bean
    fun stringToLongRedisTemplate(): RedisTemplate<String, Long> {
        val redisTemplate = RedisTemplate<String, Long>()
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = GenericToStringSerializer(Long::class.java)
        redisTemplate.connectionFactory = redisConnectionFactory()
        return redisTemplate
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, Any> {
        val template = RedisTemplate<String, Any>()
        template.connectionFactory = redisConnectionFactory()
        template.keySerializer = StringRedisSerializer()
        template.valueSerializer = GenericJackson2JsonRedisSerializer()
        return template
    }

    @Bean
    fun listOperations(
        redisStringTemplate: RedisTemplate<String?, String?>
    ): ListOperations<String?, String?> {
        return redisStringTemplate.opsForList()
    }

    @Bean
    fun messageListenerAdapter(listener: RedisMessageSubscriber): MessageListenerAdapter {
        return MessageListenerAdapter(listener)
    }

    @Bean
    fun redisMessageListenerContainer(connectionFactory: RedisConnectionFactory, listenerAdapter: MessageListenerAdapter): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(connectionFactory)
        container.addMessageListener(listenerAdapter, ChannelTopic("exceptionLogsQueue"))
        return container
    }

    @Bean
    fun topic(): ChannelTopic {
        return ChannelTopic("exceptionLogsQueue")
    }
}