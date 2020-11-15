package com.procurement.evaluation.infrastructure.handler.v2.base

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.infrastructure.api.ApiVersion
import com.procurement.evaluation.infrastructure.api.v2.ApiResponse2
import com.procurement.evaluation.infrastructure.api.v2.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.evaluation.infrastructure.api.v2.ApiSuccessResponse2
import com.procurement.evaluation.infrastructure.api.v2.tryGetId
import com.procurement.evaluation.infrastructure.api.v2.tryGetVersion
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.Handler
import com.procurement.evaluation.lib.functional.Validated

abstract class AbstractValidationHandlerV2<E : Fail>(private val logger: Logger) : Handler<ApiResponse2> {

    final override val version: ApiVersion
        get() = ApiVersion(2, 0, 0)

    override fun handle(node: JsonNode): ApiResponse2 {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        return when (val result = execute(node)) {
            is Validated.Ok -> {
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed.")
                ApiSuccessResponse2(version = version, id = id)
            }

            is Validated.Error ->
                generateResponseOnFailure(fail = result.reason, version = version, id = id, logger = logger)
        }
    }

    abstract fun execute(node: JsonNode): Validated<E>
}
