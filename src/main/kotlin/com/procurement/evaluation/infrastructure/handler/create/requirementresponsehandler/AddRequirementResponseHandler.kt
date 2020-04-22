package com.procurement.evaluation.infrastructure.handler.create.requirementresponsehandler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.domain.functional.ValidationResult
import com.procurement.evaluation.infrastructure.dto.award.create.requirement.response.AddRequirementResponseRequest
import com.procurement.evaluation.infrastructure.dto.convert.convert
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.AbstractValidationHandler
import com.procurement.evaluation.model.dto.bpe.Command2Type
import com.procurement.evaluation.model.dto.bpe.tryGetParams
import org.springframework.stereotype.Component

@Component
class AddRequirementResponseHandler(
    private val awardService: AwardService, logger: Logger
) : AbstractValidationHandler<Command2Type, Fail>(logger) {

    override val action: Command2Type = Command2Type.ADD_REQUIREMENT_RESPONSE

    override fun execute(node: JsonNode): ValidationResult<Fail> {
        val params = node
            .tryGetParams(AddRequirementResponseRequest::class.java)
            .doReturn { error -> return ValidationResult.error(error) }
            .convert()
            .doReturn { error -> return ValidationResult.error(error) }

        return awardService.addRequirementResponse(params = params)
    }
}