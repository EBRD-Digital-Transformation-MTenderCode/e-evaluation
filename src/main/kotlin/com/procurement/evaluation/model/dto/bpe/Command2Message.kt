package com.procurement.evaluation.model.dto.bpe

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.application.service.Logger
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.bind
import com.procurement.evaluation.domain.model.enums.EnumElementProvider
import com.procurement.evaluation.domain.util.extension.nowDefaultUTC
import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.configuration.properties.GlobalProperties2
import com.procurement.evaluation.infrastructure.dto.Action
import com.procurement.evaluation.infrastructure.dto.ApiDataErrorResponse2
import com.procurement.evaluation.infrastructure.dto.ApiFailResponse2
import com.procurement.evaluation.infrastructure.dto.ApiIncidentResponse2
import com.procurement.evaluation.infrastructure.dto.ApiResponse2
import com.procurement.evaluation.infrastructure.dto.ApiVersion2
import com.procurement.evaluation.infrastructure.extension.tryGetAttribute
import com.procurement.evaluation.infrastructure.extension.tryGetAttributeAsEnum
import com.procurement.evaluation.infrastructure.extension.tryGetTextAttribute
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.fail.error.BadRequest
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.utils.tryToNode
import com.procurement.evaluation.utils.tryToObject
import java.util.*

enum class Command2Type(@JsonValue override val key: String) : Action, EnumElementProvider.Key {

    GET_AWARD_STATES_BY_IDS("getAwardStateByIds"),
    CHECK_ACCESS_TO_AWARD("checkAccessToAward"),
    CHECK_RELATED_TENDERER("checkRelatedTenderer"),
    CREATE_REQUIREMENT_RESPONSE("createRequirementResponse");

    override fun toString(): String = key

    companion object : EnumElementProvider<Command2Type>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = Command2Type.orThrow(name)
    }
}

fun generateResponseOnFailure(
    fail: Fail,
    version: ApiVersion2,
    id: UUID,
    logger: Logger
): ApiResponse2 {
    fail.logging(logger)
    return when (fail) {
        is Fail.Error -> {
            when (fail) {
                is DataErrors.Validation ->
                    ApiDataErrorResponse2(
                        version = version,
                        id = id,
                        result = listOf(
                            ApiDataErrorResponse2.Error(
                                code = getFullErrorCode(fail.code),
                                description = fail.description,
                                details = listOf(
                                    ApiDataErrorResponse2.Error.Detail(name = fail.name)
                                )
                            )
                        )
                    )
                else -> ApiFailResponse2(
                    version = version,
                    id = id,
                    result = listOf(
                        ApiFailResponse2.Error(
                            code = getFullErrorCode(fail.code),
                            description = fail.description
                        )
                    )
                )
            }
        }
        is Fail.Incident -> {
            val errors = listOf(
                ApiIncidentResponse2.Incident.Details(
                    code = getFullErrorCode(fail.code),
                    description = fail.description,
                    metadata = null
                )
            )
            generateIncident(errors, version, id)
        }
    }
}

private fun generateIncident(
    details: List<ApiIncidentResponse2.Incident.Details>, version: ApiVersion2, id: UUID
): ApiIncidentResponse2 =
    ApiIncidentResponse2(
        version = version,
        id = id,
        result = ApiIncidentResponse2.Incident(
            date = nowDefaultUTC(),
            id = UUID.randomUUID(),
            service = ApiIncidentResponse2.Incident.Service(
                id = GlobalProperties2.service.id,
                version = GlobalProperties2.service.version,
                name = GlobalProperties2.service.name
            ),
            details = details
        )
    )

fun getFullErrorCode(code: String): String = "${code}/${GlobalProperties2.service.id}"

val NaN: UUID
    get() = UUID(0, 0)

fun JsonNode.tryGetVersion(): Result<ApiVersion2, DataErrors> {
    val name = "version"
    return tryGetTextAttribute(name).bind {
        when (val result = ApiVersion2.tryValueOf(it)) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                DataErrors.Validation.DataFormatMismatch(
                    name = name,
                    expectedFormat = "00.00.00",
                    actualValue = it
                )
            )
        }
    }
}

fun JsonNode.tryGetAction(): Result<Command2Type, DataErrors> =
    tryGetAttributeAsEnum("action", Command2Type)

fun <T : Any> JsonNode.tryGetParams(target: Class<T>): Result<T, Fail.Error> {
    val name = "params"
    return tryGetAttribute(name).bind {
        when (val result = it.tryToObject(target)) {
            is Result.Success -> result
            is Result.Failure -> Result.failure(
                BadRequest("Error parsing '$name'", result.error.exception)
            )
        }
    }
}

fun JsonNode.tryGetId(): Result<UUID, DataErrors> {
    val name = "id"
    return tryGetTextAttribute(name)
        .bind {
            when (val result = it.tryUUID()) {
                is Result.Success -> result
                is Result.Failure -> Result.failure(
                    DataErrors.Validation.DataFormatMismatch(
                        name = name,
                        actualValue = it,
                        expectedFormat = "uuid"
                    )
                )
            }
        }
}

fun String.tryGetNode(): Result<JsonNode, BadRequest> =
    when (val result = this.tryToNode()) {
        is Result.Success -> result
        is Result.Failure -> Result.failure(BadRequest(exception = result.error.exception))
    }

