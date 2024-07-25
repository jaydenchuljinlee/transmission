package io.iron.notification.domain.notification.repository

import io.iron.notification.domain.notification.UserNotificationGroup
import io.iron.notification.domain.user.UserInfo
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserNotificationGroupRepository {
    @Query("""
        SELECT u 
        FROM UserInfo u 
        JOIN UserNotificationGroup ung ON u.id = ung.user.id 
        JOIN NotificationGroup ng ON ung.group.id = ng.id 
        WHERE ng.name = :groupName 
        AND u.id NOT IN :excludedUserIds
    """)
    fun findUsersByGroupNameAndNotIn(
        @Param("groupName") groupName: String,
        @Param("excludedUserIds") excludedUserIds: List<Long>
    ): List<UserInfo>

    fun findByUserIdAndGroupId(userId: Long, groupId: Long): UserNotificationGroup?
}