package io.iron.notification.domain.log.service

import mu.KLogging
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service

@Service
class ExceptionLogProducer(
    private val redisTemplate: RedisTemplate<String, Any>,
    private val topic: ChannelTopic
) {
    companion object : KLogging()

    fun sendLogToQueue(log: String) {

        logger.info(log)

        // redisTemplate.convertAndSend(topic.topic, log)
    }
}