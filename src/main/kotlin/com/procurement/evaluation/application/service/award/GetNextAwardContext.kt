package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.award.AwardId

data class GetNextAwardContext(
    val cpid: String,
    val stage: String,
    val awardId: AwardId
)
