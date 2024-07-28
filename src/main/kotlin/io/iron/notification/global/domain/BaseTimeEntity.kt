package io.iron.notification.global.domain

import jakarta.persistence.*
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseTimeEntity(
    @Column(nullable = false, updatable = false)
    open var createdAt: LocalDateTime? = null,

    @Column(nullable = false)
    open var updatedAt: LocalDateTime? = null,
) {
    @PrePersist
    protected fun onCreate() {
        createdAt = LocalDateTime.now()
        updatedAt = LocalDateTime.now()
    }

    @PreUpdate
    protected fun onUpdate() {
        updatedAt = LocalDateTime.now()
    }
}