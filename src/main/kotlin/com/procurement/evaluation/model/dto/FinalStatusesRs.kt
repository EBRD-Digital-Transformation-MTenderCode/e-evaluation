package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.Period

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FinalStatusesRs(

        val awards: List<FinalAward>,

        val awardPeriod: Period
)

data class FinalAward(

        val id: String,

        val status: AwardStatus,

        val statusDetails: AwardStatusDetails
)
