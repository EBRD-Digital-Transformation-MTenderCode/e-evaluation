package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.Document

data class AwardByBidRq @JsonCreator constructor(

        val award: AwardByBid
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AwardByBid @JsonCreator constructor(

        var statusDetails: AwardStatusDetails,

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

        val lotAwarded: Boolean?,

        val bidAwarded: Boolean
)