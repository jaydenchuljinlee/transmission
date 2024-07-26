package io.iron.notification.global.external

import io.iron.notification.global.external.queue.SlackQueueServer
import kotlinx.coroutines.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/v1/queue")
@RestController
class MockController(
    private val queueServer: SlackQueueServer
) {
    @GetMapping
    fun queueServer() {
        CoroutineScope(Dispatchers.Default).launch {
            withTimeout(5000) { // 5초 동안만 실행
                val userIdCounter = 1L
                while (true) {
                    queueServer.makeRequest(userIdCounter, "message from user$userIdCounter")
                    delay(200) // 예시: 요청 간 간격을 200ms로 설정
                }
            }
        }
    }
}