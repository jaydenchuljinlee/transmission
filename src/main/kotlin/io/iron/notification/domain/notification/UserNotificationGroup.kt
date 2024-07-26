package io.iron.notification.domain.notification

import io.iron.notification.domain.user.domain.UserInfo
import jakarta.persistence.*

@Entity
@Table(name = "user_notification_group")
class UserNotificationGroup(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserInfo,

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    var group: NotificationGroup
) {
}