package com.procurement.evaluation.infrastructure.handler.v2.base

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.application.service.Transform
import com.procurement.evaluation.application.service.tryDeserialization
import com.procurement.evaluation.infrastructure.api.v2.ApiResponseV2
import com.procurement.evaluation.infrastructure.api.v2.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.handler.HistoryRepository
import com.procurement.evaluation.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.utils.toJson

abstract class AbstractHistoricalHandlerV2<R : Any>(
    private val transform: Transform,
    private val historyRepository: HistoryRepository,
    private val logger: Logger
) : AbstractHandlerV2<ApiResponseV2>() {

    override fun handle(descriptor: CommandDescriptor): ApiResponseV2 {
        val history = historyRepository.getHistory(descriptor.id, action)
            .onFailure {
                return generateResponseOnFailure(
                    fail = it.reason,
                    version = version,
                    id = descriptor.id,
                    logger = logger
                )
            }

        if (history != null) {
            return history.tryDeserialization<ApiResponseV2.Success>(transform)
                .onFailure {
                    return generateResponseOnFailure(
                        fail = Failure.Incident.Transform.ParseFromDatabaseIncident(history, it.reason.exception),
                        id = descriptor.id,
                        version = version,
                        logger = logger
                    )
                }
        }

        return when (val result = execute(descriptor)) {
            is Result.Success -> {
                ApiResponseV2.Success(version = version, id = descriptor.id, result = result.get)
                    .also {
                        historyRepository.saveHistory(descriptor.id, action, toJson(it))
                        if (logger.isDebugEnabled)
                            logger.debug("${action.key} has been executed. Response: ${toJson(it)}")
                    }
            }
            is Result.Failure ->
                generateResponseOnFailure(fail = result.reason, version = version, id = descriptor.id, logger = logger)
        }
    }

    abstract fun execute(descriptor: CommandDescriptor): Result<R, Failure>
}

