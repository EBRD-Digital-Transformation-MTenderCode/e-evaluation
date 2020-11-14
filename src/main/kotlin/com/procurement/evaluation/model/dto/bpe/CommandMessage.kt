package com.procurement.evaluation.model.dto.bpe

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.config.properties.GlobalProperties
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.enums.OperationType
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.domain.util.extension.toLocalDateTime
import com.procurement.evaluation.exception.EnumException
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.api.command.id.CommandId
import com.procurement.evaluation.infrastructure.dto.Action
import com.procurement.evaluation.infrastructure.dto.ApiErrorResponse
import com.procurement.evaluation.model.dto.ocds.Phase
import java.time.LocalDateTime

data class CommandMessage @JsonCreator constructor(

    val id: CommandId,
    val command: CommandType,
    val context: Context,
    val data: JsonNode,
    val version: ApiVersion
)

val CommandMessage.commandId: CommandId
    get() = this.id

val CommandMessage.action: Action
    get() = this.command

val CommandMessage.cpid: Cpid
    get() = this.context.cpid
        ?.let {
            Cpid.tryCreateOrNull(it)
                ?: throw ErrorException(
                    error = ErrorType.INVALID_ATTRIBUTE,
                    message = "Cannot parse 'cpid' attribute '${it}'."
                )
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'cpid' attribute in context.")

val CommandMessage.ocid: Ocid
    get() = this.context.ocid
        ?.let {
            Ocid.tryCreateOrNull(it)
                ?: throw ErrorException(
                    error = ErrorType.INVALID_ATTRIBUTE,
                    message = "Cannot parse 'ocid' attribute '${it}'."
                )
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'ocid' attribute in context.")

val CommandMessage.token: Token
    get() = this.context.token?.let { id ->
        try {
            Token.fromString(id)
        } catch (exception: Exception) {
            throw ErrorException(error = ErrorType.INVALID_FORMAT_TOKEN)
        }
    } ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'token' attribute in context.")

val CommandMessage.owner: String
    get() = this.context.owner
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'owner' attribute in context.")

val CommandMessage.phase: Phase
    get() = this.context.phase
        ?.let { Phase.creator(it) }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'phase' attribute in context.")

val CommandMessage.country: String
    get() = this.context.country
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'country' attribute in context.")

val CommandMessage.pmd: ProcurementMethod
    get() = this.context.pmd?.let {
        ProcurementMethod.fromString(it)
    } ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'pmd' attribute in context.")

val CommandMessage.startDate: LocalDateTime
    get() = this.context.startDate
        ?.toLocalDateTime()
        ?.orThrow { it.reason }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'startDate' attribute in context.")

val CommandMessage.operationType: OperationType
    get() = this.context.operationType?.let {
        OperationType.creator(it)
    } ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'operationType' attribute in context.")

val CommandMessage.lotId: LotId
    get() = this.context.id?.let {
        try {
            LotId.fromString(it)
        } catch (exception: Exception) {
            throw ErrorException(error = ErrorType.INVALID_FORMAT_LOT_ID)
        }
    } ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'id' attribute in context.")

val CommandMessage.awardId: AwardId
    get() = this.context.id?.let {
        try {
            AwardId.fromString(it)
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

enum class CommandType(@JsonValue override val key: String): Action {

    AWARDS_CANCELLATION("awardsCancellation"),
    CHECK_AWARD_STATUS("checkAwardStatus"),
    CREATE_AWARD("createAward"),
    CREATE_AWARDS("createAwards"),
    CREATE_AWARDS_AUCTION_END("createAwardsAuctionEnd"),
    CREATE_UNSUCCESSFUL_AWARDS("createUnsuccessfulAwards"),
    END_AWARD_PERIOD("endAwardPeriod"),
    EVALUATE_AWARD("evaluateAward"),
    FINAL_AWARDS_STATUS_BY_LOTS("finalAwardsStatusByLots"),
    GET_AWARDS_FOR_AC("getAwardsForAc"),
    GET_AWARD_ID_FOR_CHECK("getAwardIdForCheck"),
    GET_EVALUATED_AWARDS("getEvaluatedAwards"),
    GET_LOT_FOR_CHECK("getLotForCheck"),
    GET_NEXT_AWARD("getNextAward"),
    GET_UNSUCCESSFUL_LOTS("getUnsuccessfulLots"),
    GET_WINNING_AWARD("getWinAward"),
    SET_AWARD_FOR_EVALUATION("setAwardForEvaluation"),
    START_AWARD_PERIOD("startAwardPeriod"),
    START_CONSIDERATION("startConsideration");

    fun value(): String {
        return this.key
    }

    override fun toString(): String {
        return this.key
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

fun errorResponseDto(exception: Exception, id: CommandId, version: ApiVersion): ApiErrorResponse =
    when (exception) {
        is ErrorException -> getApiResponse(
            id = id,
            version = version,
            code = exception.code,
            message = exception.message!!
        )
        is EnumException -> getApiResponse(
            id = id,
            version = version,
            code = exception.code,
            message = exception.message!!
        )
        else -> getApiResponse(id = id, version = version, code = "00.00", message = exception.message!!)
    }

private fun getApiResponse(id: CommandId, version: ApiVersion, code: String, message: String): ApiErrorResponse {
    return ApiErrorResponse(
        errors = listOf(
            ApiErrorResponse.Error(
                code = "400.${GlobalProperties.serviceId}." + code,
                description = message
            )
        ),
        id = id,
        version = version
    )
}