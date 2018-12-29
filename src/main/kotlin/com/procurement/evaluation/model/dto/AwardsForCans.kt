package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator

data class AwardForCansRs @JsonCreator constructor(

        val awardId: String
)