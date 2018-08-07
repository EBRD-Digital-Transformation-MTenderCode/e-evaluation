package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.procurement.evaluation.model.dto.databinding.MoneyDeserializer
import java.math.BigDecimal
import javax.validation.constraints.NotNull

data class Value(

        @field:NotNull
        @field:JsonDeserialize(using = MoneyDeserializer::class)
        val amount: BigDecimal,

        @field:NotNull
        val currency: String
)