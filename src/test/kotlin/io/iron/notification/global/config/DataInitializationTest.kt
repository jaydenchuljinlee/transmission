package io.iron.notification.global.config

import io.iron.notification.domain.platform.domain.NotificationChannel
import io.iron.notification.domain.platform.domain.NotificationPlatform
import io.iron.notification.domain.platform.domain.UserChannel
import io.iron.notification.domain.platform.service.PlatformService
import io.iron.notification.domain.user.domain.UserInfo
import io.iron.notification.domain.user.domain.UserSlackInfo
import io.iron.notification.domain.user.service.UserInfoService
import io.iron.notification.domain.user.service.UserSlackCacheService
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.*
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.ApplicationRunner
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class DataInitializationTest {
    @Mock
    private lateinit var platformService: PlatformService

    @Mock
    private lateinit var userInfoService: UserInfoService

    @Mock
    private lateinit var userSlackCacheService: UserSlackCacheService

    @InjectMocks
    private lateinit var dataInitialization: DataInitialization

    @Captor
    private lateinit var argumentCaptor: ArgumentCaptor<List<UserSlackInfo>>

    fun <T> capture(argumentCaptor: ArgumentCaptor<T>): T = argumentCaptor.capture()

    @Nested
    @DisplayName("initializeData 메서드 실행 시")
    inner class InitializationTest {
        @Test
        fun `initializeData should save user slack info to cache`() {
            val user1 = UserInfo(id = 1L, nickname = "ironjin", email = "iornjin92@gmail.com")
            val user2 = UserInfo(id = 2L, nickname = "cheoljin", email = "cheoljin0721@hanmail.net")
            val users = listOf(user1, user2)

            val slack = NotificationPlatform(id = 1, name = "slack")

            val slackChannel1 = NotificationChannel(id = 1L, name = "slack_channel_1", url = "http://slack1.com", platform = slack)

            val slackChannel2 = NotificationChannel(id = 1L, name = "telegram_channel_1", url = "http://slack2.com", platform = slack)

            val channelsUser1 = listOf(
                UserChannel(id = 1L, user = user1, channel = slackChannel1)
            )

            val channelsUser2 = listOf(
                UserChannel(id = 2L, user = user2, channel = slackChannel2)
            )

            `when`(userInfoService.getUsers()).thenReturn(users)
            `when`(platformService.findActiveChannelsBy(1L)).thenReturn(channelsUser1)
            `when`(platformService.findActiveChannelsBy(2L)).thenReturn(channelsUser2)

            val runner: ApplicationRunner = dataInitialization.initializeData()
            runner.run(null)

            verify(userSlackCacheService, times(2)).saveAll(capture(argumentCaptor))

            val allCaptured = argumentCaptor.allValues.flatten()

            val expectedSlackInfos1 = UserSlackInfo(
                id = 1L, email = user1.email, accessToken = "USER${user1.id}-ACCESS-TOKEN", expiresIn = "3600", refreshToken = "USER${user1.id}-REFRESH-TOKEN", channel = slackChannel1.url
            )

            val expectedSlackInfos2 = UserSlackInfo(
                id = 2L, email = user2.email, accessToken = "USER${user2.id}-ACCESS-TOKEN", expiresIn = "3600", refreshToken = "USER${user2.id}-REFRESH-TOKEN", channel = slackChannel2.url
            )

            assertTrue(allCaptured.contains(expectedSlackInfos1))
            assertTrue(allCaptured.contains(expectedSlackInfos2))

        }
    }
}