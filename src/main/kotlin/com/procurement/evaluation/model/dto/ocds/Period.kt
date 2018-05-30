package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.evaluation.model.dto.databinding.JsonDateDeserializer
import com.procurement.evaluation.model.dto.databinding.JsonDateSerializer
import java.time.LocalDateTime
import javax.validation.constraints.NotNull

data class Period(

        @NotNull
        @JsonProperty("startDate")
        @JsonDeserialize(using = JsonDateDeserializer::class)
        @JsonSerialize(using = JsonDateSerializer::class)
        val startDate: LocalDateTime,

        @NotNull
        @JsonProperty("endDate")
        @JsonDeserialize(using = JsonDateDeserializer::class)
        @JsonSerialize(using = JsonDateSerializer::class)
        val endDate: LocalDateTime?
)
