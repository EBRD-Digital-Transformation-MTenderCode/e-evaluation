package com.procurement.evaluation.infrastructure.bind.amount

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.evaluation.infrastructure.exception.AmountValueException
import java.io.IOException
import java.math.BigDecimal

class AmountDeserializer : JsonDeserializer<BigDecimal>() {
    companion object {
        fun deserialize(text: String): BigDecimal = try {
            BigDecimal(text)
        } catch (exception: Exception) {
            throw AmountValueException(text, exception.message ?: "")
        }
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): BigDecimal =
        deserialize(jsonParser.text)
}
