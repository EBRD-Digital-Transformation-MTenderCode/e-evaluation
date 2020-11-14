package com.procurement.evaluation.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.domain.model.enums.EnumElementProvider
import com.procurement.evaluation.infrastructure.api.command.id.CommandId
import java.time.LocalDateTime
import java.util.*

@JsonPropertyOrder("version", "id", "status", "result")
sealed class ApiResponse2(
    @field:JsonProperty("version") @param:JsonProperty("version") val version: ApiVersion,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: CommandId,
    @field:JsonProperty("result") @param:JsonProperty("result") val result: Any?
) {
    abstract val status: Response2Status
}

class ApiSuccessResponse2(
    version: ApiVersion, id: CommandId,
    @JsonInclude(JsonInclude.Include.NON_EMPTY) result: Any? = null
) : ApiResponse2(
    version = version,
    id = id,
    result = result
) {
    @field:JsonProperty("status")
    override val status: Response2Status = Response2Status.SUCCESS
}

class ApiErrorResponse2(
    version: ApiVersion, id: CommandId, result: List<Error>
) : ApiResponse2(version = version, result = result, id = id) {
    @field:JsonProperty("status")
    override val status: Response2Status = Response2Status.ERROR

    class Error(
        val code: String,
        val description: String,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) val details: List<Detail> = emptyList()
    ) {
        class Detail private constructor(
            @JsonInclude(JsonInclude.Include.NON_NULL) val name: String? = null,
            @JsonInclude(JsonInclude.Include.NON_NULL) val id: String? = null
        ) {
            companion object {
                fun tryCreateOrNull(id: String? = null, name: String? = null): Detail? =
                    if (id == null && name == null)
                        null
                    else
                        Detail(id = id, name = name)
            }
        }
    }
}

class ApiIncidentResponse2(version: ApiVersion, id: CommandId, result: Incident) :
    ApiResponse2(version = version, id = id, result = result) {

    @field:JsonProperty("status")
    override val status: Response2Status = Response2Status.INCIDENT

    class Incident(val id: UUID, val date: LocalDateTime, val service: Service, val details: List<Details>) {
        class Service(val id: String, val name: String, val version: String)
        class Details(val code: String, val description: String, val metadata: Any?)
    }
}

enum class Response2Status(@JsonValue override val key: String) : EnumElementProvider.Key {
    SUCCESS("success"),
    ERROR("error"),
    INCIDENT("incident");

    override fun toString(): String = key

    companion object : EnumElementProvider<Response2Status>(info = info()) {
        @JvmStatic
        @JsonCreator
        fun creator(name: String) = Response2Status.orThrow(name)
    }
}