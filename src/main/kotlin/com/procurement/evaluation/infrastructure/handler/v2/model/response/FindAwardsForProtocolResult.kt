package com.procurement.evaluation.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonProperty
import com.procurement.evaluation.model.dto.ocds.OrganizationReference
import com.procurement.evaluation.model.dto.ocds.Award as DomainAward

data class FindAwardsForProtocolResult(
    @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award>
) {
    data class Award(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
        @field:JsonProperty("suppliers") @param:JsonProperty("suppliers") val suppliers: List<Supplier>,
    ) {

        data class Supplier(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
        )
    }

    object ResponseConverter {

        fun fromDomain(award: DomainAward): Award =
            Award(
                id = award.id,
                suppliers = award.suppliers.orEmpty().map { it.fromDomain() }
            )

        fun OrganizationReference.fromDomain(): Award.Supplier =
            Award.Supplier(id = id, name = name)

    }
}
