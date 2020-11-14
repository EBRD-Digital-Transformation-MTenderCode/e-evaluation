package com.procurement.evaluation.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.repository.history.HistoryRepository
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.infrastructure.dto.Action
import com.procurement.evaluation.infrastructure.dto.ApiResponse2
import com.procurement.evaluation.infrastructure.dto.ApiSuccessResponse2
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.model.dto.bpe.generateResponseOnFailure
import com.procurement.evaluation.model.dto.bpe.tryGetId
import com.procurement.evaluation.model.dto.bpe.tryGetVersion
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.tryToObject

abstract class AbstractHistoricalHandler<ACTION : Action, R : Any>(
    private val target: Class<ApiSuccessResponse2>,
    private val historyRepository: HistoryRepository,
    private val logger: Logger
) : Handler<ACTION, ApiResponse2> {

    override fun handle(node: JsonNode): ApiResponse2 {
        val id = node.tryGetId().get
        val version = node.tryGetVersion().get

        val history = historyRepository.getHistory(id.toString(), action)
            .doOnError { error ->
                return generateResponseOnFailure(
                    fail = error,
                    version = version,
                    id = id,
                    logger = logger
                )
            }
            .get
        if (history != null) {
            val data = history
            return data.tryToObject(target)
                .onFailure {
                    return generateResponseOnFailure(
                        fail = Fail.Incident.Transform.ParseFromDatabaseIncident(data, it.reason.exception),
                        id = id,
                        version = version,
                        logger = logger
                    )
                }
        }

        return when (val result = execute(node)) {
            is Result.Success -> {
                ApiSuccessResponse2(version = version, id = id, result = result.get).also {
                    historyRepository.saveHistory(id.toString(), action, it)
                    if (logger.isDebugEnabled)
                        logger.debug("${action.key} has been executed. Response: ${toJson(it)}")
                }
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

