package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.model.award.tenderer.CheckRelatedTendererParams
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.infrastructure.dto.award.tenderer.CheckRelatedTendererRequest
import com.procurement.evaluation.infrastructure.fail.error.DataErrors

fun CheckRelatedTendererRequest.convert(): Result<CheckRelatedTendererParams, DataErrors> =
    CheckRelatedTendererParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        awardId = awardId,
        requirementId = requirementId,
        relatedTendererId = relatedTendererId,
        responderId = responderId
    )