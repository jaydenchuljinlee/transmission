package io.iron.notification.global.external.queue

import io.iron.notification.domain.notification.domain.NotificationDeliveryLog
import io.iron.notification.domain.notification.domain.enumeration.NotificationStatus
import io.iron.notification.domain.notification.service.NotificationDeliveryLogService
import io.iron.notification.domain.user.exception.UserInfoNotFoundException
import io.iron.notification.domain.user.service.UserSlackCacheService
import io.iron.notification.global.config.properties.SlackProperties
import io.iron.notification.global.external.SlackService
import io.iron.notification.global.external.request.RequestData
import io.iron.notification.global.external.exception.SlackTooManyRequestsException
import kotlinx.coroutines.*
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

@EnableScheduling
@Component
class SlackQueueServer(
    private val slackProperties: SlackProperties,
    private val userSlackCacheService: UserSlackCacheService,
    private val slackService: SlackService,
    private val notificationDeliveryLogService: NotificationDeliveryLogService
) {
    private val queue = LinkedBlockingQueue<RequestData>()
    private val scheduler = Executors.newScheduledThreadPool(1)

    init {
        scheduler.scheduleAtFixedRate({
            val tasks = mutableListOf<RequestData>()
            queue.drainTo(tasks, slackProperties.maxRequestsPerSecond+1) // 큐에서 최대 maxRequestsPerSecond 만큼의 작업을 가져온다
            tasks.forEach { task ->
                CoroutineScope(Dispatchers.IO).launch {
                    processRequest(task)
                }
            }
        }, 0, 1000L, TimeUnit.MILLISECONDS)  // 1초(1000밀리초)마다 실행되도록 설정
    }

    fun makeRequest(userId: Long, message: String, uuid: UUID) {
        val requestData = RequestData(userId, message, uuid)
        queue.offer(requestData)
    }

    private suspend fun processRequest(requestData: RequestData) {
        var delayTime = slackProperties.initialDelay
        try {
            val optionalUser = userSlackCacheService.get(requestData.userId)

            if (optionalUser.isEmpty) throw UserInfoNotFoundException("사용자 정보가 존재하지 않습니다.")

            val userInfo = optionalUser.get()

            val token = userInfo.accessToken
            val tokenType = userInfo.tokenType
            val message = requestData.message

            val headers = HttpHeaders()
            headers["authorization"] = "$tokenType $token"
            headers.contentType = MediaType.APPLICATION_JSON

            val body = mapOf(
                "channel" to userInfo.channel,
                "text" to message
            )

            // 외부 장애 발생 시, 기록
            val notificationDeliveryLog = NotificationDeliveryLog(
                userId = userInfo.id,
                message = message,
                channel = userInfo.channel,
                platform = "slack",
                batchId = requestData.uuid,
                status = NotificationStatus.PENDING
            )

            try {
                slackService.send(slackProperties.host, headers, body)
                notificationDeliveryLog.status = NotificationStatus.SUCCESS
            } catch (e: SlackTooManyRequestsException) {
                notificationDeliveryLog.status = NotificationStatus.FAILED
            } finally {
                // 전송 상태 기록, H2 DB 저장에 대한 블로킹 처리에서 메인 스레드를 블록하지 않기 위함
                withContext(Dispatchers.IO) {
                    notificationDeliveryLogService.save(notificationDeliveryLog)
                }
            }

        } catch (e: Exception) {
            println("Request failed for userId: ${requestData.userId} with message: ${requestData.message}, retrying in $delayTime ms")

            delay(delayTime)
            delayTime *= 2 // Exponential backoff
        }
    }
}