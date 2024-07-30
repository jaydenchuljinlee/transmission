package io.iron.notification.global.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "external.slack")
class SlackProperties(
    var host: String = "",
    var maxRequestsPerSecond: Int = 0,
    var initialDelay: Long = 0,
    var timeout: Long = 0
)