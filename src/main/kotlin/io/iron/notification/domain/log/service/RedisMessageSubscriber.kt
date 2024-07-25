package io.iron.notification.domain.log.service

import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Service

@Service
class RedisMessageSubscriber : MessageListener {

    override fun onMessage(message: Message, pattern: ByteArray?) {
        println("Received message: ${String(message.body)}")
    }
}