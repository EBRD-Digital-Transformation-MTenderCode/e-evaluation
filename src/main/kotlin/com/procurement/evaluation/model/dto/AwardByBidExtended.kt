package com.procurement.evaluation.model.dto

import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.BusinessFunctionType
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

data class AwardByBidExtended(
    val id: UUID,
    val status: AwardStatus,
    val statusDetails: AwardStatusDetails,
    val relatedBid: String,
    val relatedLots: List<String>,
    val value: Value,
    val suppliers: List<Supplier>,
    val date: LocalDateTime,
    val bidDate: LocalDateTime,
    val weightedValue: WeightedValue

) {
    data class Supplier(
        val id: String,
        val name: String,
        val identifier: Identifier,
        val additionalIdentifiers: List<AdditionalIdentifier>,
        val address: Address,
        val contactPoint: ContactPoint,
        val persones: List<Person>

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

        data class Person(
            val title: String,
            val name: String,
            val identifier: Identifier,
            val businessFunctions: List<BusinessFunction>
        ) {
            data class Identifier(
                val scheme: String,
                val id: String,
                val uri: String?
            )

            data class BusinessFunction(
                val id: String,
                val type: BusinessFunctionType,
                val jobTitle: String,
                val period: Period,
                val documents: List<Document>
            ) {
                data class Period(
                    val startDate: String
                )

                data class Document(
                    val id: String,
                    val documentType: String,
                    val title: String,
                    val description: String?
                )
            }
        }

        data class BankAccount(
            val description: String,
            val bankName: String,
            val address: Address,
            val identifier: Identifier,
            val accountIdentification: AccountIdentification,

            val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>
        ) {
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

            data class Identifier(
                val scheme: String,
                val id: String
            )

            data class AccountIdentification(
                val scheme: String,
                val id: String
            )

            data class AdditionalAccountIdentifier(
                val scheme: String,
                val id: String
            )
        }

        data class LegalForm(
            val scheme: String,
            val id: String,
            val description: String,
            val uri: String?
        )
    }

    data class Value(
        val amount: BigDecimal,
        val currency: String
    )

    data class WeightedValue(
        val amount: BigDecimal,
        val currency: String
    )
}


