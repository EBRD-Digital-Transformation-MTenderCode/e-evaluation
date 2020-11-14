package com.procurement.evaluation.infrastructure.handler.get.awardstatebyids

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.infrastructure.dto.award.state.GetAwardStateByIdsRequest
import com.procurement.evaluation.infrastructure.dto.award.state.GetAwardStateByIdsResult
import com.procurement.evaluation.infrastructure.dto.convert.convert
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.AbstractHandler
import com.procurement.evaluation.model.dto.bpe.Command2Type
import com.procurement.evaluation.model.dto.bpe.tryGetParams
import org.springframework.stereotype.Component

@Component
class GetAwardStateByIdsHandler(
    private val awardService: AwardService, logger: Logger
) : AbstractHandler<Command2Type, List<GetAwardStateByIdsResult>>(logger) {

    override val action: Command2Type = Command2Type.GET_AWARD_STATES_BY_IDS

    override fun execute(node: JsonNode): Result<List<GetAwardStateByIdsResult>, Fail> {
        val params = node
            .tryGetParams(GetAwardStateByIdsRequest::class.java)
            .onFailure { return it }
            .convert()
            .onFailure { return it }

        return awardService.getAwardState(params = params)
    }
}