package com.procurement.evaluation.application.model.award.create

import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseDate
import com.procurement.evaluation.application.model.parseEnum
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.domain.model.enums.BusinessFunctionDocumentType
import com.procurement.evaluation.domain.model.enums.Scale
import com.procurement.evaluation.domain.model.tryOwner
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.Result.Companion.failure
import com.procurement.evaluation.lib.functional.asFailure
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.lib.toSetBy
import com.procurement.evaluation.model.dto.ocds.BusinessFunctionType
import com.procurement.evaluation.model.dto.ocds.DocumentType
import com.procurement.evaluation.model.dto.ocds.TypeOfSupplier
import java.math.BigDecimal
import java.time.LocalDateTime

class CreateAwardParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val date: LocalDateTime,
    val owner: Owner,
    val tender: Tender,
    val awards: List<Award>
) {
    companion object {

        fun tryCreate(
            cpid: String,
            ocid: String,
            date: String,
            owner: String,
            tender: Tender,
            awards: List<Award>
        ): Result<CreateAwardParams, DataErrors> {
            val parsedCpid = parseCpid(cpid)
                .onFailure { return it }

            val parsedOcid = parseOcid(ocid)
                .onFailure { return it }

            val parsedOwner = owner.tryOwner()
                .onFailure {
                    return DataErrors.Validation.DataMismatchToPattern(name = "owner", pattern = "uuid", actualValue = owner)
                        .asFailure()
                }

            val parsedDate = parseDate(value = date, attributeName = "date")
                .onFailure { return it }

            if (awards.isEmpty())
                return failure(DataErrors.Validation.EmptyArray(name = "awards"))

            return CreateAwardParams(
                cpid = parsedCpid,
                ocid = parsedOcid,
                date = parsedDate,
                owner = parsedOwner,
                tender = tender,
                awards = awards
            ).asSuccess()
        }
    }

    class Tender private constructor(
        val lots: List<Lot>
    ) {
        companion object {
            fun tryCreate(lots: List<Lot>): Result<Tender, DataErrors> {

                if (lots.isEmpty())
                    return failure(DataErrors.Validation.EmptyArray(name = "tender.lots"))

                return Tender(lots = lots).asSuccess()
            }
        }

        data class Lot(
            val id: String
        )
    }

    class Award private constructor(
        val id: String,
        val internalId: String?,
        val description: String?,
        val value: Value?,
        val suppliers: List<Supplier>,
        val documents: List<Document>
    ) {

        companion object {
            fun tryCreate(
                id: String,
                internalId: String?,
                description: String?,
                value: Value?,
                suppliers: List<Supplier>,
                documents: List<Document>?
            ): Result<Award, DataErrors> {

                if (documents != null && documents.isEmpty())
                    return failure(DataErrors.Validation.EmptyArray(name = "awards[$id].documents"))

                if (suppliers.isEmpty())
                    return failure(DataErrors.Validation.EmptyArray(name = "awards[$id].suppliers"))

                return Award(
                    id = id,
                    internalId = internalId,
                    description = description,
                    value = value,
                    suppliers = suppliers,
                    documents = documents.orEmpty()
                ).asSuccess()
            }
        }

        data class Value(
            val amount: BigDecimal?,
            val currency: String?
        )

        class Supplier private constructor(
            val id: String,
            val name: String,
            val identifier: Identifier,
            val additionalIdentifiers: List<Identifier>,
            val address: Address,
            val contactPoint: ContactPoint,
            val persons: List<Person>,
            val details: Details
        ) {
            companion object {
                fun tryCreate(
                    id: String,
                    name: String,
                    identifier: Identifier,
                    additionalIdentifiers: List<Identifier>?,
                    address: Address,
                    contactPoint: ContactPoint,
                    persons: List<Person>?,
                    details: Details
                ): Result<Supplier, DataErrors> {

                    if (additionalIdentifiers != null && additionalIdentifiers.isEmpty())
                        return failure(DataErrors.Validation.EmptyArray(name = "suppliers[$id].additionalIdentifiers"))

                    if (persons != null && persons.isEmpty())
                        return failure(DataErrors.Validation.EmptyArray(name = "suppliers[$id].persons"))

                    return Supplier(
                        id = id,
                        name = name,
                        identifier = identifier,
                        additionalIdentifiers = additionalIdentifiers.orEmpty(),
                        address = address,
                        contactPoint = contactPoint,
                        details = details,
                        persons = persons.orEmpty()
                    ).asSuccess()
                }
            }

            data class Identifier(
                val id: String,
                val legalName: String,
                val scheme: String,
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
                    val locality: Locality,
                ) {
                    data class Country(
                        val id: String,
                        val description: String,
                        val scheme: String,
                        val uri: String
                    )

                    data class Region(
                        val id: String,
                        val description: String,
                        val scheme: String,
                        val uri: String
                    )

                    data class Locality(
                        val id: String,
                        val description: String,
                        val scheme: String,
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
                val id: String,
                val title: String,
                val name: String,
                val identifier: Identifier,
                val businessFunctions: List<BusinessFunction>
            ) {
                data class Identifier(
                    val id: String,
                    val scheme: String,
                    val uri: String?
                )

                data class BusinessFunction(
                    val id: String,
                    val type: BusinessFunctionType,
                    val jobTitle: String,
                    val period: Period,
                    val documents: List<Document>
                ) {
                    companion object {
                        private val allowedBusinessFuctionTypes = BusinessFunctionType.allowedElements
                            .filter {
                                when (it) {
                                    BusinessFunctionType.CHAIRMAN,
                                    BusinessFunctionType.CONTRACT_POINT,
                                    BusinessFunctionType.TECHNICAL_OPENER,
                                    BusinessFunctionType.PRICE_OPENER,
                                    BusinessFunctionType.PRICE_EVALUATOR,
                                    BusinessFunctionType.TECHNICAL_EVALUATOR,
                                    BusinessFunctionType.PROCUREMENT_OFFICER -> true

                                    BusinessFunctionType.AUTHORITY -> false
                                }
                            }
                            .toSetBy { it }

                        fun tryCreate(
                            id: String,
                            type: String,
                            jobTitle: String,
                            period: Period,
                            documents: List<Document>?
                        ): Result<BusinessFunction, DataErrors> {

                            if (documents != null && documents.isEmpty())
                                return failure(DataErrors.Validation.EmptyArray(name = "persones[$id].businessFunctions"))

                            val parsedBusinessFunctionType = parseEnum(
                                value = type,
                                target = BusinessFunctionType,
                                allowedEnums = allowedBusinessFuctionTypes,
                                attributeName = "type"
                            )
                                .onFailure { return it }

                            return BusinessFunction(
                                id = id,
                                type = parsedBusinessFunctionType,
                                jobTitle = jobTitle,
                                period = period,
                                documents = documents.orEmpty()
                            ).asSuccess()
                        }
                    }

                    class Period private constructor(
                        val startDate: LocalDateTime
                    ) {
                        companion object {
                            fun tryCreate(startDate: String): Result<Period, DataErrors> {

                                val parsedDate = parseDate(value = startDate, attributeName = "date")
                                    .onFailure { return it }

                                return Period(startDate = parsedDate).asSuccess()
                            }
                        }
                    }

                    data class Document(
                        val id: String,
                        val documentType: BusinessFunctionDocumentType,
                        val title: String,
                        val description: String?
                    ) {
                        companion object {
                            private val allowedDocumentsTypes = BusinessFunctionDocumentType.allowedElements
                                .filter {
                                    when (it) {
                                        BusinessFunctionDocumentType.REGULATORY_DOCUMENT -> true
                                    }
                                }
                                .toSetBy { it }

                            fun tryCreate(
                                id: String,
                                documentType: String,
                                title: String,
                                description: String?
                            ): Result<Document, DataErrors> {

                                val parsedDocumentType = parseEnum(
                                    value = documentType,
                                    target = BusinessFunctionDocumentType,
                                    allowedEnums = allowedDocumentsTypes,
                                    attributeName = "type"
                                )
                                    .onFailure { return it }

                                return Document(
                                    id = id,
                                    title = title,
                                    description = description,
                                    documentType = parsedDocumentType
                                ).asSuccess()
                            }
                        }
                    }
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
                companion object {
                    private val allowedTypesOfSupplier = TypeOfSupplier.allowedElements
                        .filter {
                            when (it) {
                                TypeOfSupplier.COMPANY,
                                TypeOfSupplier.INDIVIDUAL -> true
                            }
                        }
                        .toSetBy { it }

                    private val allowedScales = Scale.allowedElements
                        .filter {
                            when (it) {
                                Scale.EMPTY -> false
                                Scale.LARGE,
                                Scale.MICRO,
                                Scale.SME -> true
                            }
                        }
                        .toSetBy { it }

                    fun tryCreate(
                        typeOfSupplier: String?,
                        mainEconomicActivities: List<MainEconomicActivity>?,
                        scale: String,
                        permits: List<Permit>?,
                        bankAccounts: List<BankAccount>?,
                        legalForm: LegalForm?
                    ): Result<Details, DataErrors> {

                        if (mainEconomicActivities != null && mainEconomicActivities.isEmpty())
                            return failure(DataErrors.Validation.EmptyArray(name = "details.mainEconomicActivities"))

                        if (permits != null && permits.isEmpty())
                            return failure(DataErrors.Validation.EmptyArray(name = "details.permits"))

                        if (bankAccounts != null && bankAccounts.isEmpty())
                            return failure(DataErrors.Validation.EmptyArray(name = "details.bankAccounts"))

                        val parsedTypesOfSupplier = typeOfSupplier?.let {
                            parseEnum(
                                value = typeOfSupplier,
                                target = TypeOfSupplier,
                                allowedEnums = allowedTypesOfSupplier,
                                attributeName = "typeOfSupplier"
                            ).onFailure { return it }
                        }
                        val parsedScale = parseEnum(
                            value = scale,
                            target = Scale,
                            allowedEnums = allowedScales,
                            attributeName = "scale"
                        ).onFailure { return it }


                        return Details(
                            scale = parsedScale,
                            typeOfSupplier = parsedTypesOfSupplier,
                            bankAccounts = bankAccounts.orEmpty(),
                            permits = permits.orEmpty(),
                            mainEconomicActivities = mainEconomicActivities.orEmpty(),
                            legalForm = legalForm
                        ).asSuccess()
                    }
                }

                data class MainEconomicActivity(
                    val id: String,
                    val scheme: String,
                    val description: String,
                    val uri: String?
                )

                data class Permit(
                    val id: String,
                    val scheme: String,
                    val permitDetails: PermitDetails,
                    val url: String?
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
                        ) {
                            companion object {
                                fun tryCreate(startDate: String, endDate: String?): Result<ValidityPeriod, DataErrors> {

                                    val parsedStartDate = parseDate(value = startDate, attributeName = "startDate")
                                        .onFailure { return it }

                                    val parsedEndDate = endDate?.let {
                                        parseDate(value = endDate, attributeName = "endDate")
                                            .onFailure { return it }
                                    }

                                    return ValidityPeriod(
                                        startDate = parsedStartDate,
                                        endDate = parsedEndDate
                                    ).asSuccess()
                                }
                            }
                        }
                    }
                }

                data class BankAccount(
                    val description: String,
                    val bankName: String,
                    val address: Address,
                    val identifier: Identifier,
                    val accountIdentification: AccountIdentification,
                    val additionalAccountIdentifiers: List<AccountIdentification>
                ) {
                    companion object {
                        fun tryCreate(
                            description: String,
                            bankName: String,
                            address: Address,
                            identifier: Identifier,
                            accountIdentification: AccountIdentification,
                            additionalAccountIdentifiers: List<AccountIdentification>?
                        ): Result<BankAccount, DataErrors> {

                            if (additionalAccountIdentifiers != null && additionalAccountIdentifiers.isEmpty())
                                return failure(DataErrors.Validation.EmptyArray(name = "bankAccount[${identifier.id}-${identifier.scheme}].additionalAccountIdentifiers"))

                            return BankAccount(
                                bankName = bankName,
                                description = description,
                                identifier = identifier,
                                address = address,
                                accountIdentification = accountIdentification,
                                additionalAccountIdentifiers = additionalAccountIdentifiers.orEmpty()
                            ).asSuccess()
                        }
                    }

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
                                val id: String,
                                val description: String,
                                val scheme: String,
                                val uri: String
                            )

                            data class Region(
                                val id: String,
                                val description: String,
                                val scheme: String,
                                val uri: String
                            )

                            data class Locality(
                                val id: String,
                                val description: String,
                                val scheme: String,
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
                }

                data class LegalForm(
                    val scheme: String,
                    val id: String,
                    val description: String?,
                    val uri: String?
                )
            }
        }

        data class Document(
            val id: String,
            val title: String,
            val description: String?,
            val documentType: DocumentType
        ) {
            companion object {
                private val allowedDocumentsTypes = DocumentType.allowedElements
                    .filter {
                        when (it) {
                            DocumentType.AWARD_NOTICE,
                            DocumentType.EVALUATION_REPORTS,
                            DocumentType.CONTRACT_DRAFT,
                            DocumentType.WINNING_BID,
                            DocumentType.COMPLAINTS,
                            DocumentType.BIDDERS,
                            DocumentType.CONFLICT_OF_INTEREST,
                            DocumentType.CANCELLATION_DETAILS,
                            DocumentType.SUBMISSION_DOCUMENTS,
                            DocumentType.CONTRACT_ARRANGEMENTS,
                            DocumentType.CONTRACT_SCHEDULE,
                            DocumentType.SHORTLISTED_FIRMS -> true
                        }
                    }
                    .toSetBy { it }

                fun tryCreate(
                    id: String,
                    documentType: String,
                    title: String,
                    description: String?
                ): Result<Document, DataErrors> {

                    val parsedDocumentType = parseEnum(
                        value = documentType,
                        target = DocumentType,
                        allowedEnums = allowedDocumentsTypes,
                        attributeName = "documentType"
                    )
                        .onFailure { return it }

                    return Document(
                        id = id,
                        title = title,
                        description = description,
                        documentType = parsedDocumentType
                    ).asSuccess()
                }
            }
        }
    }
}