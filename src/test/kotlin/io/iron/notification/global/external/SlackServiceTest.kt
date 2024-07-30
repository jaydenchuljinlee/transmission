package io.iron.notification.global.external

import io.iron.notification.global.config.properties.SlackProperties
import io.iron.notification.global.external.exception.SlackTimeoutException
import io.iron.notification.global.external.exception.SlackTooManyRequestsException
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpHeaders
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class SlackServiceTest {

    private lateinit var slackProperties: SlackProperties

    private lateinit var slackService: SlackService

    @BeforeEach
    fun setUp() {
        slackProperties = SlackProperties().apply {
            maxRequestsPerSecond = 10
            timeout = 5000L
            host = "https://slack.com/api/chat.postMessage"
        }
        slackService = SlackService(slackProperties)
    }

    @Nested
    @DisplayName("send 메서드 실행 시")
    inner class SendSlackRequest {
        @Test
        @DisplayName("성공: 최대 사용치를 넘지 않고 요청에 성공한다")
        fun success() {
            val url = "http://example.com"
            val headers = HttpHeaders()
            val body = "Test Body"

            assertDoesNotThrow {
                runBlocking {
                    for (i in 1..10) {
                        slackService.send(url, headers, body)
                    }
                }
            }
        }

        @Test
        @DisplayName("실패: 최대 사용치를 넘어서 SlackTooManyRequestsException이 발생한다")
        fun failed() {
            val url = "http://example.com"
            val headers = HttpHeaders()
            val body = "Test Body"

            runBlocking {
                assertThrows<SlackTooManyRequestsException> {
                    for (i in 1..11) {
                        slackService.send(url, headers, body)
                    }
                }
            }
        }

        @Test
        @DisplayName("예외 발생: 요청 타임아웃")
        fun timeoutException() {
            val url = "http://example.com"
            val headers = HttpHeaders()
            val body = "Test Body"

            slackProperties.timeout = 0L

            runBlocking {
                assertThrows<SlackTimeoutException> {
                    slackService.send(url, headers, body)
                }
            }
        }
    }
}