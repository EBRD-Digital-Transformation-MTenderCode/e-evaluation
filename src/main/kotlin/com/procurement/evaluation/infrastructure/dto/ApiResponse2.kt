package com.procurement.evaluation.infrastructure.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import com.fasterxml.jackson.annotation.JsonValue
import com.procurement.evaluation.domain.model.enums.EnumElementProvider
import java.time.LocalDateTime
import java.util.*

@JsonPropertyOrder("version", "id", "status", "result")
sealed class ApiResponse2(
    @field:JsonProperty("version") @param:JsonProperty("version") val version: ApiVersion2,
    @field:JsonProperty("id") @param:JsonProperty("id") val id: UUID,
    @field:JsonProperty("result") @param:JsonProperty("result") val result: Any?
) {
    abstract val status: Response2Status
}

class ApiSuccessResponse2(
    version: ApiVersion2, id: UUID,
    @JsonInclude(JsonInclude.Include.NON_EMPTY) result: Any? = null
) : ApiResponse2(
    version = version,
    id = id,
    result = result
) {
    @field:JsonProperty("status")
    override val status: Response2Status = Response2Status.SUCCESS
}

class ApiFailResponse2(version: ApiVersion2, id: UUID, result: List<Error>) :
    ApiResponse2(version = version, id = id, result = result) {

    @field:JsonProperty("status")
    override val status: Response2Status = Response2Status.ERROR

    class Error(val code: String, val description: String)
}

class ApiIncidentResponse2(version: ApiVersion2, id: UUID, result: Incident) :
    ApiResponse2(version = version, id = id, result = result) {

    @field:JsonProperty("status")
    override val status: Response2Status = Response2Status.INCIDENT

    class Incident(val id: UUID, val date: LocalDateTime, val service: Service, val details: List<Details>) {
        class Service(val id: String, val name: String, val version: String)
        class Details(val code: String, val description: String, val metadata: Any?)
    }
}

class ApiDataErrorResponse2(
    version: ApiVersion2, id: UUID, result: List<Error>
) : ApiResponse2(version = version, result = result, id = id) {
    @field:JsonProperty("status")
    override val status: Response2Status = Response2Status.ERROR

    class Error(val code: String, val description: String, val details: List<Detail>) {
        class Detail(val name: String)
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