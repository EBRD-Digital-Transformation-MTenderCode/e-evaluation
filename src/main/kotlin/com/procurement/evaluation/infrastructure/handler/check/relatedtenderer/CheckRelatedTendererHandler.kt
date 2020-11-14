package com.procurement.evaluation.infrastructure.handler.check.relatedtenderer

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.domain.functional.Validated
import com.procurement.evaluation.domain.functional.asValidationError
import com.procurement.evaluation.infrastructure.dto.award.tenderer.CheckRelatedTendererRequest
import com.procurement.evaluation.infrastructure.dto.convert.convert
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.AbstractValidationHandler
import com.procurement.evaluation.model.dto.bpe.Command2Type
import com.procurement.evaluation.model.dto.bpe.tryGetParams
import org.springframework.stereotype.Component

@Component
class CheckRelatedTendererHandler(
    private val awardService: AwardService,
    logger: Logger
) : AbstractValidationHandler<Command2Type, Fail>(logger) {

    override val action: Command2Type = Command2Type.CHECK_RELATED_TENDERER

    override fun execute(node: JsonNode): Validated<Fail> {
        val params = node
            .tryGetParams(CheckRelatedTendererRequest::class.java)
            .onFailure { return it.reason.asValidationError() }
            .convert()
            .onFailure { return it.reason.asValidationError() }

        return awardService.checkRelatedTenderer(params = params)
    }
}