package io.iron.notification.domain.notification

import io.iron.notification.global.domain.BaseTimeEntity
import io.iron.notification.domain.user.UserInfo
import jakarta.persistence.*

@Entity
@Table(name = "user_notification_group")
class UserNotificationGroup(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: UserInfo,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    var group: NotificationGroup,
) {
}