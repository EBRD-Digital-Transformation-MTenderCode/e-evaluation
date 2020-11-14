package com.procurement.evaluation.infrastructure.bind.api.version

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.evaluation.infrastructure.dto.ApiVersion2
import java.io.IOException

class ApiVersion2Deserializer : JsonDeserializer<ApiVersion2>() {
    companion object {
        fun deserialize(text: String) = ApiVersion2.valueOf(text)
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): ApiVersion2 =
        deserialize(jsonParser.text)
}
