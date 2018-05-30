package com.procurement.evaluation.model.entity

//@Table("evaluation_rules")
class RulesEntity(

//    @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED)
        val country: String,

//    @PrimaryKeyColumn(name = "pmd", type = PrimaryKeyType.CLUSTERED)
        val method: String,

//    @PrimaryKeyColumn(name = "parameter", type = PrimaryKeyType.CLUSTERED)
        val parameter: String,

//    @Column("value")
        val value: String
)


