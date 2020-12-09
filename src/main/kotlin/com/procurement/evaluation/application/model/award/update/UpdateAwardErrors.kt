package com.procurement.evaluation.application.model.award.update

import com.procurement.evaluation.infrastructure.fail.Failure

sealed class UpdateAwardErrors(
    numberError: String, override val description: String, val id: String? = null
) : Failure.Error("VR.COM-") {

    override val code: String = prefix + numberError

    class AwardNotFound() : UpdateAwardErrors(
        numberError = "4.10.1",
        description = "Cannot find awards by paramenters from request."
    )
}
