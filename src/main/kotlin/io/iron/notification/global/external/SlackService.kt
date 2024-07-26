package io.iron.notification.global.external

import org.springframework.stereotype.Service

@Service
class SlackService: ExternalService {
    override fun send(userId: Long, message: String) {
        TODO("Not yet implemented")
    }
}