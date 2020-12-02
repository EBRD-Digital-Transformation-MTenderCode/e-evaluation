package com.procurement.evaluation.infrastructure.handler.v2.model.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.infrastructure.bind.amount.AmountDeserializer
import com.procurement.evaluation.infrastructure.bind.amount.AmountSerializer
import com.procurement.evaluation.model.dto.ocds.Address
import com.procurement.evaluation.model.dto.ocds.AddressDetails
import com.procurement.evaluation.model.dto.ocds.BusinessFunctionType
import com.procurement.evaluation.model.dto.ocds.ContactPoint
import com.procurement.evaluation.model.dto.ocds.CountryDetails
import com.procurement.evaluation.model.dto.ocds.Details
import com.procurement.evaluation.model.dto.ocds.Document
import com.procurement.evaluation.model.dto.ocds.Identifier
import com.procurement.evaluation.model.dto.ocds.LocalityDetails
import com.procurement.evaluation.model.dto.ocds.MainEconomicActivity
import com.procurement.evaluation.model.dto.ocds.OrganizationReference
import com.procurement.evaluation.model.dto.ocds.RegionDetails
import com.procurement.evaluation.model.dto.ocds.Value
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateAwardResult(
    @field:JsonProperty("token") @param:JsonProperty("token") val token: Token,
    @field:JsonProperty("awards") @param:JsonProperty("awards") val awards: List<Award>
) {
    data class Award(
        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("internalId") @param:JsonProperty("internalId") val internalId: String?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("date") @param:JsonProperty("date") val date: LocalDateTime?,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

        @field:JsonProperty("value") @param:JsonProperty("value") val value: Value,

        @field:JsonProperty("suppliers") @param:JsonProperty("suppliers") val suppliers: List<Supplier>,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @field:JsonProperty("relatedLots") @param:JsonProperty("relatedLots") val relatedLots: List<String>?
    ) {
        data class Value(
            @JsonDeserialize(using = AmountDeserializer::class)
            @JsonSerialize(using = AmountSerializer::class)
            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("amount") @param:JsonProperty("amount") val amount: BigDecimal?,

            @field:JsonProperty("currency") @param:JsonProperty("currency") val currency: String
        )

        data class Supplier(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
            @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("additionalIdentifiers") @param:JsonProperty("additionalIdentifiers") val additionalIdentifiers: List<Identifier>?,

            @field:JsonProperty("address") @param:JsonProperty("address") val address: Address,
            @field:JsonProperty("contactPoint") @param:JsonProperty("contactPoint") val contactPoint: ContactPoint,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @field:JsonProperty("persones") @param:JsonProperty("persones") val persons: List<Person>?,

            @field:JsonProperty("details") @param:JsonProperty("details") val details: Details
        ) {
            data class Identifier(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("legalName") @param:JsonProperty("legalName") val legalName: String,
                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
            )

            data class Address(
                @field:JsonProperty("streetAddress") @param:JsonProperty("streetAddress") val streetAddress: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("postalCode") @param:JsonProperty("postalCode") val postalCode: String?,

                @field:JsonProperty("addressDetails") @param:JsonProperty("addressDetails") val addressDetails: AddressDetails
            ) {
                data class AddressDetails(
                    @field:JsonProperty("country") @param:JsonProperty("country") val country: Country,
                    @field:JsonProperty("region") @param:JsonProperty("region") val region: Region,
                    @field:JsonProperty("locality") @param:JsonProperty("locality") val locality: Locality
                ) {
                    data class Country(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                    )

                    data class Region(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                    )

                    data class Locality(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                    )
                }
            }

            data class ContactPoint(
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                @field:JsonProperty("email") @param:JsonProperty("email") val email: String,
                @field:JsonProperty("telephone") @param:JsonProperty("telephone") val telephone: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("faxNumber") @param:JsonProperty("faxNumber") val faxNumber: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("url") @param:JsonProperty("url") val url: String?
            )

            data class Person(
                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                @field:JsonProperty("title") @param:JsonProperty("title") val title: String,
                @field:JsonProperty("name") @param:JsonProperty("name") val name: String,
                @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
                @field:JsonProperty("businessFunctions") @param:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
            ) {
                data class Identifier(
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                )

                data class BusinessFunction(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("type") @param:JsonProperty("type") val type: BusinessFunctionType,
                    @field:JsonProperty("jobTitle") @param:JsonProperty("jobTitle") val jobTitle: String,
                    @field:JsonProperty("period") @param:JsonProperty("period") val period: Period,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @field:JsonProperty("documents") @param:JsonProperty("documents") val documents: List<Document>?
                ) {
                    data class Period(
                        @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: LocalDateTime
                    )

                    data class Document(
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                        @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: String,
                        @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("description") @param:JsonProperty("description") val description: String?
                    )
                }
            }

            data class Details(
                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("typeOfSupplier") @param:JsonProperty("typeOfSupplier") val typeOfSupplier: String?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("mainEconomicActivities") @param:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<MainEconomicActivity>?,

                @field:JsonProperty("scale") @param:JsonProperty("scale") val scale: String,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("permits") @param:JsonProperty("permits") val permits: List<Permit>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @field:JsonProperty("bankAccounts") @param:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount>?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @field:JsonProperty("legalForm") @param:JsonProperty("legalForm") val legalForm: LegalForm?
            ) {
                data class MainEconomicActivity(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                )

                data class Permit(
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                    @field:JsonProperty("permitDetails") @param:JsonProperty("permitDetails") val permitDetails: PermitDetails,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("url") @param:JsonProperty("url") val url: String?
                ) {
                    data class PermitDetails(
                        @field:JsonProperty("issuedBy") @param:JsonProperty("issuedBy") val issuedBy: IssuedBy,
                        @field:JsonProperty("issuedThought") @param:JsonProperty("issuedThought") val issuedThought: IssuedThought,
                        @field:JsonProperty("validityPeriod") @param:JsonProperty("validityPeriod") val validityPeriod: ValidityPeriod
                    ) {
                        data class IssuedBy(
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                            @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                        )

                        data class IssuedThought(
                            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                            @field:JsonProperty("name") @param:JsonProperty("name") val name: String
                        )

                        data class ValidityPeriod(
                            @field:JsonProperty("startDate") @param:JsonProperty("startDate") val startDate: String,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @field:JsonProperty("endDate") @param:JsonProperty("endDate") val endDate: String?
                        )
                    }
                }

                data class BankAccount(
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                    @field:JsonProperty("bankName") @param:JsonProperty("bankName") val bankName: String,
                    @field:JsonProperty("address") @param:JsonProperty("address") val address: Address,
                    @field:JsonProperty("identifier") @param:JsonProperty("identifier") val identifier: Identifier,
                    @field:JsonProperty("accountIdentification") @param:JsonProperty("accountIdentification") val accountIdentification: AccountIdentification,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @field:JsonProperty("additionalAccountIdentifiers") @param:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AccountIdentification>?
                ) {
                    data class Address(
                        @field:JsonProperty("streetAddress") @param:JsonProperty("streetAddress") val streetAddress: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @field:JsonProperty("postalCode") @param:JsonProperty("postalCode") val postalCode: String?,

                        @field:JsonProperty("addressDetails") @param:JsonProperty("addressDetails") val addressDetails: AddressDetails
                    ) {
                        data class AddressDetails(
                            @field:JsonProperty("country") @param:JsonProperty("country") val country: Country,
                            @field:JsonProperty("region") @param:JsonProperty("region") val region: Region,
                            @field:JsonProperty("locality") @param:JsonProperty("locality") val locality: Locality
                        ) {
                            data class Country(
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                            )

                            data class Region(
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String
                            )

                            data class Locality(
                                @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
                                @field:JsonProperty("description") @param:JsonProperty("description") val description: String,
                                @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                            )
                        }
                    }

                    data class Identifier(
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
                    )

                    data class AccountIdentification(
                        @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                        @field:JsonProperty("id") @param:JsonProperty("id") val id: String
                    )
                }

                data class LegalForm(
                    @field:JsonProperty("scheme") @param:JsonProperty("scheme") val scheme: String,
                    @field:JsonProperty("id") @param:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @field:JsonProperty("uri") @param:JsonProperty("uri") val uri: String?
                )
            }
        }

        data class Document(
            @field:JsonProperty("id") @param:JsonProperty("id") val id: String,
            @field:JsonProperty("title") @param:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @field:JsonProperty("description") @param:JsonProperty("description") val description: String?,

            @field:JsonProperty("documentType") @param:JsonProperty("documentType") val documentType: String
        )
    }


    object ResponseConverter {

        fun fromDomain(award: com.procurement.evaluation.model.dto.ocds.Award): Award =
            Award(
                id = award.id,
                internalId = award.internalId,
                date = award.date,
                description = award.description,
                value = award.value!!.fromDomain(),
                suppliers = award.suppliers.orEmpty().map { it.fromDomain() },
                documents = award.documents?.map { it.fromDomain() },
                relatedLots = award.relatedLots
            )

        fun Value.fromDomain(): Award.Value =
            Award.Value(
                amount = amount,
                currency = currency!!
            )

        fun Document.fromDomain(): Award.Document =
            Award.Document(
                id = id,
                title = title!!,
                description = description,
                documentType = documentType.key
            )

        fun OrganizationReference.fromDomain(): Award.Supplier =
            Award.Supplier(
                id = id,
                name = name,
                identifier = identifier.fromDomain(),
                additionalIdentifiers = additionalIdentifiers?.map { it.fromDomain() },
                address = address.fromDomain(),
                contactPoint = contactPoint.fromDomain(),
                details = details!!.fromDomain(),
                persons = persones?.map { it.fromDomain() }
            )

        fun OrganizationReference.Person.fromDomain(): Award.Supplier.Person =
            Award.Supplier.Person(
                id = "${identifier.id}-${identifier.scheme}",
                title = title,
                identifier = identifier.fromDomain(),
                name = name,
                businessFunctions = businessFunctions.map { it.fromDomain() }
            )

        fun OrganizationReference.Person.Identifier.fromDomain(): Award.Supplier.Person.Identifier =
            Award.Supplier.Person.Identifier(
                id = id,
                scheme = scheme,
                uri = uri
            )

        fun OrganizationReference.Person.BusinessFunction.fromDomain(): Award.Supplier.Person.BusinessFunction =
            Award.Supplier.Person.BusinessFunction(
                id = id,
                type = type,
                jobTitle = jobTitle,
                period = period.fromDomain(),
                documents = documents?.map { it.fromDomain() }
            )

        fun OrganizationReference.Person.BusinessFunction.Period.fromDomain(): Award.Supplier.Person.BusinessFunction.Period =
            Award.Supplier.Person.BusinessFunction.Period(startDate = startDate)

        fun OrganizationReference.Person.BusinessFunction.Document.fromDomain(): Award.Supplier.Person.BusinessFunction.Document =
            Award.Supplier.Person.BusinessFunction.Document(
                id = id,
                title = title,
                description = description,
                documentType = documentType
            )

        fun Identifier.fromDomain(): Award.Supplier.Identifier =
            Award.Supplier.Identifier(
                id = id,
                scheme = scheme,
                legalName = legalName,
                uri = uri
            )

        fun Address.fromDomain(): Award.Supplier.Address =
            Award.Supplier.Address(
                streetAddress = streetAddress,
                postalCode = postalCode,
                addressDetails = addressDetails.fromDomain()
            )

        fun AddressDetails.fromDomain(): Award.Supplier.Address.AddressDetails =
            Award.Supplier.Address.AddressDetails(
                country = country.fromDomain(),
                region = region.fromDomain(),
                locality = locality.fromDomain()
            )

        fun CountryDetails.fromDomain(): Award.Supplier.Address.AddressDetails.Country =
            Award.Supplier.Address.AddressDetails.Country(
                id = id,
                scheme = scheme!!,
                description = description!!,
                uri = uri!!
            )

        fun RegionDetails.fromDomain(): Award.Supplier.Address.AddressDetails.Region =
            Award.Supplier.Address.AddressDetails.Region(
                id = id,
                scheme = scheme!!,
                description = description!!,
                uri = uri!!
            )

        fun LocalityDetails.fromDomain(): Award.Supplier.Address.AddressDetails.Locality =
            Award.Supplier.Address.AddressDetails.Locality(
                id = id,
                scheme = scheme,
                description = description,
                uri = uri
            )

        fun ContactPoint.fromDomain(): Award.Supplier.ContactPoint =
            Award.Supplier.ContactPoint(
                name = name,
                email = email,
                telephone = telephone,
                faxNumber = faxNumber,
                url = url
            )

        fun Details.fromDomain(): Award.Supplier.Details =
            Award.Supplier.Details(
                scale = scale,
                typeOfSupplier = typeOfSupplier?.key,
                bankAccounts = bankAccounts?.map { it.fromDomain() },
                permits = permits?.map { it.fromDomain() },
                mainEconomicActivities = mainEconomicActivities?.map { it.fromDomain() },
                legalForm = legalForm?.fromDomain()
            )

        fun Details.BankAccount.fromDomain(): Award.Supplier.Details.BankAccount =
            Award.Supplier.Details.BankAccount(
                bankName = bankName,
                description = description,
                identifier = identifier.fromDomain(),
                address = address.fromDomain(),
                accountIdentification = accountIdentification.fromDomain(),
                additionalAccountIdentifiers = additionalAccountIdentifiers?.map {
                    Award.Supplier.Details.BankAccount.AccountIdentification(
                        id = it.id,
                        scheme = it.scheme
                    )
                }
            )

        fun Details.BankAccount.Identifier.fromDomain(): Award.Supplier.Details.BankAccount.Identifier =
            Award.Supplier.Details.BankAccount.Identifier(id = id, scheme = scheme)

        fun Details.BankAccount.Address.fromDomain(): Award.Supplier.Details.BankAccount.Address =
            Award.Supplier.Details.BankAccount.Address(
                streetAddress = streetAddress,
                postalCode = postalCode,
                addressDetails = addressDetails.fromDomain()
            )

        fun Details.BankAccount.Address.AddressDetails.fromDomain(): Award.Supplier.Details.BankAccount.Address.AddressDetails =
            Award.Supplier.Details.BankAccount.Address.AddressDetails(
                country = country.fromDomain(),
                region = region.fromDomain(),
                locality = locality.fromDomain()
            )

        fun Details.BankAccount.Address.AddressDetails.Country.fromDomain(): Award.Supplier.Details.BankAccount.Address.AddressDetails.Country =
            Award.Supplier.Details.BankAccount.Address.AddressDetails.Country(
                id = id,
                scheme = scheme,
                description = description,
                uri = uri
            )

        fun Details.BankAccount.Address.AddressDetails.Region.fromDomain(): Award.Supplier.Details.BankAccount.Address.AddressDetails.Region =
            Award.Supplier.Details.BankAccount.Address.AddressDetails.Region(
                id = id,
                scheme = scheme,
                description = description,
                uri = uri
            )

        fun Details.BankAccount.Address.AddressDetails.Locality.fromDomain(): Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality =
            Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality(
                id = id,
                scheme = scheme,
                description = description,
                uri = uri
            )

        fun Details.BankAccount.AccountIdentification.fromDomain(): Award.Supplier.Details.BankAccount.AccountIdentification =
            Award.Supplier.Details.BankAccount.AccountIdentification(id = id, scheme = scheme)

        fun MainEconomicActivity.fromDomain(): Award.Supplier.Details.MainEconomicActivity =
            Award.Supplier.Details.MainEconomicActivity(
                id = id,
                scheme = scheme,
                description = description,
                uri = uri
            )

        fun Details.LegalForm.fromDomain(): Award.Supplier.Details.LegalForm =
            Award.Supplier.Details.LegalForm(
                id = id,
                scheme = scheme,
                description = description,
                uri = uri
            )

        fun Details.Permit.fromDomain(): Award.Supplier.Details.Permit =
            Award.Supplier.Details.Permit(
                id = id,
                scheme = scheme,
                permitDetails = permitDetails.fromDomain(),
                url = url
            )

        fun Details.Permit.PermitDetails.fromDomain(): Award.Supplier.Details.Permit.PermitDetails =
            Award.Supplier.Details.Permit.PermitDetails(
                issuedBy = issuedBy.fromDomain(),
                issuedThought = issuedThought.fromDomain(),
                validityPeriod = validityPeriod.fromDomain()
            )

        fun Details.Permit.PermitDetails.IssuedBy.fromDomain(): Award.Supplier.Details.Permit.PermitDetails.IssuedBy =
            Award.Supplier.Details.Permit.PermitDetails.IssuedBy(id = id, name = name)

        fun Details.Permit.PermitDetails.IssuedThought.fromDomain(): Award.Supplier.Details.Permit.PermitDetails.IssuedThought =
            Award.Supplier.Details.Permit.PermitDetails.IssuedThought(id = id, name = name)

        fun Details.Permit.PermitDetails.ValidityPeriod.fromDomain(): Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod =
            Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod(
                startDate = startDate,
                endDate = endDate
            )
    }
}
