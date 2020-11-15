package com.procurement.evaluation.infrastructure.handler.v2.base

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.infrastructure.api.ApiVersion
import com.procurement.evaluation.infrastructure.extension.tryGetAttribute
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.fail.error.BadRequest
import com.procurement.evaluation.infrastructure.handler.Handler
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asFailure
import com.procurement.evaluation.lib.functional.flatMap
import com.procurement.evaluation.utils.tryToObject

abstract class AbstractHandlerV2<R : Any> : Handler<R> {

    final override val version: ApiVersion
        get() = ApiVersion(2, 0, 0)

    inline fun <reified T : Any> JsonNode.params() = params(T::class.java)

    fun <T : Any> JsonNode.params(target: Class<T>): Result<T, Failure.Error> {
        val name = "params"
        return tryGetAttribute(name)
            .flatMap {
                when (val result = it.tryToObject(target)) {
                    is Result.Success -> result
                    is Result.Failure -> BadRequest("Error parsing '$name'", result.reason.exception)
                        .asFailure()
                }
            }
    }
}
