package com.procurement.evaluation.infrastructure.bind.money

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.procurement.evaluation.domain.model.money.Money
import com.procurement.evaluation.infrastructure.exception.MoneyParseException

class MoneySerializer : JsonSerializer<Money>() {
    companion object {
        private const val AMOUNT_PATTERN = "%.${Money.AVAILABLE_SCALE}f"

        private fun serialize(money: Money, jsonGenerator: JsonGenerator) {
            val scale = money.amount.scale()
            if (scale > Money.AVAILABLE_SCALE)
                throw MoneyParseException("Attribute 'amount' is an invalid scale '$scale', the maximum scale: '${Money.AVAILABLE_SCALE}'.")

            jsonGenerator.writeStartObject()
            jsonGenerator.writeFieldName("amount")
            jsonGenerator.writeNumber(AMOUNT_PATTERN.format(money.amount))
            jsonGenerator.writeStringField("currency", money.currency)
            jsonGenerator.writeEndObject()
        }
    }

    override fun serialize(money: Money, jsonGenerator: JsonGenerator, provider: SerializerProvider) =
        serialize(money, jsonGenerator)
}
