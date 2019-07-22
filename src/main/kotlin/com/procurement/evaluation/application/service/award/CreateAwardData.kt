package com.procurement.evaluation.application.service.award

import java.math.BigDecimal

data class CreateAwardData(
    val mdm: Mdm,
    val award: Award
) {

    data class Mdm(
        val scales: List<String>,
        val schemes: List<String>
    )

    data class Award(
        val description: String?,
        val value: Value,
        val suppliers: List<Supplier>
    ) {

        data class Value(
            val amount: BigDecimal,
            val currency: String
        )

        data class Supplier(
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
