package io.iron.notification.global.external

import io.iron.notification.global.external.exception.SlackTooManyRequestsException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Component
class SlackService(
    @Value("\${external.slack.maxRequestsPerSecond}") private var maxRequestsPerSecond: Int = 10
) {
    private var requestCounts = ConcurrentHashMap<Long, AtomicInteger>()

    /**
     * 요청을 보내는 메서드
     *
     * @param url 요청할 URL
     * @param headers 요청 헤더
     * @param body 요청 바디
     * @throws TooManyRequestsException 요청이 초당 최대 요청 수를 초과했을 때 발생하는 예외
     */
    @Throws(SlackTooManyRequestsException::class)
    fun <T> send(url: String, headers: HttpHeaders, body: T) {
        val currentSecond = Instant.now().epochSecond
        val currentCount = requestCounts.computeIfAbsent(currentSecond) { AtomicInteger(0) }.incrementAndGet()

        if (currentCount > maxRequestsPerSecond) {
            throw SlackTooManyRequestsException("초당 최대 요청 수를 초과했습니다: $maxRequestsPerSecond")
        }

        // 요청 처리 로직 (예: 로깅, 실제 HTTP 요청 등)
        println("Sending request to $url with headers $headers and body $body")

        // 초당 요청 수 관리를 위한 이전 시각 데이터 정리
        requestCounts.keys.removeIf { it < currentSecond }

        // println(currentCount)
    }
}