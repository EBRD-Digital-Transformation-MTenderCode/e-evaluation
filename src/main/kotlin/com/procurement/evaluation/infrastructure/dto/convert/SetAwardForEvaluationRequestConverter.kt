package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.SetAwardForEvaluationData
import com.procurement.evaluation.infrastructure.dto.award.evaluate.request.SetAwardForEvaluationRequest

fun SetAwardForEvaluationRequest.convert() = SetAwardForEvaluationData(
    awardCriteria = this.awardCriteria,
    awardCriteriaDetails = this.awardCriteriaDetails
)