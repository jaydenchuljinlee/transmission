package io.iron.notification.domain.user.controller

import io.iron.notification.domain.user.controller.request.CreateUserRequest
import io.iron.notification.domain.user.service.UserInfoService
import io.iron.notification.global.domain.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/users")
class UserController(private val userInfoService: UserInfoService) {
    @PostMapping
    fun createUser(@RequestBody userRequest: CreateUserRequest): ApiResponse {
        val newUser = userInfoService.create(userRequest.nickname, userRequest.email)
        return ApiResponse.ok(message = "사용자를 생성했습니다.", data = newUser)
    }

    @GetMapping
    fun getUsers(): ApiResponse {
        val users = userInfoService.getUsers()
        return ApiResponse.ok(message = "사용자 목록을 조회했습니다.", data = users)
    }
}