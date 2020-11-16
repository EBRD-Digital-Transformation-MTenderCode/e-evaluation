package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.unsuccessful.CreateUnsuccessfulAwardsParams
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.CreateUnsuccessfulAwardsRequest
import com.procurement.evaluation.lib.functional.Result

fun CreateUnsuccessfulAwardsRequest.convert(): Result<CreateUnsuccessfulAwardsParams, DataErrors> =
    CreateUnsuccessfulAwardsParams.tryCreate(
        cpid = this.cpid,
        ocid = this.ocid,
        date = this.date,
        lotIds = this.lotIds,
        operationType = this.operationType
    )
