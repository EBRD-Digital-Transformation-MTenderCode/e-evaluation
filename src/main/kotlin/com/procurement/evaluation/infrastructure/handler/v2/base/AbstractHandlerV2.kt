package com.procurement.evaluation.infrastructure.handler.v2.base

import com.procurement.evaluation.infrastructure.api.ApiVersion
import com.procurement.evaluation.infrastructure.handler.Handler

abstract class AbstractHandlerV2<R : Any> : Handler<R> {

    final override val version: ApiVersion
        get() = ApiVersion(2, 0, 0)
}
