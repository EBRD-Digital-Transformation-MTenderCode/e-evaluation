package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator

data class Lot @JsonCreator constructor(

        val id: String
)