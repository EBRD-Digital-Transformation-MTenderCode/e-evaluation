package com.procurement.evaluation.infrastructure.api.v2

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.infrastructure.api.ApiVersion
import com.procurement.evaluation.infrastructure.api.command.id.CommandId
import com.procurement.evaluation.infrastructure.extension.tryGetAttribute
import com.procurement.evaluation.infrastructure.extension.tryGetAttributeAsEnum
import com.procurement.evaluation.infrastructure.extension.tryGetTextAttribute
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.fail.error.BadRequest
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asFailure
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.lib.functional.flatMap
import com.procurement.evaluation.utils.tryToObject

fun JsonNode.tryGetVersion(): Result<ApiVersion, DataErrors> {
    val name = "version"
    return tryGetTextAttribute(name)
        .flatMap { version ->
            ApiVersion.orNull(version)
                ?.asSuccess<ApiVersion, DataErrors>()
                ?: DataErrors.Validation.DataFormatMismatch(
                    name = name,
                    expectedFormat = ApiVersion.pattern,
                    actualValue = version
                ).asFailure()
        }
}

fun JsonNode.tryGetAction(): Result<CommandTypeV2, DataErrors> = tryGetAttributeAsEnum("action", CommandTypeV2)

fun <T : Any> JsonNode.tryGetParams(target: Class<T>): Result<T, Fail.Error> {
    val name = "params"
    return tryGetAttribute(name)
        .flatMap {
            when (val result = it.tryToObject(target)) {
                is Result.Success -> result
                is Result.Failure -> Result.failure(
                    BadRequest("Error parsing '$name'", result.reason.exception)
                )
            }
        }
}

fun JsonNode.tryGetId(): Result<CommandId, DataErrors> = tryGetTextAttribute("id").map { CommandId(it) }
