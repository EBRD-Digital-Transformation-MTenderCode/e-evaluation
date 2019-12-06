package com.procurement.evaluation.infrastructure.dto.award.evaluate.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.annotation.Nulls
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.domain.model.document.DocumentId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.domain.model.money.Money
import com.procurement.evaluation.infrastructure.bind.date.JsonDateTimeDeserializer
import com.procurement.evaluation.infrastructure.bind.date.JsonDateTimeSerializer
import com.procurement.evaluation.infrastructure.bind.money.MoneyDeserializer
import com.procurement.evaluation.infrastructure.bind.money.MoneySerializer
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.DocumentType
import java.time.LocalDateTime

data class EvaluateAwardResponse(
    @field:JsonProperty("award") @param:JsonProperty("award") val award: Award
) {
    data class Award(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: AwardId,

        @JsonDeserialize(using = JsonDateTimeDeserializer::class)
        @JsonSerialize(using = JsonDateTimeSerializer::class)
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

        @field:JsonProperty("status") @param:JsonProperty("status") val status: AwardStatus,
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,
        @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId>,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("relatedBid") @param:JsonProperty("relatedBid") val relatedBid: BidId?,

        @JsonDeserialize(using = MoneyDeserializer::class)
        @JsonSerialize(using = MoneySerializer::class)
        @field:JsonProperty("value") @param:JsonProperty("value") val value: Money,

        @field:JsonProperty("suppliers") @param:JsonProperty("suppliers") val suppliers: List<Supplier>,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @JsonSetter(nulls = Nulls.AS_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document> = emptyList()
    ) {

        data class Supplier(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String
        )

        data class Document(
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType,
            @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @JsonSetter(nulls = Nulls.AS_EMPTY)
            @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId> = emptyList()
        )
    }
}
