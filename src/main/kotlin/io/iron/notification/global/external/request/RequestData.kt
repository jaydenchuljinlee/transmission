package io.iron.notification.global.external.request

import java.util.UUID

data class RequestData(val userId: Long, val message: String, val uuid: UUID)