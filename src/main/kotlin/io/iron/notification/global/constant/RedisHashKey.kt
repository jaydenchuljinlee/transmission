package io.iron.notification.global.constant

object RedisHashKey {
    // 공통
    const val REDIS_RESOURCE_FREFIX = "user:info:"

    // SSO Type
    const val SLACK = "slack"

    // Key
    const val REDIS_HASH_USER_INFO_SLACK = "${REDIS_RESOURCE_FREFIX}${SLACK}"
}