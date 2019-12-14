package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.model.dto.ocds.AwardCriteria
import java.time.LocalDateTime

data class CreateAwardsAuctionEndContext (
    val cpid: String,
    val stage: String,
    val owner: String,
    val awardCriteria: AwardCriteria,
    val startDate: LocalDateTime
)