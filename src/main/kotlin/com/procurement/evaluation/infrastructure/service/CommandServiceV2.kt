package com.procurement.evaluation.infrastructure.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.infrastructure.api.v2.ApiResponse2
import com.procurement.evaluation.infrastructure.api.v2.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.evaluation.infrastructure.api.v2.CommandTypeV2
import com.procurement.evaluation.infrastructure.api.v2.tryGetAction
import com.procurement.evaluation.infrastructure.api.v2.tryGetId
import com.procurement.evaluation.infrastructure.api.v2.tryGetVersion
import com.procurement.evaluation.infrastructure.handler.check.accesstoaward.CheckAccessToAwardHandler
import com.procurement.evaluation.infrastructure.handler.check.relatedtenderer.CheckRelatedTendererHandler
import com.procurement.evaluation.infrastructure.handler.close.awardperiod.CloseAwardPeriodHandler
import com.procurement.evaluation.infrastructure.handler.create.requirementresponsehandler.AddRequirementResponseHandler
import com.procurement.evaluation.infrastructure.handler.create.unsuccessfulaward.CreateUnsuccessfulAwardsHandler
import com.procurement.evaluation.infrastructure.handler.get.awardstatebyids.GetAwardStateByIdsHandler
import org.springframework.stereotype.Service

@Service
class CommandServiceV2(
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
            .onFailure {
                return generateResponseOnFailure(
                    fail = it.reason,
                    id = node.tryGetId().get,
                    version = node.tryGetVersion().get,
                    logger = logger
                )
            }

        return when (action) {
            CommandTypeV2.GET_AWARD_STATES_BY_IDS -> getAwardStateByIdsHandler.handle(node)

            CommandTypeV2.CHECK_ACCESS_TO_AWARD -> checkAccessToAwardHandler.handle(node)

            CommandTypeV2.CHECK_RELATED_TENDERER -> checkRelatedTendererHandler.handle(node)

            CommandTypeV2.ADD_REQUIREMENT_RESPONSE -> addRequirementResponseHandler.handle(node)

            CommandTypeV2.CREATE_UNSUCCESSFUL_AWARDS -> createUnsuccessfulAwardHandler.handle(node)

            CommandTypeV2.CLOSE_AWARD_PERIOD -> closeAwardPeriodHandler.handle(node)
        }
    }
}