package io.iron.notification.domain.user.domain

import io.iron.notification.domain.user.domain.enumeration.TokenType
import io.iron.notification.global.constant.RedisHashKey
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash(RedisHashKey.REDIS_HASH_USER_TOKEN_INFO_SLACK)
data class UserSlackTokenInfo(
    @Id
    var id: Long,
    val email: String,
    val accessToken: String,
    val expiresIn: String = "",
    val refreshToken: String = "",
    val tokenType: TokenType = TokenType.BEARER,
    val scope: String,
)