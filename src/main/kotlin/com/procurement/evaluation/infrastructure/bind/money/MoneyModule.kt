package com.procurement.evaluation.infrastructure.bind.money

import com.fasterxml.jackson.databind.module.SimpleModule
import com.procurement.evaluation.domain.model.money.Money

class MoneyModule : SimpleModule() {
    companion object {
        @JvmStatic
        private val serialVersionUID = 1L
    }

    init {
        addSerializer(Money::class.java, MoneySerializer())
        addDeserializer(Money::class.java, MoneyDeserializer())
    }
}
