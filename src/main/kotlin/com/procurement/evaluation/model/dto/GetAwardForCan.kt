package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonCreator

data class GetAwardForCanRs @JsonCreator constructor(
        val awardingSuccess: Boolean,
        val awardId: String?
)