package io.iron.notification.domain.user.service

import io.iron.notification.domain.user.domain.UserInfo
import io.iron.notification.domain.user.repository.jpa.UserInfoJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class UserInfoServiceTest {
    @Mock
    private lateinit var userInfoRepository: UserInfoJpaRepository

    private lateinit var userInfoService: UserInfoService

    @BeforeEach
    fun setUp() {
        userInfoService = UserInfoService(userInfoRepository)
    }

    @Nested
    @DisplayName("getUsers 메서드 실행 시")
    internal inner class GetUsersTest {
        @Test
        fun `getUsers returns list of users`() {
            val user1 = UserInfo(id = 1L, nickname = "ironjin", email = "iornjin92@gmail.com")
            val user2 = UserInfo(id = 2L, nickname = "cheoljin", email = "cheoljin0721@hanmail.net")

            `when`(userInfoRepository.findAll()).thenReturn(listOf(user1, user2))

            val result = userInfoService.getUsers()

            assertEquals(2, result.size)
            assertEquals("ironjin", result[0].nickname)
            assertEquals("cheoljin", result[1].nickname)
        }
    }
}