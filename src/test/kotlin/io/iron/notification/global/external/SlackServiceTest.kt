package io.iron.notification.global.external

import io.iron.notification.global.external.exception.SlackTooManyRequestsException
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.http.HttpHeaders
import kotlin.test.Test

class SlackServiceTest {
    @Nested
    @DisplayName("send 메서드 실행 시")
    inner class SendSlackRequest {
        @Test
        @DisplayName("성공: 최대 사용치를 넘지 않고 요청에 성공한다")
        fun success() {
            val mockUpService = SlackService(maxRequestsPerSecond = 10)
            val url = "http://example.com"
            val headers = HttpHeaders()
            val body = "Test Body"

            assertDoesNotThrow {
                for (i in 1..10) {
                    mockUpService.send(url, headers, body)
                }
            }
        }

        @Test
        @DisplayName("실패: 최대 사용치를 넘어서 SlackTooManyRequestsException이 발생한다")
        fun failed() {
            val mockUpService = SlackService(maxRequestsPerSecond = 10)
            val url = "http://example.com"
            val headers = HttpHeaders()
            val body = "Test Body"

            assertThrows<SlackTooManyRequestsException> {
                for (i in 1..11) {
                    mockUpService.send(url, headers, body)
                }
            }
        }
    }
}