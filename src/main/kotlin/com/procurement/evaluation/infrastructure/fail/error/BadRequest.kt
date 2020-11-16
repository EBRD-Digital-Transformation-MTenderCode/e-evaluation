package com.procurement.evaluation.infrastructure.fail.error

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.infrastructure.fail.Failure

class BadRequest(override val description: String = "Invalid json", val exception: Exception) : Failure.Error("RQ-") {
    private val numberError = "1"
    override val code: String = "${prefix}${numberError}"

    override fun logging(logger: Logger) {
        logger.error(message = message, exception = exception)
    }
}
