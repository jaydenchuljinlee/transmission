package io.iron.notification.domain.user.service

import io.iron.notification.domain.user.domain.UserInfo
import io.iron.notification.domain.user.domain.UserSlackInfo
import io.iron.notification.domain.user.exception.UserInfoDuplicatedException
import io.iron.notification.domain.user.repository.jpa.UserInfoJpaRepository
import org.springframework.stereotype.Service

@Service
class UserInfoService(
    private val userInfoRepository: UserInfoJpaRepository,
    private val userSlackCacheService: UserSlackCacheService
) {
    fun create(nickname: String, email: String): UserInfo {
        val existingUser = userInfoRepository.findByEmailOrNickname(email, nickname)

        if (existingUser == null) {
            val userInfo = UserInfo(nickname = nickname, email = email)

            val entity = userInfoRepository.saveAndFlush(userInfo)

            val userCacheInfo = UserSlackInfo(
                id = entity.id,
                email = entity.email,
                accessToken = "USER${entity.id}-ACCESS-TOKEN",
                expiresIn = "3600",
                refreshToken = "USER${entity.id}-REFRESH-TOKEN",
                channel = "slack"
            )

            userSlackCacheService.save(userCacheInfo)

            return entity
        }

        if (existingUser.email == email) {
            throw UserInfoDuplicatedException("이미 존재하는 이메일입니다.")
        } else {
            throw UserInfoDuplicatedException("이미 존재하는 이름입니다.")
        }
    }

    fun getUsers(): List<UserInfo> {
        return userInfoRepository.findAll()
    }
}