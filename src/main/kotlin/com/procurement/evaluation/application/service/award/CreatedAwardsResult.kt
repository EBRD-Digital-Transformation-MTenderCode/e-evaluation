package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.award.AwardId

data class CreatedAwardsResult(
    val awards: List<Award>
) {
    data class Award(
        val token: Token,
        val id: AwardId
    )
}
