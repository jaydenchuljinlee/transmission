package io.iron.notification.domain.notification.domain

import io.iron.notification.domain.notification.domain.enumeration.NotificationStatus
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "notification_delivery_log")
data class NotificationDeliveryLog(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: Long,

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    val message: String,

    @Column(name = "channel", nullable = false)
    val channel: String,

    @Column(name = "platform", nullable = false)
    val platform: String,

    @Column(name = "batch_id", nullable = false)
    val batchId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: NotificationStatus = NotificationStatus.PENDING,
)