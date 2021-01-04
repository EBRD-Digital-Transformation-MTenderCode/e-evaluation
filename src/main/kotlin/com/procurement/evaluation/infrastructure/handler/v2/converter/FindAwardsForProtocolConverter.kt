package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.find.FindAwardsForProtocolParams
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.FindAwardsForProtocolRequest
import com.procurement.evaluation.lib.functional.Result

fun FindAwardsForProtocolRequest.convert(): Result<FindAwardsForProtocolParams, DataErrors> =
    FindAwardsForProtocolParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        tender = tender.convert()
    )

fun FindAwardsForProtocolRequest.Tender.convert(): FindAwardsForProtocolParams.Tender =
    FindAwardsForProtocolParams.Tender(lots = lots.map { it.convert() })

fun FindAwardsForProtocolRequest.Tender.Lot.convert(): FindAwardsForProtocolParams.Tender.Lot =
    FindAwardsForProtocolParams.Tender.Lot(id = id)
