package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.util.*

data class EvaluatedAward(
    val statusDetails: AwardStatusDetails,
    val relatedBid: UUID
)
