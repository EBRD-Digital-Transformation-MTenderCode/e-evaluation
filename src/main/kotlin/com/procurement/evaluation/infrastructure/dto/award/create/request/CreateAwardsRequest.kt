package com.procurement.evaluation.infrastructure.dto.award.create.request

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.procurement.evaluation.domain.model.data.CoefficientRate
import com.procurement.evaluation.domain.model.data.CoefficientValue
import com.procurement.evaluation.domain.model.data.RequirementRsValue
import com.procurement.evaluation.domain.model.money.Money
import com.procurement.evaluation.infrastructure.bind.coefficient.rate.CoefficientRateDeserializer
import com.procurement.evaluation.infrastructure.bind.coefficient.rate.CoefficientRateSerializer
import com.procurement.evaluation.infrastructure.bind.coefficient.value.CoefficientValueDeserializer
import com.procurement.evaluation.infrastructure.bind.coefficient.value.CoefficientValueSerializer
import com.procurement.evaluation.infrastructure.bind.criteria.RequirementValueDeserializer
import com.procurement.evaluation.infrastructure.bind.criteria.RequirementValueSerializer
import com.procurement.evaluation.infrastructure.bind.date.JsonDateTimeDeserializer
import com.procurement.evaluation.infrastructure.bind.date.JsonDateTimeSerializer
import com.procurement.evaluation.infrastructure.bind.money.MoneyDeserializer
import com.procurement.evaluation.infrastructure.bind.money.MoneySerializer
import com.procurement.evaluation.model.dto.ocds.AwardCriteria
import com.procurement.evaluation.model.dto.ocds.AwardCriteriaDetails
import com.procurement.evaluation.model.dto.ocds.BidDocumentType
import com.procurement.evaluation.model.dto.ocds.BidStatusDetailsType
import com.procurement.evaluation.model.dto.ocds.BidStatusType
import com.procurement.evaluation.model.dto.ocds.BusinessFunctionType
import com.procurement.evaluation.model.dto.ocds.ConversionsRelatesTo
import com.procurement.evaluation.model.dto.ocds.TypeOfSupplier
import java.time.LocalDateTime

data class CreateAwardsRequest(
    @param:JsonProperty("awardCriteria") @field:JsonProperty("awardCriteria") val awardCriteria: AwardCriteria,
    @param:JsonProperty("awardCriteriaDetails") @field:JsonProperty("awardCriteriaDetails") val awardCriteriaDetails: AwardCriteriaDetails,

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @param:JsonProperty("conversions") @field:JsonProperty("conversions") val conversions: List<Conversion>?,

    @param:JsonProperty("bids") @field:JsonProperty("bids") val bids: List<Bid>,
    @param:JsonProperty("lots") @field:JsonProperty("lots") val lots: List<Lot>
) {
    data class Conversion(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
        @param:JsonProperty("relatesTo") @field:JsonProperty("relatesTo") val relatesTo: ConversionsRelatesTo,
        @param:JsonProperty("relatedItem") @field:JsonProperty("relatedItem") val relatedItem: String,
        @param:JsonProperty("rationale") @field:JsonProperty("rationale") val rationale: String,

        @JsonInclude(JsonInclude.Include.NON_NULL)
        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,
        @param:JsonProperty("coefficients") @field:JsonProperty("coefficients") val coefficients: List<Coefficient>
    ) {
        data class Coefficient(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

            @JsonDeserialize(using = CoefficientValueDeserializer::class)
            @JsonSerialize(using = CoefficientValueSerializer::class)
            @param:JsonProperty("value") @field:JsonProperty("value") val value: CoefficientValue,

            @JsonDeserialize(using = CoefficientRateDeserializer::class)
            @JsonSerialize(using = CoefficientRateSerializer::class)
            @param:JsonProperty("coefficient") @field:JsonProperty("coefficient") val coefficient: CoefficientRate
        )
    }

    data class Bid(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

        @JsonDeserialize(using = JsonDateTimeDeserializer::class)
        @JsonSerialize(using = JsonDateTimeSerializer::class)
        @param:JsonProperty("date") @field:JsonProperty("date") val date: LocalDateTime,

        @param:JsonProperty("status") @field:JsonProperty("status") val status: BidStatusType,
        @param:JsonProperty("statusDetails") @field:JsonProperty("statusDetails") val statusDetails: BidStatusDetailsType,
        @param:JsonProperty("tenderers") @field:JsonProperty("tenderers") val tenderers: List<Tenderer>,

        @JsonDeserialize(using = MoneyDeserializer::class)
        @JsonSerialize(using = MoneySerializer::class)
        @param:JsonProperty("value") @field:JsonProperty("value") val value: Money,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?,

        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        @param:JsonProperty("requirementResponses") @field:JsonProperty("requirementResponses") val requirementResponses: List<RequirementResponse>?,

        @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<String>
    ) {
        data class Tenderer(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
            @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("additionalIdentifiers") @field:JsonProperty("additionalIdentifiers")
            val additionalIdentifiers: List<AdditionalIdentifier>?,

            @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
            @param:JsonProperty("contactPoint") @field:JsonProperty("contactPoint") val contactPoint: ContactPoint,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("persones") @field:JsonProperty("persones") val persones: List<Person>?,

            @param:JsonProperty("details") @field:JsonProperty("details") val details: Details
        ) {
            data class Identifier(
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            )

            data class AdditionalIdentifier(
                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                @param:JsonProperty("legalName") @field:JsonProperty("legalName") val legalName: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
            )

            data class Address(
                @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
            ) {
                data class AddressDetails(
                    @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                    @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                    @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                ) {
                    data class Country(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                    )

                    data class Region(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                    )

                    data class Locality(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                    )
                }
            }

            data class ContactPoint(
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                @param:JsonProperty("email") @field:JsonProperty("email") val email: String,
                @param:JsonProperty("telephone") @field:JsonProperty("telephone") val telephone: String,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("faxNumber") @field:JsonProperty("faxNumber") val faxNumber: String?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("url") @field:JsonProperty("url") val url: String?
            )

            data class Person(
                @param:JsonProperty("title") @field:JsonProperty("title") val title: String,
                @param:JsonProperty("name") @field:JsonProperty("name") val name: String,
                @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                @param:JsonProperty("businessFunctions") @field:JsonProperty("businessFunctions") val businessFunctions: List<BusinessFunction>
            ) {
                data class Identifier(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                )

                data class BusinessFunction(
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("type") @field:JsonProperty("type") val type: BusinessFunctionType,
                    @param:JsonProperty("jobTitle") @field:JsonProperty("jobTitle") val jobTitle: String,
                    @param:JsonProperty("period") @field:JsonProperty("period") val period: Period,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("documents") @field:JsonProperty("documents") val documents: List<Document>?
                ) {
                    data class Period(
                        @JsonDeserialize(using = JsonDateTimeDeserializer::class)
                        @JsonSerialize(using = JsonDateTimeSerializer::class)
                        @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime
                    )

                    data class Document(
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                        @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: String,
                        @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("description") @field:JsonProperty("description") val description: String?
                    )
                }
            }

            data class Details(
                @param:JsonProperty("typeOfSupplier") @field:JsonProperty("typeOfSupplier") val typeOfSupplier: TypeOfSupplier,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("mainEconomicActivities") @field:JsonProperty("mainEconomicActivities") val mainEconomicActivities: List<MainEconomicActivity>?,
                @param:JsonProperty("scale") @field:JsonProperty("scale") val scale: String,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("permits") @field:JsonProperty("permits") val permits: List<Permit>?,

                @JsonInclude(JsonInclude.Include.NON_EMPTY)
                @param:JsonProperty("bankAccounts") @field:JsonProperty("bankAccounts") val bankAccounts: List<BankAccount>?,

                @JsonInclude(JsonInclude.Include.NON_NULL)
                @param:JsonProperty("legalForm") @field:JsonProperty("legalForm") val legalForm: LegalForm?
            ) {
                data class MainEconomicActivity(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                )

                data class Permit(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("url") @field:JsonProperty("url") val url: String?,

                    @param:JsonProperty("permitDetails") @field:JsonProperty("permitDetails") val permitDetails: PermitDetails
                ) {
                    data class PermitDetails(
                        @param:JsonProperty("issuedBy") @field:JsonProperty("issuedBy") val issuedBy: IssuedBy,
                        @param:JsonProperty("issuedThought") @field:JsonProperty("issuedThought") val issuedThought: IssuedThought,
                        @param:JsonProperty("validityPeriod") @field:JsonProperty("validityPeriod") val validityPeriod: ValidityPeriod
                    ) {
                        data class IssuedBy(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                        )

                        data class IssuedThought(
                            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                            @param:JsonProperty("name") @field:JsonProperty("name") val name: String
                        )

                        data class ValidityPeriod(
                            @JsonDeserialize(using = JsonDateTimeDeserializer::class)
                            @JsonSerialize(using = JsonDateTimeSerializer::class)
                            @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,

                            @JsonInclude(JsonInclude.Include.NON_NULL)
                            @JsonDeserialize(using = JsonDateTimeDeserializer::class)
                            @JsonSerialize(using = JsonDateTimeSerializer::class)
                            @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime?
                        )
                    }
                }

                data class BankAccount(
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                    @param:JsonProperty("bankName") @field:JsonProperty("bankName") val bankName: String,
                    @param:JsonProperty("address") @field:JsonProperty("address") val address: Address,
                    @param:JsonProperty("identifier") @field:JsonProperty("identifier") val identifier: Identifier,
                    @param:JsonProperty("accountIdentification") @field:JsonProperty("accountIdentification") val accountIdentification: AccountIdentification,

                    @JsonInclude(JsonInclude.Include.NON_EMPTY)
                    @param:JsonProperty("additionalAccountIdentifiers") @field:JsonProperty("additionalAccountIdentifiers") val additionalAccountIdentifiers: List<AdditionalAccountIdentifier>?
                ) {
                    data class Address(
                        @param:JsonProperty("streetAddress") @field:JsonProperty("streetAddress") val streetAddress: String,

                        @JsonInclude(JsonInclude.Include.NON_NULL)
                        @param:JsonProperty("postalCode") @field:JsonProperty("postalCode") val postalCode: String?,

                        @param:JsonProperty("addressDetails") @field:JsonProperty("addressDetails") val addressDetails: AddressDetails
                    ) {
                        data class AddressDetails(
                            @param:JsonProperty("country") @field:JsonProperty("country") val country: Country,
                            @param:JsonProperty("region") @field:JsonProperty("region") val region: Region,
                            @param:JsonProperty("locality") @field:JsonProperty("locality") val locality: Locality
                        ) {
                            data class Country(
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                            )

                            data class Region(
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String
                            )

                            data class Locality(
                                @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                                @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                                @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                                @JsonInclude(JsonInclude.Include.NON_NULL)
                                @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                            )
                        }
                    }

                    data class Identifier(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                    )

                    data class AccountIdentification(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                    )

                    data class AdditionalAccountIdentifier(
                        @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
                    )
                }

                data class LegalForm(
                    @param:JsonProperty("scheme") @field:JsonProperty("scheme") val scheme: String,
                    @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
                    @param:JsonProperty("description") @field:JsonProperty("description") val description: String,

                    @JsonInclude(JsonInclude.Include.NON_NULL)
                    @param:JsonProperty("uri") @field:JsonProperty("uri") val uri: String?
                )
            }
        }

        data class Document(
            @param:JsonProperty("documentType") @field:JsonProperty("documentType") val documentType: BidDocumentType,
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String?,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            @param:JsonProperty("relatedLots") @field:JsonProperty("relatedLots") val relatedLots: List<String>?
        )

        data class RequirementResponse(
            @param:JsonProperty("id") @field:JsonProperty("id") val id: String,
            @param:JsonProperty("title") @field:JsonProperty("title") val title: String,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("description") @field:JsonProperty("description") val description: String?,

            @JsonDeserialize(using = RequirementValueDeserializer::class)
            @JsonSerialize(using = RequirementValueSerializer::class)
            @param:JsonProperty("value") @field:JsonProperty("value") val value: RequirementRsValue,
            @param:JsonProperty("requirement") @field:JsonProperty("requirement") val requirement: Requirement,

            @JsonInclude(JsonInclude.Include.NON_NULL)
            @param:JsonProperty("period") @field:JsonProperty("period") val period: Period?
        ) {
            data class Requirement(
                @param:JsonProperty("id") @field:JsonProperty("id") val id: String
            )

            data class Period(
                @JsonDeserialize(using = JsonDateTimeDeserializer::class)
                @JsonSerialize(using = JsonDateTimeSerializer::class)
                @param:JsonProperty("startDate") @field:JsonProperty("startDate") val startDate: LocalDateTime,

                @JsonDeserialize(using = JsonDateTimeDeserializer::class)
                @JsonSerialize(using = JsonDateTimeSerializer::class)
                @param:JsonProperty("endDate") @field:JsonProperty("endDate") val endDate: LocalDateTime
            )
        }
    }

    data class Lot(
        @param:JsonProperty("id") @field:JsonProperty("id") val id: String
    )
}