package com.procurement.evaluation.infrastructure.handler.v2

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.Transform
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.infrastructure.api.Action
import com.procurement.evaluation.infrastructure.api.v2.CommandTypeV2
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.handler.HistoryRepository
import com.procurement.evaluation.infrastructure.handler.v2.base.AbstractHistoricalHandlerV2
import com.procurement.evaluation.infrastructure.handler.v2.converter.convert
import com.procurement.evaluation.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.evaluation.infrastructure.handler.v2.model.request.StartAwardPeriodRequest
import com.procurement.evaluation.infrastructure.handler.v2.model.response.StartAwardPeriodResult
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class StartAwardPeriodHandler(
    private val awardService: AwardService,
    transform: Transform,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<StartAwardPeriodResult>(
    logger = logger,
    transform = transform,
    historyRepository = historyRepository
) {

    override val action: Action = CommandTypeV2.START_AWARD_PERIOD

    override fun execute(descriptor: CommandDescriptor): Result<StartAwardPeriodResult, Failure> =
        descriptor.body.asJsonNode
            .params<StartAwardPeriodRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }
            .let { params -> awardService.startAwardPeriod(params) }
}
