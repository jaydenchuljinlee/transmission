package io.iron.notification.domain.user.controller.request

data class CreateUserRequest(
    val nickname: String,
    val email: String
)