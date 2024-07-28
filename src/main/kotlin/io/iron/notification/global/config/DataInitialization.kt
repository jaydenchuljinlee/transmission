package io.iron.notification.global.config

import io.iron.notification.domain.platform.service.PlatformService
import io.iron.notification.domain.user.domain.UserSlackInfo
import io.iron.notification.domain.user.service.UserInfoService
import io.iron.notification.domain.user.service.UserSlackCacheService
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * 애플리케이션 시작 시 사용자 정보를 데이터베이스에서 조회하여 Redis에 저장하는 초기화 클래스
 */
@Configuration
class DataInitialization(
    private val platformService: PlatformService,
    private val userInfoService: UserInfoService,
    private val userSlackCacheService: UserSlackCacheService
) {
    /**
     * 애플리케이션 시작 시 실행되는 초기화 메서드
     *
     * 데이터베이스에서 모든 사용자 정보를 조회한 후, 각 사용자에 대해 UserSlackTokenInfo 객체를 생성하여 Redis에 저장합니다.
     *
     * @return ApplicationRunner 초기화 작업을 수행하는 러너
     */
    @Bean
    fun initializeData() = ApplicationRunner {
        val users = userInfoService.getUsers()

        users.forEach { user ->
            // delYn이 'N'인 사용자 채널 정보 조회
            val userChannels = platformService.findActiveChannelsBy(user.id)

            // 플랫폼을 기준으로 그룹화하여 URL만 추출
            val channelUrlsByPlatform = userChannels.groupBy { it.channel.platform.name }
                .mapValues { entry -> entry.value.map { it.channel.url }[0] }

            val slack = mutableListOf<UserSlackInfo>()

            channelUrlsByPlatform.keys.forEach { type ->
                when(type) {
                    "slack" -> {
                        val userCacheInfo = UserSlackInfo(
                            id = user.id,
                            email = user.email,
                            accessToken = "USER${user.id}-ACCESS-TOKEN",
                            expiresIn = "3600",
                            refreshToken = "USER${user.id}-REFRESH-TOKEN",
                            channel = channelUrlsByPlatform[type] ?: ""
                        )

                        slack.add(userCacheInfo)
                    }
                    "telegram" -> {}
                }
            }

            userSlackCacheService.saveAll(slack)
        }
    }
}