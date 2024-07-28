package io.iron.notification.domain.notification.controller.validation

import io.iron.notification.domain.notification.controller.validation.annotation.ValidTargetPattern
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class ValidTargetPatternValidator: ConstraintValidator<ValidTargetPattern, List<String>> {
    override fun isValid(value: List<String>?, context: ConstraintValidatorContext): Boolean {
        if (value == null) return true

        return value.all { it.startsWith("@") || it.startsWith("@@") }
    }
}