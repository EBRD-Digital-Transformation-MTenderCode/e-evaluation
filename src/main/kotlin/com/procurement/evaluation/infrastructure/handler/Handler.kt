package com.procurement.evaluation.infrastructure.handler

import com.procurement.evaluation.infrastructure.api.Action
import com.procurement.evaluation.infrastructure.api.ApiVersion
import com.procurement.evaluation.infrastructure.handler.v2.model.CommandDescriptor

interface Handler<R : Any> {
    val version: ApiVersion
    val action: Action

    fun handle(descriptor: CommandDescriptor): R
}
