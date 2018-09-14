package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.Document
import com.procurement.evaluation.model.dto.ocds.Status
import javax.validation.Valid

data class AwardByBidRq @JsonCreator constructor(

        @field:Valid
        val award: AwardByBid
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AwardByBid @JsonCreator constructor(

        var statusDetails: Status,

        var description: String?,

        var documents: List<Document>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AwardByBidRs(

        val award: Award,

        val nextAwardForUpdate: Award?,

        val awardStatusDetails: String?,

        val bidId: String?,

        val lotId: String?,

        val lotAwarded: Boolean?
)