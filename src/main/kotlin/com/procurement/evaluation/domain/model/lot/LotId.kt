package com.procurement.evaluation.domain.model.lot

import com.procurement.evaluation.domain.util.extension.tryUUID
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.Result
import java.util.*

typealias LotId = UUID

fun String.tryLotId(): Result<LotId, Fail.Incident.Transform.Parsing> =
    this.tryUUID()
