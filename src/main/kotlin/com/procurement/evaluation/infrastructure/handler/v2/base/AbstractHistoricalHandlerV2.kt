package com.procurement.evaluation.infrastructure.handler.v2.base

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.Transform
import com.procurement.evaluation.application.service.tryDeserialization
import com.procurement.evaluation.infrastructure.api.v2.ApiResponse2
import com.procurement.evaluation.infrastructure.api.v2.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.evaluation.infrastructure.api.v2.ApiSuccessResponse2
import com.procurement.evaluation.infrastructure.api.v2.tryGetId
import com.procurement.evaluation.infrastructure.api.v2.tryGetVersion
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.handler.HistoryRepository
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.utils.toJson

abstract class AbstractHistoricalHandlerV2<R : Any>(
    private val transform: Transform,
    private val historyRepository: HistoryRepository,
    private val logger: Logger
) : AbstractHandlerV2<ApiResponse2>() {

    override fun handle(node: JsonNode): ApiResponse2 {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        val history = historyRepository.getHistory(id, action)
            .onFailure {
                return generateResponseOnFailure(fail = it.reason, version = version, id = id, logger = logger)
            }

        if (history != null) {
            return history.tryDeserialization<ApiSuccessResponse2>(transform)
                .onFailure {
                    return generateResponseOnFailure(
                        fail = Fail.Incident.Transform.ParseFromDatabaseIncident(history, it.reason.exception),
                        id = id,
                        version = version,
                        logger = logger
                    )
                }
        }

        return when (val result = execute(node)) {
            is Result.Success -> {
                ApiSuccessResponse2(version = version, id = id, result = result.get)
                    .also {
                        historyRepository.saveHistory(id, action, toJson(it))
                        if (logger.isDebugEnabled)
                            logger.debug("${action.key} has been executed. Response: ${toJson(it)}")
                    }
            }
            is Result.Failure ->
                generateResponseOnFailure(fail = result.reason, version = version, id = id, logger = logger)
        }
    }

    abstract fun execute(node: JsonNode): Result<R, Fail>
}

