package com.procurement.evaluation.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.infrastructure.api.v2.CommandTypeV2
import com.procurement.evaluation.infrastructure.api.v2.tryGetParams
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.v2.base.AbstractValidationHandlerV2
import com.procurement.evaluation.infrastructure.handler.v2.converter.convert
import com.procurement.evaluation.infrastructure.handler.v2.model.request.AddRequirementResponseRequest
import com.procurement.evaluation.lib.functional.Validated
import com.procurement.evaluation.lib.functional.asValidationError
import org.springframework.stereotype.Component

@Component
class AddRequirementResponseHandler(
    private val awardService: AwardService, logger: Logger
) : AbstractValidationHandlerV2<CommandTypeV2, Fail>(logger) {

    override val action: CommandTypeV2 = CommandTypeV2.ADD_REQUIREMENT_RESPONSE

    override fun execute(node: JsonNode): Validated<Fail> {
        val params = node
            .tryGetParams(AddRequirementResponseRequest::class.java)
            .onFailure { return it.reason.asValidationError() }
            .convert()
            .onFailure { return it.reason.asValidationError() }

        return awardService.addRequirementResponse(params = params)
    }
}