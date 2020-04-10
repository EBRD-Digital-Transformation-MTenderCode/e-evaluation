package com.procurement.evaluation.infrastructure.handler.check.accesstoaward

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.domain.functional.ValidationResult
import com.procurement.evaluation.infrastructure.dto.award.access.CheckAccessToAwardRequest
import com.procurement.evaluation.infrastructure.dto.convert.convert
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.AbstractValidationHandler
import com.procurement.evaluation.model.dto.bpe.Command2Type
import com.procurement.evaluation.model.dto.bpe.tryGetParams
import org.springframework.stereotype.Component

@Component
class CheckAccessToAwardHandler(
    private val awardService: AwardService, logger: Logger
) : AbstractValidationHandler<Command2Type, Fail>(logger)  {

    override val action: Command2Type = Command2Type.CHECK_ACCESS_TO_AWARD

    override fun execute(node: JsonNode): ValidationResult<Fail> {
        val params = node
            .tryGetParams(CheckAccessToAwardRequest::class.java)
            .doReturn { error -> return ValidationResult.error(error) }
            .convert()
            .doReturn { error -> return ValidationResult.error(error) }

        return awardService.checkAccessToAward(params = params)
    }
}