package io.iron.notification.domain.notification.service

import io.iron.notification.domain.notification.domain.NotificationGroup
import io.iron.notification.domain.notification.domain.UserNotificationGroup
import io.iron.notification.domain.notification.exception.NotificationGroupDuplicationException
import io.iron.notification.domain.notification.exception.NotificationGroupNotFoundException
import io.iron.notification.domain.notification.exception.UserGroupDuplicationException
import io.iron.notification.domain.notification.exception.UserGroupNotFoundException
import io.iron.notification.domain.notification.repository.jpa.NotificationGroupJpaRepository
import io.iron.notification.domain.notification.repository.jpa.UserNotificationGroupJpaRepository
import io.iron.notification.domain.user.exception.UserInfoNotFoundException
import io.iron.notification.domain.user.repository.jpa.UserInfoJpaRepository
import jakarta.transaction.Transactional
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service

@Service
class GroupService(
    private val groupRepository: NotificationGroupJpaRepository,
    private val userGroupRepository: UserNotificationGroupJpaRepository,
    private val userRepository: UserInfoJpaRepository
) {
    @Transactional
    fun createGroup(name: String) {
        groupRepository.findByName(name)?.let {
            throw NotificationGroupDuplicationException("이미 존재하는 그룹 이름입니다: $name ")
        }

        // 읽기에 대한 Exception이 사전에 처리 되더라도, 동시에 읽기가 발생할 가능성이 있다
        try {
            groupRepository.save(NotificationGroup(name = name))
        } catch (e: DataIntegrityViolationException) {
            throw NotificationGroupDuplicationException("이미 존재하는 그룹 이름입니다: $name ")
        }
    }

    @Transactional
    fun joinGroup(userId: Long, groupId: Long) {
        val user = userRepository.findById(userId).orElseThrow { UserInfoNotFoundException("존재하지 않는 유저 정보입니다: $userId") }
        val group = groupRepository.findById(groupId).orElseThrow { NotificationGroupNotFoundException("존재하지 않는 그룹 정보입니다: $groupId") }

        userGroupRepository.findByUserIdAndGroupId(userId, groupId)?.let {
            throw UserGroupDuplicationException("이미 존재하는 사용자 그룹 정보입니다.")
        }

        userGroupRepository.save(UserNotificationGroup(user = user, group = group))
    }

    @Transactional
    fun leaveGroup(userId: Long, groupId: Long) {
        val userGroup = userGroupRepository.findByUserIdAndGroupId(userId, groupId)
            ?: throw UserGroupNotFoundException("그룹에 존재하지 않는 사용자입니다.")
        userGroupRepository.delete(userGroup)
    }
}