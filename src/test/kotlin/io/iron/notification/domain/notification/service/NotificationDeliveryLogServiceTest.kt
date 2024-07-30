package io.iron.notification.domain.notification.service

import io.iron.notification.domain.notification.domain.NotificationDeliveryLog
import io.iron.notification.domain.notification.domain.enumeration.NotificationStatus
import io.iron.notification.domain.notification.repository.jpa.NotificationDeliveryLogJpaRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class NotificationDeliveryLogServiceTest {
    @Mock
    private lateinit var failedNotificationRepository: NotificationDeliveryLogJpaRepository

    private lateinit var notificationDeliveryLogService: NotificationDeliveryLogService

    @BeforeEach
    fun setUP() {
        notificationDeliveryLogService = NotificationDeliveryLogService(failedNotificationRepository)
    }

    @Nested
    @DisplayName("send 메서드 실행 시")
    internal inner class SendTest {
        @Test
        fun `test save method`() {
            val notificationLog = NotificationDeliveryLog(
                userId = 1L,
                message = "Test message",
                channel = "Test channel",
                platform = "Test platform",
                batchId = UUID.randomUUID(),
                status = NotificationStatus.PENDING
            )

            `when`(failedNotificationRepository.save(notificationLog)).thenReturn(notificationLog)

            notificationDeliveryLogService.save(notificationLog)

            verify(failedNotificationRepository, times(1)).save(notificationLog)
        }
    }
}