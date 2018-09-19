package com.procurement.evaluation.model.dto.bpe

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.exception.EnumException
import com.procurement.evaluation.exception.ErrorException

data class CommandMessage @JsonCreator constructor(

        val id: String,
        val command: CommandType,
        val context: Context,
        val data: JsonNode,
        val version: ApiVersion
)

data class Context @JsonCreator constructor(
        val operationId: String,
        val requestId: String?,
        val cpid: String?,
        val ocid: String?,
        val stage: String?,
        val prevStage: String?,
        val processType: String?,
        val operationType: String?,
        val phase: String?,
        val owner: String?,
        val country: String?,
        val language: String?,
        val pmd: String?,
        val token: String?,
        val startDate: String?,
        val endDate: String?,
        val id: String?
)

enum class CommandType(private val value: String) {

    CREATE_AWARDS("createAwards"),
    AWARD_BY_BID("awardByBid"),
    AWARDS_FOR_CANS("AwardsForCans"),
    SET_FINAL_STATUSES("setFinalStatuses"),
    PREPARE_CANCELLATION("prepareCancellation"),
    AWARDS_CANCELLATION("awardsCancellation");

    @JsonValue
    fun value(): String {
        return this.value
    }

    override fun toString(): String {
        return this.value
    }
}

enum class ApiVersion(private val value: String) {
    V_0_0_1("0.0.1");

    @JsonValue
    fun value(): String {
        return this.value
    }

    override fun toString(): String {
        return this.value
    }
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseDto(

        val errors: List<ResponseErrorDto>? = null,
        val data: Any? = null,
        val id: String? = null
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ResponseErrorDto(

        val code: String,
        val description: String?
)

fun getExceptionResponseDto(exception: Exception): ResponseDto {
    return ResponseDto(
            errors = listOf(ResponseErrorDto(
                    code = "400.07.00",
                    description = exception.message
            )))
}

fun getErrorExceptionResponseDto(error: ErrorException, id: String? = null): ResponseDto {
    return ResponseDto(
            errors = listOf(ResponseErrorDto(
                    code = "400.07." + error.code,
                    description = error.msg
            )),
            id = id)
}

fun getEnumExceptionResponseDto(error: EnumException, id: String? = null): ResponseDto {
    return ResponseDto(
            errors = listOf(ResponseErrorDto(
                    code = "400.07." + error.code,
                    description = error.msg
            )),
            id = id)
}