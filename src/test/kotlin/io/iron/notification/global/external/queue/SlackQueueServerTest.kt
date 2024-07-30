package io.iron.notification.global.external.queue

import io.iron.notification.domain.notification.service.NotificationDeliveryLogService
import io.iron.notification.domain.user.domain.UserSlackInfo
import io.iron.notification.domain.user.domain.enumeration.TokenType
import io.iron.notification.domain.user.service.UserSlackCacheService
import io.iron.notification.global.config.properties.SlackProperties
import io.iron.notification.global.external.SlackService
import io.iron.notification.global.external.request.RequestData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.util.ReflectionTestUtils
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import kotlin.test.Test

@ExperimentalCoroutinesApi
@ExtendWith(MockitoExtension::class)
class SlackQueueServerTest {
    @Mock
    private lateinit var userCacheService: UserSlackCacheService

    @Mock
    private lateinit var slackService: SlackService

    @Mock
    private lateinit var notificationDeliveryLogService: NotificationDeliveryLogService

    private lateinit var slackProperties: SlackProperties

    private lateinit var slackQueueServer: SlackQueueServer

    @BeforeEach
    fun setUp() {
        slackProperties = SlackProperties().apply {
            maxRequestsPerSecond = 10
            timeout = 5000L
            host = "https://slack.com/api/chat.postMessage"
        }

        slackQueueServer = SlackQueueServer(
            slackProperties = slackProperties,
            userSlackCacheService = userCacheService,
            slackService = slackService,
            notificationDeliveryLogService = notificationDeliveryLogService
        )

        // 큐를 초기화
        ReflectionTestUtils.setField(slackQueueServer, "queue", LinkedBlockingQueue<RequestData>())
    }

    @Nested
    @DisplayName("scheduler 실행 시")
    inner class SchedulerTest {

        @Test
        @DisplayName("성공: 요청이 큐에 추가된다")
        fun `should add request to queue`() {
            slackQueueServer.makeRequest(1L, "Test Message", UUID.randomUUID())

            assertEquals(1, ReflectionTestUtils.getField(slackQueueServer, "queue")?.let { (it as LinkedBlockingQueue<*>).size })
        }

        @Test
        @DisplayName("성공: 큐에서 요청을 처리한다")
        fun `scheduler should process requests from queue`() = runTest {
            val userSlackTokenInfo = UserSlackInfo(
                id = 1L,
                email = "test@example.com",
                accessToken = "test-token",
                channel = "channel1",
                tokenType = TokenType.BEARER
            )
            `when`(userCacheService.get(1L)).thenReturn(Optional.of(userSlackTokenInfo))

            slackQueueServer.makeRequest(1L, "Test Message 1", UUID.randomUUID())
            slackQueueServer.makeRequest(1L, "Test Message 2", UUID.randomUUID())

            val headers1 = HttpHeaders().apply {
                set("authorization", "${userSlackTokenInfo.tokenType} ${userSlackTokenInfo.accessToken}")
                contentType = MediaType.APPLICATION_JSON
            }
            val body1 = mapOf(
                "channel" to "channel1",
                "text" to "Test Message 1"
            )

            val headers2 = HttpHeaders().apply {
                set("authorization", "${userSlackTokenInfo.tokenType} ${userSlackTokenInfo.accessToken}")
                contentType = MediaType.APPLICATION_JSON
            }
            val body2 = mapOf(
                "channel" to "channel1",
                "text" to "Test Message 2"
            )

            withContext(Dispatchers.Default) {
                delay(2000L) // 실제 지연 시간
            }

            // 비동기 작업이 완료될 때까지 기다리기
            advanceTimeBy(2000L) // 이 코드가 비동기 작업을 기다리도록 합니다.

            verify(slackService).send(slackProperties.host, headers1, body1)
            verify(slackService).send(slackProperties.host, headers2, body2)
        }

        @Test
        @DisplayName("성공: 초과된 요청 수에 대해 예외를 던지지 않는다")
        fun `should throw exception when maxRequestsPerSecond is exceeded`() = runTest {
            val uuid = UUID.randomUUID()

            val userSlackTokenInfo = UserSlackInfo(
                id = 1L,
                email = "test@example.com",
                accessToken = "test-token",
                channel = "channel1",
                tokenType = TokenType.BEARER
            )

            // 초당 10개의 요청을 추가
            for (i in 1..10) {
                slackQueueServer.makeRequest(userSlackTokenInfo.id, "Test Message $i", uuid)
            }

            // 추가 요청을 10개 더 추가
            for (i in 11..20) {
                slackQueueServer.makeRequest(userSlackTokenInfo.id, "Test Message $i", uuid)
            }

            // 비동기 작업이 완료될 때까지 기다리기
            advanceTimeBy(2000L) // 이 코드가 비동기 작업을 기다리도록 합니다.

        }
    }
}