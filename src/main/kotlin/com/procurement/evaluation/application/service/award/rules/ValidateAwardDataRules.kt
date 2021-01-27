package com.procurement.evaluation.application.service.award.rules

import com.procurement.evaluation.application.model.award.validate.ValidateAwardDataParams
import com.procurement.evaluation.domain.model.enums.OperationType2
import com.procurement.evaluation.domain.util.extension.doOnFalse
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.fail.error.ValidationError
import com.procurement.evaluation.lib.functional.Validated
import com.procurement.evaluation.lib.functional.asValidationError
import com.procurement.evaluation.model.dto.ocds.Award
import java.math.BigDecimal

object ValidateAwardDataRules {

    object Value {

        fun validate(
            awardValue: ValidateAwardDataParams.Award.Value?,
            lotValue: ValidateAwardDataParams.Tender.Lot,
            operationType: OperationType2
        ): Validated<Failure> {
            awardValue?.amount?.let {
                // VR.COM-4.8.2
                checkAmount(it).onFailure { return it }

                // VR.COM-4.8.3
                checkAmountConsistency(awardValue.amount, lotValue.value.amount).onFailure { return it }

                // VR.COM-4.8.4
                val isCurrencyMissing = isCurrencyMissing(awardValue, operationType)
                if (isCurrencyMissing)
                    return ValidationError.ValidateAwardData.MissingCurrency().asValidationError()
            }

            awardValue?.currency?.let { currency ->
                // VR.COM-4.8.5
                checkCurrencyConsistency(currency, lotValue.value.currency)
                    .onFailure { return it }
            }

            return Validated.ok()
        }

        fun checkAmount(amount: BigDecimal): Validated<Failure> =
            if (amount <= BigDecimal.ZERO)
                ValidationError.ValidateAwardData.AmountLessOrEqThanZero(amount).asValidationError()
            else
                Validated.ok()

        fun checkAmountConsistency(awardValueAmount: BigDecimal, lotValueAmount: BigDecimal): Validated<Failure> =
            if (awardValueAmount > lotValueAmount)
                ValidationError.ValidateAwardData.AwardValueAmountMismatchWithLot(awardValueAmount, lotValueAmount)
                    .asValidationError()
            else
                Validated.ok()

        fun isCurrencyMissing(value: ValidateAwardDataParams.Award.Value?, operationType2: OperationType2): Boolean =
            when (operationType2) {
                OperationType2.CREATE_AWARD -> {
                    val currency = value?.currency
                    currency == null
                }

                OperationType2.APPLY_QUALIFICATION_PROTOCOL,
                OperationType2.CREATE_PCR,
                OperationType2.UPDATE_AWARD,
                OperationType2.CREATE_SUBMISSION,
                OperationType2.DECLARE_NON_CONFLICT_OF_INTEREST,
                OperationType2.LOT_CANCELLATION,
                OperationType2.PCR_PROTOCOL,
                OperationType2.SUBMISSION_PERIOD_END,
                OperationType2.TENDER_CANCELLATION,
                OperationType2.TENDER_OR_LOT_AMENDMENT_CANCELLATION,
                OperationType2.TENDER_OR_LOT_AMENDMENT_CONFIRMATION -> false
            }

        fun checkCurrencyConsistency(awardCurrency: String, lotCurrency: String): Validated<Failure> =
            if (awardCurrency != lotCurrency)
                ValidationError.ValidateAwardData.AwardValueCurrencyMismatchWithLot(awardCurrency, lotCurrency)
                    .asValidationError()
            else
                Validated.ok()
    }

    object Supplier {
        fun validate(award: ValidateAwardDataParams.Award): Validated<Failure> {
            award.suppliers.let { suppliers ->

                // VR.COM-4.8.6
                isIdsUniq(suppliers.map { it.id })
                    .doOnFalse { return ValidationError.ValidateAwardData.SupplierDuplicatedIds().asValidationError() }

                //VR.COM-4.8.7
                val additionalIdentifiers = suppliers.asSequence()
                    .flatMap { it.additionalIdentifiers.asSequence() }
                    .map { it.id to it.scheme }
                    .toList()

                isIdentifiersUniq(additionalIdentifiers)
                    .doOnFalse { return ValidationError.ValidateAwardData.SupplierIdentifiersDuplicatedIds().asValidationError() }

                //VR.COM-4.8.8
                val personsIds = suppliers.asSequence()
                    .flatMap { it.persons.asSequence() }
                    .map { it.id }
                    .toList()

                isIdsUniq(personsIds)
                    .doOnFalse { return ValidationError.ValidateAwardData.PersonsDuplicateIds().asValidationError() }

                // VR.COM-4.8.9
                val businessFunctionsIds = suppliers.asSequence()
                    .flatMap { it.persons.asSequence() }
                    .flatMap { it.businessFunctions.asSequence() }
                    .map { it.id }
                    .toList()

                isIdsUniq(businessFunctionsIds)
                    .doOnFalse { return ValidationError.ValidateAwardData.BusinessFunctionsDuplicateIds().asValidationError() }

                // VR.COM-4.8.10
                val documentsIdsByPerson = suppliers.asSequence()
                    .flatMap { it.persons.asSequence() }
                    .map { person ->
                        person.businessFunctions
                            .asSequence()
                            .flatMap { it.documents.asSequence() }
                            .map { it.id }
                            .toList()
                    }
                    .toList()

                documentsIdsByPerson.map { documentIds ->
                    isIdsUniq(documentIds)
                        .doOnFalse { return ValidationError.ValidateAwardData.BusinessFunctionsDocumentsDuplicateIds().asValidationError() }
                }

                // VR.COM-4.8.11
                val mainEconomicActivities = suppliers.asSequence()
                    .map { it.details }
                    .flatMap { it.mainEconomicActivities.asSequence() }
                    .map { it.id to it.scheme }
                    .toList()

                isIdentifiersUniq(mainEconomicActivities)
                    .doOnFalse { return ValidationError.ValidateAwardData.MainEconomicActivitiesDuplicateIds().asValidationError() }

                // VR.COM-4.8.13
                val permitsIdentifiers = suppliers.asSequence()
                    .map { it.details }
                    .flatMap { it.permits.asSequence() }
                    .map { it.id to it.scheme }
                    .toList()

                isIdentifiersUniq(permitsIdentifiers)
                    .doOnFalse { return ValidationError.ValidateAwardData.PermitsDuplicateIds().asValidationError() }

                //VR.COM-4.8.15
                val bankAccountsIdentifiers = suppliers.asSequence()
                    .map { it.details }
                    .flatMap { it.bankAccounts.asSequence() }
                    .map { it.identifier.id to it.identifier.scheme }
                    .toList()

                isIdentifiersUniq(bankAccountsIdentifiers)
                    .doOnFalse { return ValidationError.ValidateAwardData.BankAccountsDuplicatedIds().asValidationError() }

                //VR.COM-4.8.16
                val additionalAccountIdentifiers = suppliers.asSequence()
                    .map { it.details }
                    .flatMap { it.bankAccounts.asSequence() }
                    .flatMap { it.additionalAccountIdentifiers.asSequence() }
                    .map { it.id to it.scheme }
                    .toList()

                isIdentifiersUniq(additionalAccountIdentifiers)
                    .doOnFalse { return ValidationError.ValidateAwardData.AdditionalIdentifiersDuplicatedIds().asValidationError() }

                // VR.COM-4.8.14
                suppliers.asSequence()
                    .map { it.details }
                    .flatMap { it.permits.asSequence() }
                    .map { it.permitDetails.validityPeriod }
                    .forEach { validatePeriod(it).onFailure { return it } }

                //VR.COM-4.8.12
                val legalForms = suppliers.asSequence()
                    .mapNotNull { it.details.legalForm }
                    .map { it.id to it.scheme }
                    .toList()

                isIdentifiersUniq(legalForms)
                    .doOnFalse { return ValidationError.ValidateAwardData.LegalFormsDuplicateIds().asValidationError() }

            }

            return Validated.ok()
        }

        fun checkAwardAlreadyExists(storedAwards: List<Award>, receivedLot: String, receivedSuppliers: List<String>): Validated<Failure> {
            val sameAward = storedAwards.asSequence()
                .filter { it.relatedLots.contains(receivedLot) }
                .filter { award ->
                    val storedSuppliers = award.suppliers.orEmpty().map { it.id }
                    (storedSuppliers.size == receivedSuppliers.size) && (storedSuppliers.containsAll(receivedSuppliers))
                }
                .toList()

            return if (sameAward.isNotEmpty())
                ValidationError.ValidateAwardData.AwardAlreadyExists(receivedSuppliers).asValidationError()
            else
                Validated.ok()
        }
    }

    object Document {
        fun validate(documents: List<ValidateAwardDataParams.Award.Document>): Validated<Failure> {
            isIdsUniq(documents.map { it.id })
                .doOnFalse { return ValidationError.ValidateAwardData.AwardDocumentsDuplicatedIds().asValidationError() }

            return Validated.ok()
        }
    }

    fun isIdsUniq(ids: List<String>): Boolean = ids.size == ids.toSet().size

    fun isIdentifiersUniq(identifiers: List<Pair<String, String>>): Boolean {
        val uniqIds = identifiers.map { (id, scheme) -> id + scheme }
            .toSet()

        return identifiers.size == uniqIds.size
    }

    fun validatePeriod(period: ValidateAwardDataParams.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod): Validated<Failure> =
        period.endDate
            ?.let {
                if (period.startDate.isBefore(period.endDate))
                    Validated.ok()
                else
                    ValidationError.ValidateAwardData.InvalidValidityPeriod().asValidationError()
            }
            ?: Validated.ok()

    fun isNeedToCheckSuppliers(operationType2: OperationType2): Boolean =
        when (operationType2) {
            OperationType2.CREATE_AWARD -> true

            OperationType2.APPLY_QUALIFICATION_PROTOCOL,
            OperationType2.CREATE_PCR,
            OperationType2.UPDATE_AWARD,
            OperationType2.CREATE_SUBMISSION,
            OperationType2.DECLARE_NON_CONFLICT_OF_INTEREST,
            OperationType2.LOT_CANCELLATION,
            OperationType2.PCR_PROTOCOL,
            OperationType2.SUBMISSION_PERIOD_END,
            OperationType2.TENDER_CANCELLATION,
            OperationType2.TENDER_OR_LOT_AMENDMENT_CANCELLATION,
            OperationType2.TENDER_OR_LOT_AMENDMENT_CONFIRMATION -> false
        }
}