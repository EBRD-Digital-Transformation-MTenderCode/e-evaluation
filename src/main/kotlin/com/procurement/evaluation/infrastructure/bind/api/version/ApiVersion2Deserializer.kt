package com.procurement.evaluation.infrastructure.bind.api.version

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.evaluation.infrastructure.dto.ApiVersion2

class ApiVersion2Deserializer : JsonDeserializer<ApiVersion2>() {
    companion object {
        fun deserialize(text: String) = ApiVersion2.orThrow(text) {
            IllegalAccessException("Invalid format of the api version. Expected: '${ApiVersion2.pattern}', actual: '$text'.")
        }
    }

    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): ApiVersion2 =
        deserialize(jsonParser.text)
}
