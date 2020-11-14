package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.model.award.state.GetAwardStateByIdsParams
import com.procurement.evaluation.infrastructure.dto.award.state.GetAwardStateByIdsRequest
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result

fun GetAwardStateByIdsRequest.convert(): Result<GetAwardStateByIdsParams, DataErrors> =
    GetAwardStateByIdsParams.tryCreate(awardIds = awardIds,cpid =  cpid,ocid =  ocid)
