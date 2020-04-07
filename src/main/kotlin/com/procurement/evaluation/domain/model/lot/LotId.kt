package com.procurement.evaluation.domain.model.lot

import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.fail.Fail
import java.util.*
import com.procurement.evaluation.domain.functional.Result

typealias LotId = UUID

fun String.tryLotId(): Result<LotId, Fail.Incident.Parsing> =
    this.tryUUID()
