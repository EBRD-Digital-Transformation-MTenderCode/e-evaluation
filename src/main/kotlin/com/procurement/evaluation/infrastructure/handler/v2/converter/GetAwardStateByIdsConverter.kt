package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.state.GetAwardStateByIdsParams
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.GetAwardStateByIdsRequest
import com.procurement.evaluation.lib.functional.Result

fun GetAwardStateByIdsRequest.convert(): Result<GetAwardStateByIdsParams, DataErrors> =
    GetAwardStateByIdsParams.tryCreate(awardIds = awardIds,cpid =  cpid,ocid =  ocid)
