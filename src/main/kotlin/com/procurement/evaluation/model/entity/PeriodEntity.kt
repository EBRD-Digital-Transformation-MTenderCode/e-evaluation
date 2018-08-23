package com.procurement.evaluation.model.entity

import java.util.*

//@Table("evaluation_period")
data class PeriodEntity(

//    @PrimaryKeyColumn(name = "cp_id", type = PrimaryKeyType.PARTITIONED)
    val cpId: String,

//    @PrimaryKeyColumn(name = "stage", type = PrimaryKeyType.CLUSTERED)
    val stage: String,

    val awardCriteria: String,

//    @Column(value = "start_date")
    var startDate: Date,

//    @Column(value = "end_date")
    var endDate: Date?

)
