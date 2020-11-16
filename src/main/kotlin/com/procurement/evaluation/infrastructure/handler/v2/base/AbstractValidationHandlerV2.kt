package com.procurement.evaluation.infrastructure.handler.v2.base

import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.infrastructure.api.v2.ApiResponseV2
import com.procurement.evaluation.infrastructure.api.v2.ApiResponseV2Generator.generateResponseOnFailure
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.handler.v2.model.CommandDescriptor
import com.procurement.evaluation.lib.functional.Validated

abstract class AbstractValidationHandlerV2(private val logger: Logger) : AbstractHandlerV2<ApiResponseV2>() {

    override fun handle(descriptor: CommandDescriptor): ApiResponseV2 =
        when (val result = execute(descriptor)) {
            is Validated.Ok -> {
                if (logger.isDebugEnabled)
                    logger.debug("${action.key} has been executed.")
                ApiResponseV2.Success(version = version, id = descriptor.id)
            }

            is Validated.Error ->
                generateResponseOnFailure(fail = result.reason, version = version, id = descriptor.id, logger = logger)
        }

    abstract fun execute(descriptor: CommandDescriptor): Validated<Failure>
}
