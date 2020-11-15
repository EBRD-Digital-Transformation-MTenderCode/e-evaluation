package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.tenderer.CheckRelatedTendererParams
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.CheckRelatedTendererRequest
import com.procurement.evaluation.lib.functional.Result

fun CheckRelatedTendererRequest.convert(): Result<CheckRelatedTendererParams, DataErrors> =
    CheckRelatedTendererParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        awardId = awardId,
        requirementId = requirementId,
        relatedTendererId = relatedTendererId,
        responderId = responderId
    )