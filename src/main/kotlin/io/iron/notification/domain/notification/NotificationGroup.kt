package io.iron.notification.domain.notification

import io.iron.notification.global.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "notification_group")
data class NotificationGroup(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(nullable = false, unique = true)
    var name: String,
): BaseTimeEntity() {
}