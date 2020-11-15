package com.procurement.evaluation.infrastructure.handler.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.infrastructure.api.v2.ApiSuccessResponse2
import com.procurement.evaluation.infrastructure.api.v2.CommandTypeV2
import com.procurement.evaluation.infrastructure.api.v2.tryGetParams
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.HistoryRepository
import com.procurement.evaluation.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.evaluation.infrastructure.handler.v2.converter.convert
import com.procurement.evaluation.infrastructure.handler.v2.model.request.CloseAwardPeriodRequest
import com.procurement.evaluation.infrastructure.handler.v2.model.response.CloseAwardPeriodResult
import com.procurement.evaluation.lib.functional.Result
import org.springframework.stereotype.Component

@Component
class CloseAwardPeriodHandler(
    private val awardService: AwardService,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<CommandTypeV2, CloseAwardPeriodResult>(
    logger = logger,
    historyRepository = historyRepository,
    target = ApiSuccessResponse2::class.java
) {

    override val action: CommandTypeV2 = CommandTypeV2.CLOSE_AWARD_PERIOD

    override fun execute(node: JsonNode): Result<CloseAwardPeriodResult, Fail> {
        val params = node
            .tryGetParams(CloseAwardPeriodRequest::class.java)
            .onFailure { return it }
            .convert()
            .onFailure { return it }

        return awardService.closeAwardPeriod(params = params)
    }
}
