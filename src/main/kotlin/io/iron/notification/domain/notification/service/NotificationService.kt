package io.iron.notification.domain.notification.service

import io.iron.notification.domain.log.service.ExceptionLogProducer
import io.iron.notification.domain.notification.repository.jpa.UserNotificationGroupJpaRepository
import io.iron.notification.domain.user.UserInfo
import io.iron.notification.domain.user.repository.jpa.UserInfoJpaRepository
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val userRepository: UserInfoJpaRepository,
    private val userNotificationGroupRepository: UserNotificationGroupJpaRepository,
    private val exceptionLogProducer: ExceptionLogProducer
) {
    fun sendExternalAlert(targets: List<String>, severity: String, message: String): Int {
        val notifiedUsers = mutableSetOf<UserInfo>()

        val logMessage = "Severity: $severity, Message: $message"

        // filter 대신 any를 사용하여 조건을 만족하는 요소를 찾으면 즉시 true를 반환하고 탐색을 종료
        if (targets.any { it == "@all" }) {
            val allUsers = userRepository.findAll().map { it.id }
            exceptionLogProducer.sendLogToQueue(logMessage)
            return allUsers.size
        }

        targets.forEach { target ->
            when {
                target.startsWith("@@") -> {
                    val groupName = target.substring(2)
                    val userIds = notifiedUsers.map { it.id }
                    val usersInGroup = userNotificationGroupRepository.findUsersByGroupNameAndNotIn(groupName, userIds)
                    notifiedUsers.addAll(usersInGroup)
                }
                target.startsWith("@") -> {
                    val nickname = target.substring(1)

                    if (!notifiedUsers.none { it.nickname == nickname }) return@forEach

                    userRepository.findByNickname(nickname)?.let { notifiedUsers.add(it) }
                }
                // 목적은 예외를 발생시키는 것이 아닌 파라마티에 맞는 UserCount를 반환하는 것이기 때문에 0을 반환합니다.
                else -> return 0
            }
        }

        // 메시지 전송
        for (userId in notifiedUsers) exceptionLogProducer.sendLogToQueue(logMessage)

        return notifiedUsers.size
    }
}