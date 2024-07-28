package io.iron.notification.domain.platform.domain

import io.iron.notification.global.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "notification_channel")
data class NotificationChannel(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var url: String,

    @ManyToOne
    @JoinColumn(name = "platform_id", nullable = false)
    var platform: NotificationPlatform,
): BaseTimeEntity()