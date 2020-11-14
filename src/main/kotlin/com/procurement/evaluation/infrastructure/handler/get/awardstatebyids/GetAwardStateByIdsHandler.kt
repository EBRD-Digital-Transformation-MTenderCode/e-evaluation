package com.procurement.evaluation.infrastructure.handler.get.awardstatebyids

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.infrastructure.api.v2.CommandTypeV2
import com.procurement.evaluation.infrastructure.api.v2.tryGetParams
import com.procurement.evaluation.infrastructure.dto.award.state.GetAwardStateByIdsRequest
import com.procurement.evaluation.infrastructure.dto.award.state.GetAwardStateByIdsResult
import com.procurement.evaluation.infrastructure.dto.convert.convert
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.v2.base.AbstractHandlerV2
import com.procurement.evaluation.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class GetAwardStateByIdsHandler(
    private val awardService: AwardService, logger: Logger
) : AbstractHandlerV2<CommandTypeV2, List<GetAwardStateByIdsResult>>(logger) {

    override val action: CommandTypeV2 = CommandTypeV2.GET_AWARD_STATES_BY_IDS

    override fun execute(node: JsonNode): Result<List<GetAwardStateByIdsResult>, Fail> {
        val params = node
            .tryGetParams(GetAwardStateByIdsRequest::class.java)
            .onFailure { return it }
            .convert()
            .onFailure { return it }

        return awardService.getAwardState(params = params)
    }
}