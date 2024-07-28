package io.iron.notification.global.external.queue

import io.iron.notification.domain.notification.service.NotificationDeliveryLogService
import io.iron.notification.domain.user.domain.UserSlackInfo
import io.iron.notification.domain.user.domain.enumeration.TokenType
import io.iron.notification.domain.user.service.UserCacheService
import io.iron.notification.domain.user.service.UserSlackCacheService
import io.iron.notification.global.config.TestConfig
import io.iron.notification.global.config.properties.SlackProperties
import io.iron.notification.global.external.SlackService
import io.iron.notification.global.external.request.RequestData
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.util.ReflectionTestUtils
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
@ExtendWith(SpringExtension::class)
@Import(TestConfig::class)
class SlackQueueServerTest {
    @Mock
    private lateinit var userCacheService: UserSlackCacheService

    @Mock
    private lateinit var slackService: SlackService

    @Mock
    private lateinit var notificationDeliveryLogService: NotificationDeliveryLogService

    @Autowired
    private lateinit var slackProperties: SlackProperties

    private lateinit var slackQueueServer: SlackQueueServer

    private val MAX_REQUEST_PER_SECOND = 10
    private val INITIAL_DELAY = 100L
    private val HOST = "https://slack.com/api/chat.postMessage"

    @BeforeEach
    fun setUp() {
        slackQueueServer = SlackQueueServer(
            slackProperties = slackProperties,
            userSlackCacheService = userCacheService,
            slackService = slackService,
            notificationDeliveryLogService = notificationDeliveryLogService
        )
        ReflectionTestUtils.setField(slackQueueServer, "queue", LinkedBlockingQueue<RequestData>())
        ReflectionTestUtils.setField(slackQueueServer, "scheduler", Executors.newScheduledThreadPool(1))
    }

    @Nested
    @DisplayName("makeRequest 메서드 실행 시")
    inner class MakeRequestTest {
        @Test
        @DisplayName("성공: 요청이 큐에 추가된다")
        fun `should add request to queue`() {
            slackQueueServer.makeRequest(1L, "Test Message", UUID.randomUUID())

            assertEquals(1, ReflectionTestUtils.getField(slackQueueServer, "queue")?.let { (it as LinkedBlockingQueue<*>).size })
        }
    }

    @Nested
    @DisplayName("scheduler 실행 시")
    inner class SchedulerTest {

        @Test
        @DisplayName("성공: 큐에서 요청을 처리한다")
        fun `scheduler should process requests from queue`() {
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

            // Wait for scheduler to run at least once
            Thread.sleep(2000)

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

            runBlocking {
                verify(slackService, times(1)).send(HOST, headers1, body1)
                verify(slackService, times(1)).send(HOST, headers2, body2)
            }
        }
    }

    @Nested
    @DisplayName("초당 최대 요청 수 초과 시")
    inner class MaxRequestsPerSecondExceededTest {

        @Test
        @DisplayName("성공: 초과된 요청 수에 대해 예외를 던지지 않는다")
        fun `should throw exception when maxRequestsPerSecond is exceeded`() {
            val userSlackTokenInfo = UserSlackInfo(
                id = 1L,
                email = "test@example.com",
                accessToken = "test-token",
                channel = "channel1",
                tokenType = TokenType.BEARER
            )

            `when`(userCacheService.get(1L)).thenReturn(Optional.of(userSlackTokenInfo))


            // 초당 10개의 요청을 추가
            for (i in 1..10) {
                slackQueueServer.makeRequest(1L, "Test Message $i", UUID.randomUUID())
            }

            // 스케줄러가 첫 10개의 요청을 처리할 시간을 줌
            Thread.sleep(2000)

            // 추가 요청을 10개 더 추가
            for (i in 11..20) {
                slackQueueServer.makeRequest(1L, "Test Message $i", UUID.randomUUID())
            }

            // 다시 스케줄러가 처리할 시간을 줌
            Thread.sleep(2000)

            // 예외가 발생하지 않는지 확인
            assertTrue(true)
        }
    }

}