package com.procurement.evaluation.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.domain.model.money.Money
import com.procurement.evaluation.infrastructure.bind.date.JsonDateTimeDeserializer
import com.procurement.evaluation.infrastructure.bind.date.JsonDateTimeSerializer
import com.procurement.evaluation.infrastructure.bind.money.MoneyDeserializer
import com.procurement.evaluation.infrastructure.bind.money.MoneySerializer
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.time.LocalDateTime

data class SetAwardForEvaluationResponse(

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award>
) {

    data class Award(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

        @JsonDeserialize(using = JsonDateTimeDeserializer::class)
        @JsonSerialize(using = JsonDateTimeSerializer::class)
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,

        @field:JsonProperty("status") @param:JsonProperty("status") val status: AwardStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId>,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: BidId?,

        @JsonDeserialize(using = MoneyDeserializer::class)
        @JsonSerialize(using = MoneySerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("value") @param:JsonProperty("value") val value: Money?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("suppliers") @param:JsonProperty("suppliers") val suppliers: List<Supplier> = emptyList(),

        @JsonDeserialize(using = MoneyDeserializer::class)
        @JsonSerialize(using = MoneySerializer::class)
        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("weightedValue") @param:JsonProperty("weightedValue") val weightedValue: Money?
    ) {

        data class Supplier(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String
        )
    }
}
