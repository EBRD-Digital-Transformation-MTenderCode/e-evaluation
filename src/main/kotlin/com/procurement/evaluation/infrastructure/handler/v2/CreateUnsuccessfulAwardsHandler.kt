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
import com.procurement.evaluation.infrastructure.handler.v2.model.request.CreateUnsuccessfulAwardsRequest
import com.procurement.evaluation.infrastructure.handler.v2.model.response.CreateUnsuccessfulAwardsResult
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.flatMap
import org.springframework.stereotype.Component

@Component
class CreateUnsuccessfulAwardsHandler(
    private val awardService: AwardService,
    transform: Transform,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandlerV2<List<CreateUnsuccessfulAwardsResult>>(
    logger = logger,
    transform = transform,
    historyRepository = historyRepository
) {

    override val action: Action = CommandTypeV2.CREATE_UNSUCCESSFUL_AWARDS

    override fun execute(descriptor: CommandDescriptor): Result<List<CreateUnsuccessfulAwardsResult>, Failure> =
        descriptor.body.asJsonNode
            .params<CreateUnsuccessfulAwardsRequest>()
            .flatMap { it.convert() }
            .onFailure { return it }
            .let { params ->
                awardService.createUnsuccessfulAwards(params)
            }
}
