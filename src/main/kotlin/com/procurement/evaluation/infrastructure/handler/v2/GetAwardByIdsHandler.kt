package com.procurement.evaluation.infrastructure.handler.v2

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.infrastructure.api.Action
import com.procurement.evaluation.infrastructure.api.v2.CommandTypeV2
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.handler.v2.base.AbstractQueryHandlerV2
import com.procurement.evaluation.infrastructure.handler.v2.converter.convert
import com.procurement.evaluation.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.evaluation.infrastructure.handler.v2.model.request.GetAwardByIdsRequest
import com.procurement.evaluation.infrastructure.handler.v2.model.response.GetAwardByIdsResult
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class GetAwardByIdsHandler(
    private val awardService: AwardService,
    logger: Logger
) : AbstractQueryHandlerV2<GetAwardByIdsResult>(logger) {

    override val action: Action = CommandTypeV2.GET_AWARD_BY_IDS

    override fun execute(descriptor: CommandDescriptor): Result<GetAwardByIdsResult, Failure> =
        descriptor.body.asJsonNode
            .params<GetAwardByIdsRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }
            .let { params -> awardService.getAwardByIds(params) }
}
