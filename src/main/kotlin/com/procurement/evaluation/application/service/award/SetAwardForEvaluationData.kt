package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.model.dto.ocds.AwardCriteria
import com.procurement.evaluation.model.dto.ocds.AwardCriteriaDetails

data class SetAwardForEvaluationData(
    val awardCriteria: AwardCriteria,
    val awardCriteriaDetails: AwardCriteriaDetails
)
