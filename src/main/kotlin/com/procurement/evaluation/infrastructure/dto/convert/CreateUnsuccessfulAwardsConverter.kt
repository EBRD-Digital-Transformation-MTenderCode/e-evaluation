package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.model.award.unsuccessful.CreateUnsuccessfulAwardsParams
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.create.unsuccessfulaward.CreateUnsuccessfulAwardsRequest

fun CreateUnsuccessfulAwardsRequest.convert(): Result<CreateUnsuccessfulAwardsParams, DataErrors> =
    CreateUnsuccessfulAwardsParams.tryCreate(
        cpid = this.cpid,
        ocid = this.ocid,
        requestDate = this.requestDate,
        lotIds = this.lotIds,
        owner = this.owner
    )
