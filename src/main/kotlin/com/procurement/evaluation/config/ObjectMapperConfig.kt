package com.procurement.evaluation.config

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.procurement.evaluation.domain.model.data.RequirementRsValue
import com.procurement.evaluation.infrastructure.bind.api.version.ApiVersion2Deserializer
import com.procurement.evaluation.infrastructure.bind.api.version.ApiVersion2Serializer
import com.procurement.evaluation.infrastructure.bind.criteria.RequirementValueDeserializer
import com.procurement.evaluation.infrastructure.bind.criteria.RequirementValueSerializer
import com.procurement.evaluation.infrastructure.dto.ApiVersion2
import com.procurement.evaluation.model.dto.databinding.IntDeserializer
import com.procurement.evaluation.model.dto.databinding.JsonDateDeserializer
import com.procurement.evaluation.model.dto.databinding.JsonDateSerializer
import com.procurement.evaluation.model.dto.databinding.StringsDeserializer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Configuration
import java.time.LocalDateTime

@Configuration
class ObjectMapperConfig(@Autowired objectMapper: ObjectMapper) {

    init {
        val module = SimpleModule()
        module.addSerializer(LocalDateTime::class.java, JsonDateSerializer())
        module.addDeserializer(LocalDateTime::class.java, JsonDateDeserializer())
        module.addDeserializer(String::class.java, StringsDeserializer())
        module.addDeserializer(Int::class.java, IntDeserializer())

        /**
         * Serializer/Deserializer for ApiVersion type
         */
        module.addSerializer(ApiVersion2::class.java, ApiVersion2Serializer())
        module.addDeserializer(ApiVersion2::class.java, ApiVersion2Deserializer())

        /**
         * Serializer/Deserializer for RequirementRsValue type
         */
        module.addSerializer(RequirementRsValue::class.java, RequirementValueSerializer())
        module.addDeserializer(RequirementRsValue::class.java, RequirementValueDeserializer())

        objectMapper.registerModule(module)
        objectMapper.registerKotlinModule()
        objectMapper.configure(DeserializationFeature.USE_BIG_INTEGER_FOR_INTS, true)
        objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.nodeFactory = JsonNodeFactory.withExactBigDecimals(true)
    }
}