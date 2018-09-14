package com.procurement.evaluation.model.dto.databinding

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import java.io.IOException


class BooleansDeserializer : JsonDeserializer<Boolean>() {

    @Throws(IOException::class)
    override fun deserialize(jsonParser: JsonParser, deserializationContext: DeserializationContext): Boolean {
        if (!jsonParser.currentToken.isBoolean) {
            throw ErrorException(ErrorType.JSON_TYPE, jsonParser.currentName)
        }
        return jsonParser.valueAsBoolean
    }
}