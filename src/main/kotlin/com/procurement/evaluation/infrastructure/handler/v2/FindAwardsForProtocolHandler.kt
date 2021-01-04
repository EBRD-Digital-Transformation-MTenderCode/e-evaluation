package com.procurement.evaluation.infrastructure.handler.v2

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.infrastructure.api.Action
import com.procurement.evaluation.infrastructure.api.v2.CommandTypeV2
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.handler.v2.base.AbstractQueryHandlerV2
import com.procurement.evaluation.infrastructure.handler.v2.converter.convert
import com.procurement.evaluation.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.evaluation.infrastructure.handler.v2.model.request.FindAwardsForProtocolRequest
import com.procurement.evaluation.infrastructure.handler.v2.model.response.FindAwardsForProtocolResult
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class FindAwardsForProtocolHandler(
    private val awardService: AwardService,
    logger: Logger
) : AbstractQueryHandlerV2<FindAwardsForProtocolResult?>(logger) {

    override val action: Action = CommandTypeV2.FIND_AWARDS_FOR_PROTOCOL

    override fun execute(descriptor: CommandDescriptor): Result<FindAwardsForProtocolResult?, Failure> =
        descriptor.body.asJsonNode
            .params<FindAwardsForProtocolRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }
            .let { params -> awardService.findAwardsForProtocol(params) }
}
