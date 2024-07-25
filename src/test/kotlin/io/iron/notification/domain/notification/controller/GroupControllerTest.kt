package io.iron.notification.domain.notification.controller

import io.iron.notification.domain.notification.NotificationGroup
import io.iron.notification.domain.notification.service.GroupService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.willDoNothing
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@WebMvcTest(GroupController::class)
class GroupControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var groupService: GroupService

    @Nested
    @DisplayName("createGroup 메서드 실행 시")
    inner class CreateGroupTest {

        @Test
        @DisplayName("성공: 그룹이 성공적으로 생성되었습니다")
        fun success() {
            // given
            val group = NotificationGroup(id = 1L, name = "group_1")

            willDoNothing().given(groupService).createGroup(anyString())

            // when
            val resultActions = mockMvc.perform(
                post("/v1/groups")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("groupName", "group_1")
            )

            // then
            resultActions.andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("그룹 생성했습니다."))

            verify(groupService).createGroup("group_1")
        }
    }

    @Nested
    @DisplayName("joinGroup 메서드 실행 시")
    inner class JoinGroupTest {

        @Test
        @DisplayName("성공: 사용자가 그룹에 성공적으로 가입했습니다")
        fun success() {
            // given
            willDoNothing().given(groupService).joinGroup(anyLong(), anyLong())

            // when
            val resultActions = mockMvc.perform(
                post("/v1/groups/1/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("userId", "1")
            )

            // then
            resultActions.andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("사용자를 그룹에 추가했습니다."))

            verify(groupService).joinGroup(1L, 1L)
        }
    }

    @Nested
    @DisplayName("leaveGroup 메서드 실행 시")
    inner class LeaveGroupTest {

        @Test
        @DisplayName("성공: 사용자가 그룹에서 성공적으로 탈퇴했습니다")
        fun success() {
            // given
            willDoNothing().given(groupService).leaveGroup(anyLong(), anyLong())

            // when
            val resultActions = mockMvc.perform(
                post("/v1/groups/1/leave")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("userId", "1")
            )

            // then
            resultActions.andExpect(status().isOk)
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("사용자를 그룹에서 제거했습니다."))

            verify(groupService).leaveGroup(1L, 1L)
        }
    }
}