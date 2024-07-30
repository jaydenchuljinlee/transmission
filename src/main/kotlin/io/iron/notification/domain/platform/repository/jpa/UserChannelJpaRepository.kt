package io.iron.notification.domain.platform.repository.jpa

import io.iron.notification.domain.platform.domain.UserChannel
import io.lettuce.core.dynamic.annotation.Param
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface UserChannelJpaRepository: JpaRepository<UserChannel, Long> {
    @Query("""
        SELECT uc 
        FROM UserChannel uc 
        JOIN uc.channel c 
        JOIN c.platform p 
        WHERE uc.user.id = :userId
    """)
    fun findActiveChannelsByUserId(@Param("userId") userId: Long): List<UserChannel>
}