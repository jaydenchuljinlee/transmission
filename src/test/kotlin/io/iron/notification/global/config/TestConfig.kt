package io.iron.notification.global.config

import io.iron.notification.global.config.properties.SlackProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestConfig {
    @Bean
    fun slackProperties() = SlackProperties(
        host = "https://slack.com/api/chat.postMessage",
        maxRequestsPerSecond = 10,
        initialDelay = 1000L
    )
}