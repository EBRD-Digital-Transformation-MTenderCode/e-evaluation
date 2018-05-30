package com.procurement.evaluation.model.entity

import java.util.*

//@Getter
//@Setter
//@Table("evaluation_award")
data class AwardEntity(

//    @PrimaryKeyColumn(name = "cp_id", type = PrimaryKeyType.PARTITIONED)
        val cpId: String,

//    @PrimaryKeyColumn(name = "stage", type = PrimaryKeyType.CLUSTERED)
        val stage: String,

//    @PrimaryKeyColumn(value = "token_entity", type = PrimaryKeyType.CLUSTERED)
        val token: UUID,

//    @Column(value = "status")
        val status: String,

//    @Column(value = "status_details")
        var statusDetails: String,

//    @Column(value = "owner")
        val owner: String,

//    @Column(value = "json_data")
        var jsonData: String
)
