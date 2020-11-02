package com.procurement.evaluation.infrastructure.dto.award.evaluate.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.evaluation.domain.model.document.DocumentId
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.infrastructure.bind.amount.AmountDeserializer
import com.procurement.evaluation.infrastructure.bind.amount.AmountSerializer
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.DocumentType
import java.math.BigDecimal

data class EvaluateAwardRequest(
    @field:JsonProperty("award") @param:JsonProperty("award") val award: Award
) {
    data class Award(
        @field:JsonProperty("statusDetails") @param:JsonProperty("statusDetails") val statusDetails: AwardStatusDetails,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("value") @param:JsonProperty("value") val value: Value?
    ) {
        data class Document(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: DocumentId,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<LotId>?,
            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: DocumentType
        )
        data class Value(
            @JsonDeserialize(using = AmountDeserializer::class)
            @JsonSerialize(using = AmountSerializer::class)
            @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: BigDecimal
        )
    }
}
