package com.procurement.evaluation.model.entity

import java.util.*


data class AwardEntity(

        val cpId: String,

        val stage: String,

        val token: UUID,

        val status: String,

        var statusDetails: String,

        val owner: String,

        var jsonData: String
)
