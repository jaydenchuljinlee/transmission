package io.iron.notification.domain.notification.controller.validation

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [UniqueElementsValidator::class])
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class UniqueElements(
    val message: String = "List must not contain duplicates",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
