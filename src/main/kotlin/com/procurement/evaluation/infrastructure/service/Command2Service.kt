package com.procurement.evaluation.infrastructure.service

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.infrastructure.dto.ApiResponse2
import com.procurement.evaluation.model.dto.bpe.Command2Type
import com.procurement.evaluation.model.dto.bpe.tryGetAction
import org.springframework.stereotype.Service

@Service
class Command2Service {

    fun execute(node: JsonNode): ApiResponse2 {
        val action = node.tryGetAction().get

        return when (action) {
            Command2Type.GET_AWARD_STATES_BY_IDS -> {
                TODO()
            }
        }
    }
}