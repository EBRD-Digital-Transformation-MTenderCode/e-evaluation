package com.procurement.evaluation.domain.model.lot

import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.lib.functional.Result
import java.util.*

typealias LotId = UUID

fun String.tryLotId(): Result<LotId, Failure.Incident.Transform.Parsing> =
    this.tryUUID()
