package io.iron.notification.global.external.queue

import io.iron.notification.domain.user.service.UserCacheService
import io.iron.notification.global.external.dto.RequestData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

@EnableScheduling
@Component
class SlackQueueServer(
    @Value("\${external.slack.maxRequestsPerSecond}") private val maxRequestsPerSecond: Int,
    @Value("\${external.slack.initialDelay}") private val initialDelay: Long,
    private val userCacheService: UserCacheService
) {
    private val queue = LinkedBlockingQueue<RequestData>()
    private val scheduler = Executors.newScheduledThreadPool(1)

    init {
        scheduler.scheduleAtFixedRate({
            val tasks = mutableListOf<RequestData>()
            queue.drainTo(tasks, maxRequestsPerSecond) // 큐에서 최대 maxRequestsPerSecond 만큼의 작업을 가져온다
            tasks.forEach { task ->
                CoroutineScope(Dispatchers.IO).launch {
                    processRequest(task)
                }
            }
        }, 0, 1000L, TimeUnit.MILLISECONDS)  // 1초(1000밀리초)마다 실행되도록 설정
    }

    fun makeRequest(userId: Long, message: String) {
        val requestData = RequestData(userId, message)
        queue.offer(requestData)
    }

    private suspend fun processRequest(requestData: RequestData) {
        var delayTime = initialDelay
        while (true) {
            try {
                val userInfo = userCacheService.get(requestData.userId)
                if (userInfo.isPresent) {
                    // Simulate API request
                    println("API request executed for userId: ${requestData.userId} with message: ${requestData.message} at ${System.currentTimeMillis()}")
                    // Here you can replace the println with the actual API call
                } else {
                    println("User not found for userId: ${requestData.userId}")
                }
                break
            } catch (e: Exception) {
                println("Request failed for userId: ${requestData.userId} with message: ${requestData.message}, retrying in $delayTime ms")
                delay(delayTime)
                delayTime *= 2 // Exponential backoff
            }
        }
    }
}