package com.procurement.evaluation.infrastructure.bind.api.version

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.evaluation.infrastructure.api.ApiVersion

class ApiVersionModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(ApiVersion::class.java, ApiVersionSerializer())
        addDeserializer(ApiVersion::class.java, ApiVersionDeserializer())
    }
}
