package io.iron.notification.global.external.queue

import io.iron.notification.domain.notification.service.NotificationDeliveryLogService
import io.iron.notification.domain.user.service.UserSlackCacheService
import io.iron.notification.global.config.properties.SlackProperties
import io.iron.notification.global.external.SlackService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.test.util.ReflectionTestUtils
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

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
    }

    @Nested
    @DisplayName("scheduler 실행 시")
    inner class SchedulerTest {

        // @Test
        @DisplayName("성공: 요청이 큐에 추가된다")
        fun `should add request to queue`() = runTest{
            slackQueueServer.makeRequest(1L, "Test Message", UUID.randomUUID())

            advanceUntilIdle()

            assertEquals(1, ReflectionTestUtils.getField(slackQueueServer, "queue")?.let { (it as LinkedBlockingQueue<*>).size })
        }

    }
}