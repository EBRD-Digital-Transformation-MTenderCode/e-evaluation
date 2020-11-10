package com.procurement.evaluation.model.dto

import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails

data class FinalAward(

        val id: String,

        val status: AwardStatus,

        val statusDetails: AwardStatusDetails
)
