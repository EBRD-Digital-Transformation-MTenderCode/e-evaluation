package com.procurement.evaluation.application.model

import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Validated
import com.procurement.evaluation.lib.functional.ValidationRule

fun <T : Collection<Any>?> notEmptyRule(attributeName: String): ValidationRule<T, DataErrors.Validation> =
    ValidationRule { received: T ->
        if (received != null && received.isEmpty())
            Validated.error(DataErrors.Validation.EmptyArray(attributeName))
        else
            Validated.ok()
    }