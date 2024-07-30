package io.iron.notification.domain.notification.service

import io.iron.notification.domain.notification.domain.NotificationGroup
import io.iron.notification.domain.notification.domain.UserNotificationGroup
import io.iron.notification.domain.notification.exception.NotificationGroupDuplicationException
import io.iron.notification.domain.notification.exception.NotificationGroupNotFoundException
import io.iron.notification.domain.notification.exception.UserGroupDuplicationException
import io.iron.notification.domain.notification.exception.UserGroupNotFoundException
import io.iron.notification.domain.notification.repository.jpa.NotificationGroupJpaRepository
import io.iron.notification.domain.notification.repository.jpa.UserNotificationGroupJpaRepository
import io.iron.notification.domain.user.domain.UserInfo
import io.iron.notification.domain.user.exception.UserInfoNotFoundException
import io.iron.notification.domain.user.repository.jpa.UserInfoJpaRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.willDoNothing
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.util.*
import kotlin.test.Test

@ExtendWith(MockitoExtension::class)
class GroupServiceTest {
    @Mock
    private lateinit var groupRepository: NotificationGroupJpaRepository

    @Mock
    private lateinit var userGroupRepository: UserNotificationGroupJpaRepository

    @Mock
    private lateinit var userRepository: UserInfoJpaRepository

    private lateinit var groupService: GroupService

    @BeforeEach
    fun setUp() {
        groupService = GroupService(
            groupRepository,
            userGroupRepository,
            userRepository
        )
    }

    @Nested
    @DisplayName("createGroup 메서드 실행 시")
    inner class CreateGroupTest {

        @Test
        @DisplayName("성공: 그룹이 성공적으로 생성되었습니다")
        fun success() {
            // given
            given(groupRepository.findByName(anyString())).willReturn(null)
            given(groupRepository.save(any(NotificationGroup::class.java))).willAnswer { invocation ->
                invocation.arguments[0] as NotificationGroup
            }

            // when
            groupService.createGroup("group_1")

            // then
            verify(groupRepository).findByName("group_1")
            verify(groupRepository).save(any(NotificationGroup::class.java))
        }

        @Test
        @DisplayName("실패: 그룹 이름이 중복되었습니다")
        fun duplicateGroupName() {
            // given
            given(groupRepository.findByName(anyString())).willReturn(NotificationGroup(name = "group_1"))

            // when & then
            val exception = assertThrows<NotificationGroupDuplicationException> {
                groupService.createGroup("group_1")
            }

            assert(exception.message!!.contains("이미 존재하는 그룹 이름입니다: group_1"))
            verify(groupRepository).findByName("group_1")
            verify(groupRepository, never()).save(any(NotificationGroup::class.java))
        }
    }

    @Nested
    @DisplayName("joinGroup 메서드 실행 시")
    inner class JoinGroupTest {

        @Test
        @DisplayName("성공: 사용자가 그룹에 성공적으로 가입했습니다")
        fun success() {
            // given
            val user = UserInfo(id = 1L, nickname = "user1", email = "user1@example.com")
            val group = NotificationGroup(id = 1L, name = "group_1")
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user))
            given(groupRepository.findById(anyLong())).willReturn(Optional.of(group))
            given(userGroupRepository.findByUserIdAndGroupId(anyLong(), anyLong())).willReturn(null)

            // when
            groupService.joinGroup(1L, 1L)

            // then
            verify(userRepository).findById(1L)
            verify(groupRepository).findById(1L)
            verify(userGroupRepository).findByUserIdAndGroupId(1L, 1L)
            verify(userGroupRepository).save(any(UserNotificationGroup::class.java))
        }

        @Test
        @DisplayName("실패: 사용자 정보가 존재하지 않습니다")
        fun userNotFound() {
            // given
            given(userRepository.findById(anyLong())).willReturn(Optional.empty())

            // when & then
            val exception = assertThrows<UserInfoNotFoundException> {
                groupService.joinGroup(1L, 1L)
            }



            assert(exception.message!!.contains("존재하지 않는 유저 정보입니다: 1"))
            verify(userRepository).findById(1L)
            verify(groupRepository, never()).findById(anyLong())
            verify(userGroupRepository, never()).findByUserIdAndGroupId(anyLong(), anyLong())
            verify(userGroupRepository, never()).save(any(UserNotificationGroup::class.java))
        }

        @Test
        @DisplayName("실패: 그룹 정보가 존재하지 않습니다")
        fun groupNotFound() {
            // given
            val user = UserInfo(id = 1L, nickname = "user1", email = "user1@example.com")
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user))
            given(groupRepository.findById(anyLong())).willReturn(Optional.empty())

            // when & then
            val exception = assertThrows<NotificationGroupNotFoundException> {
                groupService.joinGroup(1L, 1L)
            }



            assert(exception.message!!.contains("존재하지 않는 그룹 정보입니다: 1"))
            verify(userRepository).findById(1L)
            verify(groupRepository).findById(1L)
            verify(userGroupRepository, never()).findByUserIdAndGroupId(anyLong(), anyLong())
            verify(userGroupRepository, never()).save(any(UserNotificationGroup::class.java))
        }

        @Test
        @DisplayName("실패: 이미 존재하는 사용자 그룹 정보입니다")
        fun userGroupDuplication() {
            // given
            val user = UserInfo(id = 1L, nickname = "user1", email = "user1@example.com")
            val group = NotificationGroup(id = 1L, name = "group_1")
            val userGroup = UserNotificationGroup(user = user, group = group)
            given(userRepository.findById(anyLong())).willReturn(Optional.of(user))
            given(groupRepository.findById(anyLong())).willReturn(Optional.of(group))
            given(userGroupRepository.findByUserIdAndGroupId(anyLong(), anyLong())).willReturn(userGroup)

            // when & then
            val exception = assertThrows<UserGroupDuplicationException> {
                groupService.joinGroup(1L, 1L)
            }



            assert(exception.message!!.contains("이미 존재하는 사용자 그룹 정보입니다"))
            verify(userRepository).findById(1L)
            verify(groupRepository).findById(1L)
            verify(userGroupRepository).findByUserIdAndGroupId(1L, 1L)
            verify(userGroupRepository, never()).save(any(UserNotificationGroup::class.java))
        }
    }

    @Nested
    @DisplayName("leaveGroup 메서드 실행 시")
    inner class LeaveGroupTest {

        @Test
        @DisplayName("성공: 사용자가 그룹에서 성공적으로 탈퇴했습니다")
        fun success() {
            // given
            val user = UserInfo(id = 1L, nickname = "user1", email = "user1@example.com")
            val group = NotificationGroup(id = 1L, name = "group_1")
            val userGroup = UserNotificationGroup(user = user, group = group)
            given(userGroupRepository.findByUserIdAndGroupId(anyLong(), anyLong())).willReturn(userGroup)
            willDoNothing().given(userGroupRepository).delete(userGroup)

            // when
            groupService.leaveGroup(1L, 1L)

            // then
            verify(userGroupRepository).findByUserIdAndGroupId(1L, 1L)
            verify(userGroupRepository).delete(userGroup)
        }

        @Test
        @DisplayName("실패: 그룹에 존재하지 않는 사용자입니다")
        fun userNotInGroup() {
            // given
            given(userGroupRepository.findByUserIdAndGroupId(anyLong(), anyLong())).willReturn(null)

            // when & then
            val exception = assertThrows<UserGroupNotFoundException> {
                groupService.leaveGroup(1L, 1L)
            }



            assert(exception.message!!.contains("그룹에 존재하지 않는 사용자입니다"))
            verify(userGroupRepository).findByUserIdAndGroupId(1L, 1L)
            verify(userGroupRepository, never()).delete(any(UserNotificationGroup::class.java))
        }
    }
}