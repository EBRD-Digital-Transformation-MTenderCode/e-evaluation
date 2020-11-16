package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

data class OrganizationReference @JsonCreator constructor(

    val id: String,

    val name: String,

    val identifier: Identifier,

    val address: Address,

    val contactPoint: ContactPoint,
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val additionalIdentifiers: MutableList<Identifier>?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    var details: Details?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val persones: List<Person>?
) {
    data class Person(
        val title: String,
        val name: String,
        val identifier: Identifier,
        val businessFunctions: List<BusinessFunction>
    ) {
        data class Identifier(
            val scheme: String,
            val id: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            val uri: String?
        )

        data class BusinessFunction(
            val id: String,
            val type: BusinessFunctionType,
            val jobTitle: String,
            val period: Period,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            val documents: List<Document>?
        ) {
            data class Period(
                val startDate: LocalDateTime
            )

            data class Document(
                val id: String,
                val documentType: String,
                val title: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                val description: String?
            )
        }
    }
}
