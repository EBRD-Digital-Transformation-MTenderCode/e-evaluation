package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreatedAwardData(
    val token: String,
    val awardPeriod: AwardPeriod?,
    val lotAwarded: Boolean?,
    val award: Award
) {

    data class AwardPeriod(
        val startDate: LocalDateTime
    )

    data class Award(
        val id: String,
        val date: LocalDateTime,
        val status: AwardStatus,
        val statusDetails: AwardStatusDetails,
        val relatedLots: List<String>,
        val description: String?,
        val value: Value,
        val suppliers: List<Supplier>
    ) {

        data class Value(
            val amount: BigDecimal,
            val currency: String
        )

        data class Supplier(
            val id: String,
            val name: String,
            val identifier: Identifier,
            val additionalIdentifiers: List<AdditionalIdentifier>?,
            val address: Address,
            val contactPoint: ContactPoint,
            val details: Details
        ) {

            data class Identifier(
                val scheme: String,
                val id: String,
                val legalName: String,
                val uri: String?
            )

            data class AdditionalIdentifier(
                val scheme: String,
                val id: String,
                val legalName: String,
                val uri: String?
            )

            data class Address(
                val streetAddress: String,
                val postalCode: String?,
                val addressDetails: AddressDetails
            ) {

                data class AddressDetails(
                    val country: Country,
                    val region: Region,
                    val locality: Locality
                ) {

                    data class Country(
                        val scheme: String,
                        val id: String,
                        val description: String,
                        val uri: String
                    )

                    data class Region(
                        val scheme: String,
                        val id: String,
                        val description: String,
                        val uri: String
                    )

                    data class Locality(
                        val scheme: String,
                        val id: String,
                        val description: String,
                        val uri: String?
                    )
                }
            }

            data class ContactPoint(
                val name: String,
                val email: String,
                val telephone: String,
                val faxNumber: String?,
                val url: String?
            )

            data class Details(
                val scale: String
            )
        }
    }
}
