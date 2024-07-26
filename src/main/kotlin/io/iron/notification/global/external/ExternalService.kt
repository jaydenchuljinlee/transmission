package io.iron.notification.global.external

interface ExternalService {
    fun send(userId: Long, message: String)
}