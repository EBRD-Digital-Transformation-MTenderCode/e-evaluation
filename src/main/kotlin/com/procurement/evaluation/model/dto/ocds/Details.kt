package com.procurement.evaluation.model.dto.ocds

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonInclude

data class Details @JsonCreator constructor(

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val typeOfSupplier: TypeOfSupplier?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val mainEconomicActivities: List<String>?,

    val scale: String,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val permits: List<Permit>?,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    val bankAccounts: List<BankAccount>?,

    @JsonInclude(JsonInclude.Include.NON_NULL)
    val legalForm: LegalForm?
) {
    data class Permit(
        val scheme: String,
        val id: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        val url: String?,

        val permitDetails: PermitDetails
    ) {
        data class PermitDetails(
            val issuedBy: IssuedBy,
            val issuedThought: IssuedThought,
            val validityPeriod: ValidityPeriod
        ) {
            data class IssuedBy(
                val id: String,
                val name: String
            )

            data class IssuedThought(
                val id: String,
                val name: String
            )

            data class ValidityPeriod(
                val startDate: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                val endDate: String?
            )
        }
    }

    data class BankAccount(
        val description: String,
        val bankName: String,
        val address: Address,
        val identifier: Identifier,
        val accountIdentification: AccountIdentification,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>?
    ) {
        data class Address(
            val streetAddress: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
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

                    @JsonInclude(JsonInclude.Include.NON_NULL)
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

        @JsonInclude(JsonInclude.Include.NON_NULL)
        val uri: String?
    )
}

