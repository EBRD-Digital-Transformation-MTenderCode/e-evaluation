package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.SetAwardForEvaluationResult
import com.procurement.evaluation.infrastructure.dto.award.evaluate.response.SetAwardForEvaluationResponse

fun SetAwardForEvaluationResult.convert() = SetAwardForEvaluationResponse(
    awards = this.awards.map { award ->
        SetAwardForEvaluationResponse.Award(
            id = award.id,
            token = award.token,
            title = award.title,
            date = award.date,
            status = award.status,
            statusDetails = award.statusDetails,
            relatedLots = award.relatedLots.toList(),
            relatedBid = award.relatedBid,
            value = award.value,
            suppliers = award.suppliers.map { supplier ->
                SetAwardForEvaluationResponse.Award.Supplier(
                    id = supplier.id,
                    name = supplier.name
                )
            },
            weightedValue = award.weightedValue
        )
    }
)
