package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.get.GetAwardByIdsParams
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.GetAwardByIdsRequest
import com.procurement.evaluation.lib.functional.Result

fun GetAwardByIdsRequest.convert(): Result<GetAwardByIdsParams, DataErrors> =
    GetAwardByIdsParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        awards = awards.map { it.convert() }
    )

fun GetAwardByIdsRequest.Award.convert(): GetAwardByIdsParams.Award =
    GetAwardByIdsParams.Award(id = id)
