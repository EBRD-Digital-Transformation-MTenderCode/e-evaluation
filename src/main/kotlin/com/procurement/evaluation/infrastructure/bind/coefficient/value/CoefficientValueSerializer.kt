package com.procurement.evaluation.infrastructure.bind.coefficient.value

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.evaluation.domain.model.data.CoefficientValue
import java.io.IOException

class CoefficientValueSerializer : JsonSerializer<CoefficientValue>() {
    companion object {
        fun serialize(coefficientValue: CoefficientValue.AsString): String = coefficientValue.value
        fun serialize(coefficientValue: CoefficientValue.AsBoolean): Boolean = coefficientValue.value
        fun serialize(coefficientValue: CoefficientValue.AsNumber): String = "%.3f".format(coefficientValue.value)
        fun serialize(coefficientValue: CoefficientValue.AsInteger): Long = coefficientValue.value
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(
        coefficientValue: CoefficientValue,
        jsonGenerator: JsonGenerator,
        provider: SerializerProvider
    ) =
        when (coefficientValue) {
            is CoefficientValue.AsString -> jsonGenerator.writeString(serialize(coefficientValue))
            is CoefficientValue.AsNumber -> jsonGenerator.writeNumber(serialize(coefficientValue))
            is CoefficientValue.AsBoolean -> jsonGenerator.writeBoolean(serialize(coefficientValue))
            is CoefficientValue.AsInteger -> jsonGenerator.writeNumber(serialize(coefficientValue))
        }
}
