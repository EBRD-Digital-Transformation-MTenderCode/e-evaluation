package com.procurement.evaluation.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.infrastructure.dto.Action
import com.procurement.evaluation.infrastructure.dto.ApiResponse2
import com.procurement.evaluation.infrastructure.dto.ApiSuccessResponse2
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.model.dto.bpe.generateResponseOnFailure
import com.procurement.evaluation.model.dto.bpe.tryGetId
import com.procurement.evaluation.model.dto.bpe.tryGetVersion
import com.procurement.evaluation.utils.toJson

abstract class AbstractHandler<ACTION : Action, R : Any>(private val logger: Logger) : Handler<ACTION, ApiResponse2> {

    override fun handle(node: JsonNode): ApiResponse2 {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        return when (val result = execute(node)) {
            is Result.Success -> {
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed. Result: ${toJson(result.get)}")
                return ApiSuccessResponse2(version = version, id = id, result = result.get)
            }
            is Result.Failure -> generateResponseOnFailure(
                fail = result.reason,
                version = version,
                id = id,
                logger = logger
            )
        }
    }

    abstract fun execute(node: JsonNode): Result<R, Fail>
}