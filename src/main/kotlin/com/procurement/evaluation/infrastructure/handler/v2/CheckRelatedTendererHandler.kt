package com.procurement.evaluation.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.infrastructure.api.Action
import com.procurement.evaluation.infrastructure.api.v2.CommandTypeV2
import com.procurement.evaluation.infrastructure.api.v2.tryGetParams
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.handler.v2.base.AbstractValidationHandlerV2
import com.procurement.evaluation.infrastructure.handler.v2.converter.convert
import com.procurement.evaluation.infrastructure.handler.v2.model.request.CheckRelatedTendererRequest
import com.procurement.evaluation.lib.functional.Validated
import com.procurement.evaluation.lib.functional.asValidationError
import org.springframework.stereotype.Component

@Component
class CheckRelatedTendererHandler(
    private val awardService: AwardService,
    logger: Logger
) : AbstractValidationHandlerV2(logger) {

    override val action: Action = CommandTypeV2.CHECK_RELATED_TENDERER

    override fun execute(node: JsonNode): Validated<Failure> {
        val params = node
            .tryGetParams(CheckRelatedTendererRequest::class.java)
            .onFailure { return it.reason.asValidationError() }
            .convert()
            .onFailure { return it.reason.asValidationError() }

        return awardService.checkRelatedTenderer(params = params)
    }
}
