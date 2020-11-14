package com.procurement.evaluation.infrastructure.handler.check.accesstoaward

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.infrastructure.api.v2.CommandTypeV2
import com.procurement.evaluation.infrastructure.api.v2.tryGetParams
import com.procurement.evaluation.infrastructure.dto.award.access.CheckAccessToAwardRequest
import com.procurement.evaluation.infrastructure.dto.convert.convert
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.v2.base.AbstractValidationHandlerV2
import com.procurement.evaluation.lib.functional.Validated
import com.procurement.evaluation.lib.functional.asValidationError
import org.springframework.stereotype.Component

@Component
class CheckAccessToAwardHandler(
    private val awardService: AwardService, logger: Logger
) : AbstractValidationHandlerV2<CommandTypeV2, Fail>(logger)  {

    override val action: CommandTypeV2 = CommandTypeV2.CHECK_ACCESS_TO_AWARD

    override fun execute(node: JsonNode): Validated<Fail> {
        val params = node
            .tryGetParams(CheckAccessToAwardRequest::class.java)
            .onFailure { return it.reason.asValidationError() }
            .convert()
            .onFailure { return it.reason.asValidationError() }

        return awardService.checkAccessToAward(params = params)
    }
}