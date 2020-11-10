package com.procurement.evaluation.model.entity

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import java.time.LocalDateTime

data class PeriodEntity(
    val cpid: Cpid,
    val ocid: Ocid,
    val awardCriteria: String?,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?
)
