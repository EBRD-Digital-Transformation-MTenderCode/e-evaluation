package com.procurement.evaluation.model.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.evaluation.model.dto.databinding.MoneyDeserializer
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CheckAwardRq(

        val award: CheckAward
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CheckAward(

        val id: String,

        val value: CheckValue
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CheckValue(

        @JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal,

        @JsonDeserialize(using = MoneyDeserializer::class)
        val amountNet: BigDecimal,

        val valueAddedTaxIncluded: Boolean,

        val currency: String
)