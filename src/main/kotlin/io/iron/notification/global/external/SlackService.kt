package io.iron.notification.global.external

import io.iron.notification.global.config.properties.SlackProperties
import io.iron.notification.global.external.exception.SlackTimeoutException
import io.iron.notification.global.external.exception.SlackTooManyRequestsException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Component
class SlackService(
    private val slackProperties: SlackProperties
) {
    private var requestCounts = ConcurrentHashMap<Long, AtomicInteger>()

    /**
     * 요청을 보내는 메서드
     *
     * @param url 요청할 URL
     * @param headers 요청 헤더
     * @param body 요청 바디
     * @throws TooManyRequestsException 요청이 초당 최대 요청 수를 초과했을 때 발생하는 예외
     * @throws SlackTimeoutException 요청이 초당 최대 시간을 초과했을 때 발생하는 예외
     */
    @Throws(SlackTooManyRequestsException::class)
    suspend fun <T> send(url: String, headers: HttpHeaders, body: T) {
        try {
            withTimeout(slackProperties.timeout) {
                val currentSecond = Instant.now().epochSecond
                val currentCount = requestCounts.computeIfAbsent(currentSecond) { AtomicInteger(0) }.incrementAndGet()

                if (currentCount > slackProperties.maxRequestsPerSecond) {
                    throw SlackTooManyRequestsException("초당 최대 요청 수 초과: ${slackProperties.maxRequestsPerSecond}")
                }

                // 초당 요청 수 관리를 위한 이전 시각 데이터 정리
                requestCounts.keys.removeIf { it < currentSecond }
            }
        } catch (e: TimeoutCancellationException) {
            throw SlackTimeoutException("최대 요청 시간 초과: ${slackProperties.timeout}")
        }
    }
}