package com.procurement.evaluation.infrastructure.handler.v2.model

import com.fasterxml.jackson.databind.JsonNode
import com.procurement.evaluation.infrastructure.api.Action
import com.procurement.evaluation.infrastructure.api.ApiVersion
import com.procurement.evaluation.infrastructure.api.command.id.CommandId

data class CommandDescriptor(
    val version: ApiVersion,
    val id: CommandId,
    val action: Action,
    val body: Body
) {
    data class Body(val asString: String, val asJsonNode: JsonNode)

    companion object
}
