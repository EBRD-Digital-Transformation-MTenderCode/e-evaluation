package com.procurement.evaluation.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.infrastructure.api.Action
import com.procurement.evaluation.infrastructure.api.ApiVersion

interface Handler<R: Any> {
    val version: ApiVersion
    val action: Action

    fun handle(node: JsonNode): R
}
