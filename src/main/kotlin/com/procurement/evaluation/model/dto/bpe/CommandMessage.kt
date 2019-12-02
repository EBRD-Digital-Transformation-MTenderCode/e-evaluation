package com.procurement.evaluation.model.dto.bpe

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.exception.EnumException
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.tools.toLocalDateTime
import com.procurement.evaluation.model.dto.ocds.Phase
import java.time.LocalDateTime
import java.util.*

data class CommandMessage @JsonCreator constructor(

        val id: String,
        val command: CommandType,
        val context: Context,
        val data: JsonNode,
        val version: ApiVersion
)

val CommandMessage.cpid: String
    get() = this.context.cpid
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'cpid' attribute in context.")

val CommandMessage.ocid: String
    get() = this.context.ocid
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'ocid' attribute in context.")

val CommandMessage.token: UUID
    get() = this.context.token?.let { id ->
        try {
            UUID.fromString(id)
        } catch (exception: Exception) {
            throw ErrorException(error = ErrorType.INVALID_FORMAT_TOKEN)
        }
    } ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'token' attribute in context.")

val CommandMessage.owner: String
    get() = this.context.owner
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'owner' attribute in context.")

val CommandMessage.stage: String
    get() = this.context.stage
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'stage' attribute in context.")

val CommandMessage.phase: Phase
    get() = this.context.phase?.let{
        Phase.fromValue(it)
    } ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'phase' attribute in context.")

val CommandMessage.country: String
    get() = this.context.country
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'country' attribute in context.")

val CommandMessage.pmd: ProcurementMethod
    get() = this.context.pmd?.let {
        ProcurementMethod.fromString(it)
    } ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'pmd' attribute in context.")

val CommandMessage.startDate: LocalDateTime
    get() = this.context.startDate?.toLocalDateTime()
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'startDate' attribute in context.")

val CommandMessage.operationType: String
    get() = this.context.operationType
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'operationType' attribute in context.")

val CommandMessage.lotId: UUID
    get() = this.context.id?.let {
        try {
            UUID.fromString(id)
        } catch (exception: Exception) {
            throw ErrorException(error = ErrorType.INVALID_FORMAT_LOT_ID)
        }
    } ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'id' attribute in context.")

val CommandMessage.awardId: UUID
    get() = this.context.id?.let {
        try {
            UUID.fromString(id)
        } catch (exception: Exception) {
            throw ErrorException(error = ErrorType.INVALID_FORMAT_AWARD_ID)
        }
    } ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'id' attribute in context.")

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
        val id: String?,
        val awardCriteria: String?
)

enum class CommandType(private val value: String) {

    CREATE_AWARD("createAward"),
    EVALUATE_AWARD("evaluateAward"),
    CREATE_AWARDS("createAwards"),
    AWARD_BY_BID("awardByBid"),
    SET_FINAL_STATUSES("setFinalStatuses"),
    PREPARE_CANCELLATION("prepareCancellation"),
    AWARDS_CANCELLATION("awardsCancellation"),
    CREATE_AWARDS_BY_LOT_AUCTION("createAwardsByLotAuction"),
    CREATE_AWARDS_AUCTION("createAwardsAuction"),
    CREATE_AWARDS_AUCTION_END("createAwardsAuctionEnd"),
    CHECK_AWARD_VALUE("checkAwardValue"),
    END_AWARD_PERIOD("endAwardPeriod"),
    SET_INITIAL_AWARDS_STATUS("setInitialAwardsStatus"),
    GET_WINNING_AWARD("getWinAward"),
    GET_EVALUATED_AWARDS("getEvaluatedAwards"),
    GET_AWARDS_FOR_AC("getAwardsForAc"),
    GET_LOT_FOR_CHECK("getLotForCheck"),
    GET_AWARD_ID_FOR_CHECK("getAwardIdForCheck"),
    FINAL_AWARDS_STATUS_BY_LOTS("finalAwardsStatusByLots"),
    COMPLETE_AWARDING("completeAwarding"),
    GET_UNSUCCESSFUL_LOTS("getUnsuccessfulLots"),
    SET_AWARD_FOR_EVALUATION("setAwardForEvaluation"),
    START_AWARD_PERIOD("startAwardPeriod"),
    CREATE_UNSUCCESSFUL_AWARDS("createUnsuccessfulAwards");

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