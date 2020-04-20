package com.procurement.evaluation.infrastructure.handler.create.requirementresponsehandler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.model.award.requirement.response.CreateRequirementResponseResult
import com.procurement.evaluation.application.repository.HistoryRepository
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.infrastructure.dto.ApiSuccessResponse2
import com.procurement.evaluation.infrastructure.dto.award.create.requirement.response.AddRequirementResponseRequest
import com.procurement.evaluation.infrastructure.dto.convert.convert
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.AbstractHistoricalHandler
import com.procurement.evaluation.model.dto.bpe.Command2Type
import com.procurement.evaluation.model.dto.bpe.tryGetParams
import org.springframework.stereotype.Component

@Component
class CreateRequirementResponseHandler(
    private val awardService: AwardService,
    historyRepository: HistoryRepository,
    logger: Logger
) : AbstractHistoricalHandler<Command2Type, CreateRequirementResponseResult>(
    logger = logger,
    historyRepository = historyRepository,
    target = ApiSuccessResponse2::class.java
) {

    override val action: Command2Type = Command2Type.ADD_REQUIREMENT_RESPONSE

    override fun execute(node: JsonNode): Result<CreateRequirementResponseResult, Fail> {
        val params = node
            .tryGetParams(AddRequirementResponseRequest::class.java)
            .forwardResult { result -> return result }
            .convert()
            .forwardResult { result -> return result }

        return awardService.createRequirementResponse(params = params)
    }
}