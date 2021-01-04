package com.procurement.evaluation.infrastructure.handler.v2.model.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class CheckAwardsStateRequest(
    @param:JsonProperty("cpid") @field:JsonProperty("cpid") val cpid: String,
    @param:JsonProperty("ocid") @field:JsonProperty("ocid") val ocid: String,
    @param:JsonProperty("pmd") @field:JsonProperty("pmd") val pmd: String,
    @param:JsonProperty("country") @field:JsonProperty("country") val country: String,
    @param:JsonProperty("operationType") @field:JsonProperty("operationType") val operationType: String,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @param:JsonProperty("awards") @field:JsonProperty("awards") val awards: List<Award>?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @param:JsonProperty("tender") @field:JsonProperty("tender") val tender: Tender?
) {
    data class Award(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
    )

    data class Tender(
        @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>
    ) {
        data class Lot(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String
        )
    }
}
