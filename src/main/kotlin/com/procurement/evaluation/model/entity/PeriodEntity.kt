package com.procurement.evaluation.model.entity

import java.util.*

data class PeriodEntity(

        val cpId: String,

        val stage: String,

        val awardCriteria: String?,

        var startDate: Date?,

        var endDate: Date?

)
