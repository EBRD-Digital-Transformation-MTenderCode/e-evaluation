package com.procurement.evaluation.infrastructure.fail.error

import com.procurement.evaluation.infrastructure.fail.Fail

sealed class ValidationError(numberError: String, override val description: String) : Fail.Error("VR-") {
    override val code: String = prefix + numberError
}