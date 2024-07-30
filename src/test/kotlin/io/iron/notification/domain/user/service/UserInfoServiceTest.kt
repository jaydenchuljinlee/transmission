package io.iron.notification.domain.user.service

import io.iron.notification.domain.user.domain.UserInfo
import io.iron.notification.domain.user.domain.UserSlackInfo
import io.iron.notification.domain.user.exception.UserInfoDuplicatedException
import io.iron.notification.domain.user.repository.jpa.UserInfoJpaRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito
import org.mockito.BDDMockito.willDoNothing
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class UserInfoServiceTest {
    @Mock
    private lateinit var userInfoRepository: UserInfoJpaRepository

    @Mock
    private lateinit var userSlackCacheService: UserSlackCacheService

    private lateinit var userInfoService: UserInfoService

    @BeforeEach
    fun setUp() {
        userInfoService = UserInfoService(userInfoRepository, userSlackCacheService)
    }

    @Nested
    @DisplayName("getUsers 메서드 실행 시")
    internal inner class GetUsersTest {
        @Test
        @DisplayName("성공: 사용자를 생성한다")
        fun `should create new user successfully`() {
            val nickname = "testuser"
            val email = "test@example.com"
            val userInfo = UserInfo(id = 0, nickname = nickname, email = email)

            val userCacheInfo = UserSlackInfo(
                id = userInfo.id,
                email = userInfo.email,
                accessToken = "USER${userInfo.id}-ACCESS-TOKEN",
                expiresIn = "3600",
                refreshToken = "USER${userInfo.id}-REFRESH-TOKEN",
                channel = "slack"
            )

            `when`(userInfoRepository.findByEmailOrNickname(email, nickname)).thenReturn(null)

            `when`(userInfoRepository.saveAndFlush(userInfo)).thenReturn(userInfo)

            willDoNothing().given(userSlackCacheService).save(userCacheInfo)

            val createdUser = userInfoService.create(nickname, email)

            assertNotNull(createdUser)
            assertEquals(nickname, createdUser.nickname)
            assertEquals(email, createdUser.email)
            verify(userInfoRepository, times(1)).saveAndFlush(userInfo)
        }

        @Test
        @DisplayName("실패: 사용자를 이메일이 중복되어 UserInfoDuplicatedException 발생")
        fun `should throw exception when email already exists`() {
            val nickname = "testuser"
            val email = "test@example.com"
            val existingUser = UserInfo(id = 1, nickname = "otheruser", email = email)

            `when`(userInfoRepository.findByEmailOrNickname(email, nickname)).thenReturn(existingUser)

            val exception = assertThrows<UserInfoDuplicatedException> {
                userInfoService.create(nickname, email)
            }

            assertEquals("이미 존재하는 이메일입니다.", exception.message)
        }

        @Test
        @DisplayName("실패: 사용자를 이름 중복되어 UserInfoDuplicatedException 발생")
        fun `should throw exception when nickname already exists`() {
            val nickname = "testuser"
            val email = "test@example.com"
            val existingUser = UserInfo(id = 1, nickname = nickname, email = "other@example.com")

            `when`(userInfoRepository.findByEmailOrNickname(email, nickname)).thenReturn(existingUser)

            val exception = assertThrows<UserInfoDuplicatedException> {
                userInfoService.create(nickname, email)
            }

            assertEquals("이미 존재하는 이름입니다.", exception.message)
        }

        @Test
        @DisplayName("성공: 사용자 목록을 조회한다")
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