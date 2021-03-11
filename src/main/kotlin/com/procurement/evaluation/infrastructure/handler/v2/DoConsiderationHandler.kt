package com.procurement.evaluation.infrastructure.handler.v2

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.infrastructure.api.Action
import com.procurement.evaluation.infrastructure.api.v2.CommandTypeV2
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.handler.v2.base.AbstractQueryHandlerV2
import com.procurement.evaluation.infrastructure.handler.v2.converter.convert
import com.procurement.evaluation.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.evaluation.infrastructure.handler.v2.model.request.DoConsiderationRequest
import com.procurement.evaluation.infrastructure.handler.v2.model.response.DoConsiderationResult
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class DoConsiderationHandler(
    private val awardService: AwardService,
    logger: Logger
) : AbstractQueryHandlerV2<DoConsiderationResult>(logger) {

    override val action: Action = CommandTypeV2.DO_CONSIDERATION

    override fun execute(descriptor: CommandDescriptor): Result<DoConsiderationResult, Failure> =
        descriptor.body.asJsonNode
            .params<DoConsiderationRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }
            .let { params -> awardService.doConsideration(params) }
}
