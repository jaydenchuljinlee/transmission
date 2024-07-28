package io.iron.notification.domain.notification.service

import io.iron.notification.domain.notification.repository.jpa.UserNotificationGroupJpaRepository
import io.iron.notification.domain.user.domain.UserInfo
import io.iron.notification.domain.user.repository.jpa.UserInfoJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class NotificationServiceTest {
    @Mock
    private lateinit var userRepository: UserInfoJpaRepository

    @Mock
    private lateinit var userNotificationGroupRepository: UserNotificationGroupJpaRepository

    @InjectMocks
    private lateinit var notificationService: NotificationService


    @Nested
    @DisplayName("sendExternalAlert 메서드 실행 시")
    inner class SendExternalAlertTest {

        @Test
        @DisplayName("성공: @all을 사용하여 모든 사용자에게 알림을 보낸다")
        fun sendAlertToAllUsers() {
            // given
            val allUsers = listOf(
                UserInfo(id = 1, nickname = "user1", email = "user1@example.com"),
                UserInfo(id = 2, nickname = "user2", email = "user2@example.com")
            )
            `when`(userRepository.findAll()).thenReturn(allUsers)

            // when
            val targetCount = notificationService.sendExternalAlert(listOf("@all"), "high", "테스트 메시지")

            // then
            // verify(exceptionLogProducer, times(1)).sendLogToQueue("Severity: high, Message: 테스트 메시지")
            assertEquals(2, targetCount)
        }

        @Test
        @DisplayName("성공: 특정 그룹에 알림을 보낸다")
        fun sendAlertToGroup() {
            // given
            val user1 = UserInfo(id = 1, nickname = "user1", email = "user1@example.com")
            val user2 = UserInfo(id = 2, nickname = "user2", email = "user2@example.com")
            val usersInGroup = listOf(user1, user2)
            `when`(userNotificationGroupRepository.findUsersByGroupNameAndNotIn("group1", listOf()))
                .thenReturn(usersInGroup)

            // when
            val targetCount = notificationService.sendExternalAlert(listOf("@@group1"), "high", "테스트 메시지")

            // then
            // verify(exceptionLogProducer, times(2)).sendLogToQueue("Severity: high, Message: 테스트 메시지")
            assertEquals(2, targetCount)
        }

        @Test
        @DisplayName("성공: 특정 사용자에게 알림을 보낸다")
        fun sendAlertToUser() {
            // given
            val user = UserInfo(id = 1, nickname = "user1", email = "user1@example.com")
            `when`(userRepository.findByNickname("user1")).thenReturn(user)

            // when
            val targetCount = notificationService.sendExternalAlert(listOf("@user1"), "high", "테스트 메시지")

            // then
            // verify(exceptionLogProducer, times(1)).sendLogToQueue("Severity: high, Message: 테스트 메시지")
            assertEquals(1, targetCount)
        }

        @Test
        @DisplayName("실패: 잘못된 타겟 형식으로 알림을 보낸다")
        fun sendAlertWithInvalidTarget() {
            // when
            val targetCount = notificationService.sendExternalAlert(listOf("invalid"), "high", "테스트 메시지")

            // then
            assertEquals(0, targetCount)
            // verify(exceptionLogProducer, never()).sendLogToQueue(anyString())
        }

        @Test
        @DisplayName("성공: 여러 타겟에 알림을 보냅니다")
        fun sendAlertToMultipleTargets() {
            // given
            val user1 = UserInfo(id = 1, nickname = "user1", email = "user1@example.com")
            val user2 = UserInfo(id = 2, nickname = "user2", email = "user2@example.com")
            val user3 = UserInfo(id = 3, nickname = "user3", email = "user3@example.com")

            `when`(userRepository.findByNickname("user1")).thenReturn(user1)
            `when`(userRepository.findByNickname("user2")).thenReturn(user2)
            `when`(userNotificationGroupRepository.findUsersByGroupNameAndNotIn("group1", listOf(user1.id)))
                .thenReturn(listOf(user3))

            // when
            val targetCount = notificationService.sendExternalAlert(listOf("@user1", "@@group1", "@user2"), "high", "테스트 메시지")

            // then
            // verify(exceptionLogProducer, times(3)).sendLogToQueue("Severity: high, Message: 테스트 메시지")
            assertEquals(3, targetCount)
        }
    }
}