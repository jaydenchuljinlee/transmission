package io.iron.notification.domain.notification.controller.request

import io.iron.notification.domain.notification.controller.validation.UniqueElements
import io.iron.notification.domain.notification.controller.validation.ValidTargetPattern
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class NotificationRequest(
    @field:Size(min = 1, message = "Targets must not be empty")
    @field:UniqueElements(message = "Targets must not contain duplicates")
    @field:ValidTargetPattern(message = "Each target must start with '@' or '@@'")
    val target: List<String>,

    @field:NotBlank(message = "Severity must not be blank")
    val severity: String,

    @field:NotBlank(message = "Message must not be blank")
    val message: String
) {

}