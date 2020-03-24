package com.procurement.evaluation.infrastructure.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.infrastructure.dto.ApiResponse2
import com.procurement.evaluation.infrastructure.handler.CheckAccessToAwardHandler
import com.procurement.evaluation.infrastructure.handler.GetAwardStateByIdsHandler
import com.procurement.evaluation.model.dto.bpe.Command2Type
import com.procurement.evaluation.model.dto.bpe.tryGetAction
import org.springframework.stereotype.Service

@Service
class Command2Service(private val getAwardStateByIdsHandler: GetAwardStateByIdsHandler,
                      private val checkAccessToAwardHandler: CheckAccessToAwardHandler) {

    fun execute(node: JsonNode): ApiResponse2 {
        val action = node.tryGetAction().get

        return when (action) {
            Command2Type.GET_AWARD_STATES_BY_IDS -> {
                getAwardStateByIdsHandler.handle(node)
            }
            Command2Type.CHECK_ACCESS_TO_AWARD -> {
                checkAccessToAwardHandler.handle(node)
            }
        }
    }
}