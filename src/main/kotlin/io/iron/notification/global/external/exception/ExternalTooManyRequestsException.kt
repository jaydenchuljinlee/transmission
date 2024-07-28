package io.iron.notification.global.external.exception

abstract class ExternalTooManyRequestsException(message: String): RuntimeException(message) {
}