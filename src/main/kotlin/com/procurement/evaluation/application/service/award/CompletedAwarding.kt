package com.procurement.evaluation.application.service.award

import java.time.LocalDateTime

data class CompletedAwarding(val awardPeriod: AwardPeriod) {

    data class AwardPeriod(val endDate: LocalDateTime)
}
