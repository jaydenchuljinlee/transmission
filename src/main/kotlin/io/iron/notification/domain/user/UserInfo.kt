package io.iron.notification.domain.user

import io.iron.notification.global.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "user_info")
data class UserInfo(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,

    @Column(nullable = false, unique = true)
    var nickname: String,

    @Column(nullable = false, unique = true)
    var email: String,

): BaseTimeEntity() {

}