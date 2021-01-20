package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.domain.model.data.CoefficientRate
import com.procurement.evaluation.domain.model.data.CoefficientValue
import com.procurement.evaluation.domain.model.data.RequirementRsValue
import com.procurement.evaluation.domain.model.document.DocumentId
import com.procurement.evaluation.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.evaluation.domain.model.enums.Scale
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.domain.model.money.Money
import com.procurement.evaluation.domain.model.requirement.RequirementId
import com.procurement.evaluation.domain.model.requirement.response.RequirementResponseId
import com.procurement.evaluation.model.dto.ocds.AwardCriteria
import com.procurement.evaluation.model.dto.ocds.AwardCriteriaDetails
import com.procurement.evaluation.model.dto.ocds.BidDocumentType
import com.procurement.evaluation.model.dto.ocds.BidStatusDetailsType
import com.procurement.evaluation.model.dto.ocds.BidStatusType
import com.procurement.evaluation.model.dto.ocds.BusinessFunctionType
import com.procurement.evaluation.model.dto.ocds.ConversionsRelatesTo
import com.procurement.evaluation.model.dto.ocds.TypeOfSupplier
import java.math.BigDecimal
import java.time.LocalDateTime

data class CreateAwardsAuctionEndData(
    val awardCriteria: AwardCriteria,
    val awardCriteriaDetails: AwardCriteriaDetails,
    val conversions: List<Conversion>,
    val bids: List<Bid>,
    val lots: List<Lot>,
    val electronicAuctions: ElectronicAuctions,
    val criteria: List<Criterion>
) {
    data class Conversion(
        val id: String,
        val relatesTo: ConversionsRelatesTo,
        val relatedItem: String,
        val rationale: String,
        val description: String?,
        val coefficients: List<Coefficient>
    ) {
        data class Coefficient(
            val id: String,
            val value: CoefficientValue,
            val coefficient: CoefficientRate
        )
    }

    data class Bid(
        val id: BidId,
        val date: LocalDateTime,
        val status: BidStatusType,
        val statusDetails: BidStatusDetailsType,
        val tenderers: List<Tenderer>,
        val value: Money,
        val documents: List<Document>,
        val requirementResponses: List<RequirementResponse>,
        val relatedLots: List<LotId>
    ) {
        data class Tenderer(
            val id: String,
            val name: String,
            val identifier: Identifier,
            val additionalIdentifiers: List<AdditionalIdentifier>,
            val address: Address,
            val contactPoint: ContactPoint,
            val persones: List<Person>,
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
                        val startDate: LocalDateTime
                    )

                    data class Document(
                        val id: String,
                        val documentType: BusinessFunctionDocumentType,
                        val title: String,
                        val description: String?
                    )
                }
            }

            data class Details(
                val typeOfSupplier: TypeOfSupplier?,
                val mainEconomicActivities: List<MainEconomicActivity>,
                val scale: Scale,
                val permits: List<Permit>,
                val bankAccounts: List<BankAccount>,
                val legalForm: LegalForm?
            ) {
                data class MainEconomicActivity(
                    val scheme: String,
                    val id: String,
                    val description: String,
                    val uri: String?
                )

                data class Permit(
                    val scheme: String,
                    val id: String,
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
                            val startDate: LocalDateTime,
                            val endDate: LocalDateTime?
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
        }

        data class Document(
            val documentType: BidDocumentType,
            val id: String,
            val title: String?,
            val description: String?,
            val relatedLots: List<LotId>
        )

        data class RequirementResponse(
            val id: RequirementResponseId,
            val value: RequirementRsValue,
            val requirement: Requirement,
            val period: Period?,
            val relatedTenderer: RelatedTenderer?,
            val evidences: List<Evidence>
        ) {
            data class Requirement(
                val id: RequirementId
            )

            data class Period(
                val startDate: LocalDateTime,
                val endDate: LocalDateTime
            )

            data class RelatedTenderer(
                val id: String,
                val name: String
            )

            data class Evidence(
                val id: String,
                val title: String,
                val description: String?,
                val relatedDocument: RelatedDocument?
            ) {
                data class RelatedDocument(
                    val id: DocumentId
                )
            }
        }
    }

    data class Lot(
        val id: LotId
    )

    data class ElectronicAuctions(
        val details: List<Detail>
    ) {
        data class Detail(
            val id: String,
            val relatedLot: LotId,
            val electronicAuctionResult: List<ElectronicAuctionResult>
        ) {
            data class ElectronicAuctionResult(
                val relatedBid: BidId,
                val value: Value
            ) {
                data class Value(
                    val amount: BigDecimal,
                    val currency: String?
                )
            }
        }
    }

    data class Criterion(
        val id: String,
        val title: String,
        val classification: Classification,
        val description: String?,
        val source: String,
        val relatesTo: String,
        val relatedItem: String?,
        val requirementGroups: List<RequirementGroup>,
    ) {
        data class RequirementGroup(
            val id: String,
            val description: String?,
            val requirements: List<Requirement>
        ) {
            data class Requirement(
                val id: RequirementId,
                val title: String,
                val description: String?,
                val status: String,
                val dataType: String,
                val datePublished: LocalDateTime,
                val eligibleEvidences: List<EligibleEvidence>?
            ) {
                data class EligibleEvidence(
                    val id: String,
                    val title: String,
                    val description: String?,
                    val type: String,
                    val relatedDocument: RelatedDocument?
                ) {
                    data class RelatedDocument(
                        val id: DocumentId
                    )
                }
            }
        }

        data class Classification(
            val id: String,
            val scheme: String
        )
    }
}
