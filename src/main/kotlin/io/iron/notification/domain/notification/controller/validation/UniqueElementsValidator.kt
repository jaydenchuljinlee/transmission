package io.iron.notification.domain.notification.controller.validation

import io.iron.notification.domain.notification.controller.validation.annotation.UniqueElements
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class UniqueElementsValidator: ConstraintValidator<UniqueElements, List<*>> {
    override fun isValid(value: List<*>?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return true
        return value.size == value.distinct().size
    }
}