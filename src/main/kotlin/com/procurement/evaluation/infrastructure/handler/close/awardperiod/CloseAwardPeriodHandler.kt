package com.procurement.evaluation.infrastructure.handler.close.awardperiod

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.repository.history.HistoryRepository
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.infrastructure.dto.ApiSuccessResponse2
import com.procurement.evaluation.infrastructure.dto.convert.convert
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.AbstractHistoricalHandler
import com.procurement.evaluation.model.dto.bpe.Command2Type
import com.procurement.evaluation.model.dto.bpe.tryGetParams
import org.springframework.stereotype.Component

@Component
class CloseAwardPeriodHandler(
    private val awardService: AwardService,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandler<Command2Type, CloseAwardPeriodResult>(
    logger = logger,
    historyRepository = historyRepository,
    target = ApiSuccessResponse2::class.java
) {

    override val action: Command2Type = Command2Type.CLOSE_AWARD_PERIOD

    override fun execute(node: JsonNode): Result<CloseAwardPeriodResult, Fail> {
        val params = node
            .tryGetParams(CloseAwardPeriodRequest::class.java)
            .onFailure { return it }
            .convert()
            .onFailure { return it }

        return awardService.closeAwardPeriod(params = params)
    }
}
