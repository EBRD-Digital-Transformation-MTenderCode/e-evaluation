package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class Details @JsonCreator constructor(

        val scale: String
)
