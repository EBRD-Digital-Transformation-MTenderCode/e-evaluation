package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.evaluation.domain.model.money.Money
import com.procurement.evaluation.model.dto.databinding.MoneyDeserializer
import java.math.BigDecimal

data class Value(
    @field:JsonDeserialize(using = MoneyDeserializer::class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    val amount: BigDecimal?,

    val currency: String?
)

val Money.asValue: Value
    get() = this.let { money ->
        Value(
            amount = money.amount,
            currency = money.currency
        )
    }

val Value.asMoney: Money
    get() = this.let { money ->
        Money(
            amount = money.amount!!,
            currency = money.currency!!
        )
    }
