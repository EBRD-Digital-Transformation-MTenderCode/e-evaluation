package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*
import javax.validation.Valid
import javax.validation.constraints.NotNull

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrganizationReference @JsonCreator constructor(

        val id: String,

        val name: String,

        val identifier: Identifier?,

        val address: Address?,

        val additionalIdentifiers: HashSet<Identifier>?,

        val contactPoint: ContactPoint?,

        var details: Details?
)