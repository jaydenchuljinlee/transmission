package io.iron.notification.domain.notification

import io.iron.notification.global.domain.BaseTimeEntity
import io.iron.notification.domain.user.UserInfo
import jakarta.persistence.*

@Entity
@Table(name = "user_notification_platform")
data class UserNotificationPlatform(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

        @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: UserInfo,

        @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "platform_id")
    var platform: NotificationPlatform,

        @Column(nullable = false)
    var externalId: String,
) {
    // constructor() : this(user = UserInfo(), service = ExternalService(), externalId = "")
}