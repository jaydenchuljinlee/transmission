package io.iron.notification.domain.notification.controller

import io.iron.notification.domain.notification.service.GroupService
import io.iron.notification.global.domain.ApiResponse
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/v1/groups")
class GroupController(
    private val groupService: GroupService
) {
    @PostMapping
    fun createGroup(@RequestParam groupName: String): ApiResponse {
        val newGroup = groupService.createGroup(groupName)
        return ApiResponse.ok(message = "그룹 생성했습니다.", data = newGroup)
    }

    @PostMapping("/{groupId}/join")
    fun joinGroup(
        @PathVariable groupId: Long,
        @RequestParam userId: Long): ApiResponse {
        groupService.joinGroup(userId, groupId)
        return ApiResponse.ok(null, "사용자를 그룹에 추가했습니다.")
    }

    @DeleteMapping("/{groupId}/leave")
    fun leaveGroup(@PathVariable groupId: Long, @RequestParam userId: Long): ApiResponse {
        groupService.leaveGroup(userId, groupId)
        return ApiResponse.ok(null, "사용자를 그룹에서 제거했습니다.")
    }
}