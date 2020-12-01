package com.procurement.evaluation.infrastructure.fail.error

import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.infrastructure.fail.Failure
import java.math.BigDecimal

sealed class ValidationError(
    numberError: String, override val description: String, val id: String? = null
) : Failure.Error("VR-") {
    override val code: String = prefix + numberError

    class InvalidToken : ValidationError(
        numberError = "10.4.2.1",
        description = "Request token doesn't match token from the database."
    )

    class InvalidOwner : ValidationError(
        numberError = "10.4.2.2",
        description = "Request owner doesn't match owner from the database."
    )

    class AwardNotFoundOnCheckRelatedTenderer(id: AwardId) : ValidationError(
        numberError = "10.4.4.1",
        description = "Award not found.",
        id = id.toString()
    )

    class TendererNotLinkedToAwardOnCheckRelatedTenderer : ValidationError(
        numberError = "10.4.4.2",
        description = "Tenderer is not linked to award."
    )

    class DuplicateRequirementResponseOnCheckRelatedTenderer : ValidationError(
        numberError = "10.4.4.3",
        description = "Duplicate requirement response."
    )

    class AwardNotFoundOnCheckAccess(id: AwardId) : ValidationError(
        numberError = "10.4.2.3",
        description = "Award not found.",
        id = id.toString()
    )

    class AwardNotFoundOnAddRequirementRs(id: AwardId) : ValidationError(
        numberError = "10.4.3.1",
        description = "Award not found.",
        id = id.toString()
    )

    class AwardNotFoundOnGetAwardState(id: AwardId) : ValidationError(
        numberError = "10.4.1.1",
        description = "Award not found.",
        id = id.toString()
    )

    class PeriodNotFoundOnCloseAwardPeriod : ValidationError(
        numberError = "10.4.6.1",
        description = "Period not found."
    )

    object ValidateAwardData {
        class AwardAlreadyExists(suppliers: List<String>) : ValidationError(
            numberError = "4.8.1",
            description = "Award with suppliers (ids: $suppliers) already exist."
        )


        class AmountLessOrEqThanZero(amount: BigDecimal) : ValidationError(
            numberError = "4.8.2",
            description = "Amount must be greater than 0. Current value '$amount'."
        )

        class AwardValueAmountMismatchWithLot(awardValueAmount: BigDecimal, lotValueAmount: BigDecimal) : ValidationError(
            numberError = "4.8.3",
            description = "Amount in award must be less than amount in related lot. Award value's amount '$awardValueAmount', Lot value's amount '$lotValueAmount'."
        )

        class MissingCurrency : ValidationError(
            numberError = "4.8.4",
            description = "Missing 'currency' attribute in award's value."
        )

        class AwardValueCurrencyMismatchWithLot(awardCurrency: String, lotCurrency: String) : ValidationError(
            numberError = "4.8.5",
            description = "Currency in award must be equals to currency in related lot. Award currency '$awardCurrency', Lot currency '$lotCurrency'."
        )

        class SupplierDuplicatedIds: ValidationError(
            numberError = "4.8.6",
            description = "Award's suppliers contains duplicated ids."
        )

        class SupplierIdentifiersDuplicatedIds: ValidationError(
            numberError = "4.8.7",
            description = "Award's suppliers contains duplicated ids into additionalIdentifiers array."
        )

        class PersonsDuplicateIds: ValidationError(
            numberError = "4.8.8",
            description = "Suppliers contains duplicated ids into persons array."
        )

        class BusinessFunctionsDuplicateIds: ValidationError(
            numberError = "4.8.9",
            description = "Business functions contains duplicated ids."
        )

        class BusinessFunctionsDocumentsDuplicateIds: ValidationError(
            numberError = "4.8.10",
            description = "Business functions documents contains duplicated ids."
        )

        class MainEconomicActivitiesDuplicateIds: ValidationError(
            numberError = "4.8.11",
            description = "MainEconomicActivities contains duplicated identifiers."
        )

        class LegalFormsDuplicateIds: ValidationError(
            numberError = "4.8.12",
            description = "Duplicated identifiers in legalForm attributes."
        )

        class PermitsDuplicateIds: ValidationError(
            numberError = "4.8.13",
            description = "Duplicated identifiers in permits array."
        )

        class InvalidValidityPeriod: ValidationError(
            numberError = "4.8.14",
            description = "Invalid validity period. Start date cannot be greater than end date."
        )

        class BankAccountsDuplicatedIds: ValidationError(
            numberError = "4.8.15",
            description = "Duplicated ids in bankAccounts array."
        )

        class AdditionalIdentifiersDuplicatedIds: ValidationError(
            numberError = "4.8.16",
            description = "Duplicated ids in bankAccounts.additionalAccountIdentifiers array."
        )

        class AwardDocumentsDuplicatedIds: ValidationError(
            numberError = "4.8.17",
            description = "Duplicated ids in awards.documents array."
        )
    }
}
