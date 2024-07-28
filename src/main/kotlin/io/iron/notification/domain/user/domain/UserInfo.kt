package io.iron.notification.domain.user.domain

import io.iron.notification.global.domain.BaseTimeEntity
import io.iron.notification.global.domain.StateYn
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

    @Version
    var version: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    var delYn: StateYn = StateYn.N
): BaseTimeEntity() {

}