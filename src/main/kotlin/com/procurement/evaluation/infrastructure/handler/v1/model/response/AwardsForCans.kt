package com.procurement.evaluation.infrastructure.handler.v1.model.response

import com.fasterxml.jackson.annotation.JsonCreator

data class AwardForCansRs @JsonCreator constructor(

        val awardId: String
)