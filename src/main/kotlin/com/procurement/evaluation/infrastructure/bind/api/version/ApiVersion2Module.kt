package com.procurement.evaluation.infrastructure.bind.api.version

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.evaluation.infrastructure.dto.ApiVersion2

class ApiVersion2Module : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(ApiVersion2::class.java, ApiVersion2Serializer())
        addDeserializer(ApiVersion2::class.java, ApiVersion2Deserializer())
    }
}
