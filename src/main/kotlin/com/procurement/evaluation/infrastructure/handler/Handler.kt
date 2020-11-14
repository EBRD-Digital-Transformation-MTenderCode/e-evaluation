package com.procurement.evaluation.infrastructure.handler

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.infrastructure.api.Action

interface Handler<T : Action, R: Any> {
    val action: T
    fun handle(node: JsonNode): R
}
