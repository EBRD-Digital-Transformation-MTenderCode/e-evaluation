package com.procurement.evaluation.infrastructure.handler.v1.converter

import com.procurement.evaluation.application.service.award.SetAwardForEvaluationData
import com.procurement.evaluation.infrastructure.handler.v1.model.request.SetAwardForEvaluationRequest

fun SetAwardForEvaluationRequest.convert() = SetAwardForEvaluationData(
    awardCriteria = this.awardCriteria,
    awardCriteriaDetails = this.awardCriteriaDetails
)