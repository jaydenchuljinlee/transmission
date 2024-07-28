package io.iron.notification.domain.platform.domain

import io.iron.notification.global.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "notification_platform")
data class NotificationPlatform(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true)
    var name: String,
) : BaseTimeEntity() {

}