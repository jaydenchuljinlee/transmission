package io.iron.notification.domain.platform.domain

import io.iron.notification.domain.user.domain.UserInfo
import io.iron.notification.global.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_channel")
data class UserChannel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    var user: UserInfo,

    @ManyToOne
    @JoinColumn(name = "channel_id", nullable = false)
    var channel: NotificationChannel,
): BaseTimeEntity()