package com.procurement.evaluation.infrastructure.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.infrastructure.dto.ApiResponse2
import com.procurement.evaluation.infrastructure.handler.check.accesstoaward.CheckAccessToAwardHandler
import com.procurement.evaluation.infrastructure.handler.check.relatedtenderer.CheckRelatedTendererHandler
import com.procurement.evaluation.infrastructure.handler.close.awardperiod.CloseAwardPeriodHandler
import com.procurement.evaluation.infrastructure.handler.create.requirementresponsehandler.AddRequirementResponseHandler
import com.procurement.evaluation.infrastructure.handler.create.unsuccessfulaward.CreateUnsuccessfulAwardsHandler
import com.procurement.evaluation.infrastructure.handler.get.awardstatebyids.GetAwardStateByIdsHandler
import com.procurement.evaluation.model.dto.bpe.Command2Type
import com.procurement.evaluation.model.dto.bpe.generateResponseOnFailure
import com.procurement.evaluation.model.dto.bpe.tryGetAction
import com.procurement.evaluation.model.dto.bpe.tryGetId
import com.procurement.evaluation.model.dto.bpe.tryGetVersion
import org.springframework.stereotype.Service

@Service
class Command2Service(
    private val logger: Logger,
    private val getAwardStateByIdsHandler: GetAwardStateByIdsHandler,
    private val checkAccessToAwardHandler: CheckAccessToAwardHandler,
    private val checkRelatedTendererHandler: CheckRelatedTendererHandler,
    private val addRequirementResponseHandler: AddRequirementResponseHandler,
    private val createUnsuccessfulAwardHandler: CreateUnsuccessfulAwardsHandler,
    private val closeAwardPeriodHandler: CloseAwardPeriodHandler
) {

    fun execute(node: JsonNode): ApiResponse2 {
        val action = node.tryGetAction()
            .doReturn { error ->
                return generateResponseOnFailure(
                    fail = error,
                    id = node.tryGetId().get,
                    version = node.tryGetVersion().get,
                    logger = logger
                )
            }

        return when (action) {
            Command2Type.GET_AWARD_STATES_BY_IDS -> getAwardStateByIdsHandler.handle(node)

            Command2Type.CHECK_ACCESS_TO_AWARD -> checkAccessToAwardHandler.handle(node)

            Command2Type.CHECK_RELATED_TENDERER -> checkRelatedTendererHandler.handle(node)

            Command2Type.ADD_REQUIREMENT_RESPONSE -> addRequirementResponseHandler.handle(node)

            Command2Type.CREATE_UNSUCCESSFUL_AWARDS -> createUnsuccessfulAwardHandler.handle(node)

            Command2Type.CLOSE_AWARD_PERIOD -> closeAwardPeriodHandler.handle(node)
        }
    }
}