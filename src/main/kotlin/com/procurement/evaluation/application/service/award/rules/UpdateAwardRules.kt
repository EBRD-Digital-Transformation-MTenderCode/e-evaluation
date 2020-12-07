package com.procurement.evaluation.application.service.award.rules

import com.procurement.evaluation.application.model.award.update.UpdateAwardParams
import com.procurement.evaluation.infrastructure.handler.v2.converter.toDomain
import com.procurement.evaluation.lib.getElementsForUpdate
import com.procurement.evaluation.lib.getNewElements
import com.procurement.evaluation.lib.toSetBy
import com.procurement.evaluation.model.dto.ocds.Address
import com.procurement.evaluation.model.dto.ocds.AddressDetails
import com.procurement.evaluation.model.dto.ocds.Award
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

object UpdateAwardRules {
    fun update(target: Award, source: UpdateAwardParams.Award): Award {
        val updatedValue = source.value
            ?.let { target.value?.updateBy(it) ?: source.value.toDomain() }
            ?: target.value

        val updatedSuppliers = updateSuppliers(source.suppliers, target.suppliers.orEmpty())
        val updatedDocuments = updateAwardDocuments(source.documents, target.documents.orEmpty())

        return target.copy(
            internalId = source.internalId ?: target.internalId, // FR.COM-4.10.1
            description = source.description ?: target.description, // FR.COM-4.10.2
            value = updatedValue, // FR.COM-4.10.3
            suppliers = updatedSuppliers, // FR.COM-4.10.4, FR.COM-4.10.5
            documents = updatedDocuments
        )
    }

    fun <R, A, K> updateStrategy(
        receivedElements: List<R>,
        keyExtractorForReceivedElement: (R) -> K,
        availableElements: List<A>,
        keyExtractorForAvailableElement: (A) -> K,
        updateBlock: A.(R) -> A,
        createBlock: R.() -> A
    ): List<A> {

        val receivedIds = receivedElements.toSetBy { keyExtractorForReceivedElement(it) }
        val knownIds = availableElements.toSetBy { keyExtractorForAvailableElement(it) }

        val receivedElementsById = receivedElements.associateBy { keyExtractorForReceivedElement(it) }
        val storedElementsById = availableElements.associateBy { keyExtractorForAvailableElement(it) }

        val newElements = getNewElements(received = receivedIds, known = knownIds)
            .map { id -> receivedElementsById.getValue(id).createBlock() }

        val updatedElements = getElementsForUpdate(received = receivedIds, known = knownIds)
            .map { id ->
                val stored = storedElementsById.getValue(id)
                val received = receivedElementsById.getValue(id)
                stored.updateBlock(received)
            }

        return updatedElements + newElements
    }
}

private val receivedSupplierKeyExtractor: (UpdateAwardParams.Award.Supplier) -> String = { it.id }
private val storedSupplierKeyExtractor: (OrganizationReference) -> String = { it.id }
private fun updateSuppliers(
    received: List<UpdateAwardParams.Award.Supplier>,
    stored: List<OrganizationReference>
): List<OrganizationReference> =
    UpdateAwardRules.updateStrategy(
        received, receivedSupplierKeyExtractor,
        stored, storedSupplierKeyExtractor,
        OrganizationReference::updateBy,
        UpdateAwardParams.Award.Supplier::toDomain
    )

private val receivedIndentifierKeyExtractor: (UpdateAwardParams.Award.Supplier.Identifier) -> String = { it.id + it.scheme }
private val storedIdentifierKeyExtractor: (Identifier) -> String = { it.id + it.scheme }
private fun updateIdentifiers(
    received: List<UpdateAwardParams.Award.Supplier.Identifier>,
    stored: List<Identifier>
): List<Identifier> =
    UpdateAwardRules.updateStrategy(
        received, receivedIndentifierKeyExtractor,
        stored, storedIdentifierKeyExtractor,
        Identifier::updateBy,
        UpdateAwardParams.Award.Supplier.Identifier::toDomain
    )

private val receivedPersonKeyExtractor: (UpdateAwardParams.Award.Supplier.Person) -> String = { it.id }
private val storedPersonKeyExtractor: (OrganizationReference.Person) -> String = { it.id!! }
private fun updatePersons(
    received: List<UpdateAwardParams.Award.Supplier.Person>,
    stored: List<OrganizationReference.Person>
): List<OrganizationReference.Person> =
    UpdateAwardRules.updateStrategy(
        received, receivedPersonKeyExtractor,
        stored, storedPersonKeyExtractor,
        OrganizationReference.Person::updateBy,
        UpdateAwardParams.Award.Supplier.Person::toDomain
    )

private val receivedBusinessFunctionKeyExtractor: (UpdateAwardParams.Award.Supplier.Person.BusinessFunction) -> String = { it.id }
private val storedBusinessFunctionKeyExtractor: (OrganizationReference.Person.BusinessFunction) -> String = { it.id }
private fun updateBusinessFunctions(
    received: List<UpdateAwardParams.Award.Supplier.Person.BusinessFunction>,
    stored: List<OrganizationReference.Person.BusinessFunction>
): List<OrganizationReference.Person.BusinessFunction> =
    UpdateAwardRules.updateStrategy(
        received, receivedBusinessFunctionKeyExtractor,
        stored, storedBusinessFunctionKeyExtractor,
        OrganizationReference.Person.BusinessFunction::updateBy,
        UpdateAwardParams.Award.Supplier.Person.BusinessFunction::toDomain
    )

private val receivedBusinessFunctionDocumentKeyExtractor: (UpdateAwardParams.Award.Supplier.Person.BusinessFunction.Document) -> String = { it.id }
private val storedBusinessFunctionDocumentKeyExtractor: (OrganizationReference.Person.BusinessFunction.Document) -> String = { it.id }
private fun updateBusinessFunctionsDocuments(
    received: List<UpdateAwardParams.Award.Supplier.Person.BusinessFunction.Document>,
    stored: List<OrganizationReference.Person.BusinessFunction.Document>
): List<OrganizationReference.Person.BusinessFunction.Document> =
    UpdateAwardRules.updateStrategy(
        received, receivedBusinessFunctionDocumentKeyExtractor,
        stored, storedBusinessFunctionDocumentKeyExtractor,
        OrganizationReference.Person.BusinessFunction.Document::updateBy,
        UpdateAwardParams.Award.Supplier.Person.BusinessFunction.Document::toDomain
    )

private val receivedMainEconomicActivityKeyExtractor: (UpdateAwardParams.Award.Supplier.Details.MainEconomicActivity) -> String = { it.id + it.scheme }
private val storedMainEconomicActivityKeyExtractor: (MainEconomicActivity) -> String = { it.id + it.scheme }
private fun updateMainEconomicActivities(
    received: List<UpdateAwardParams.Award.Supplier.Details.MainEconomicActivity>,
    stored: List<MainEconomicActivity>
): List<MainEconomicActivity> =
    UpdateAwardRules.updateStrategy(
        received, receivedMainEconomicActivityKeyExtractor,
        stored, storedMainEconomicActivityKeyExtractor,
        MainEconomicActivity::updateBy,
        UpdateAwardParams.Award.Supplier.Details.MainEconomicActivity::toDomain
    )

private val receivedPermitsKeyExtractor: (UpdateAwardParams.Award.Supplier.Details.Permit) -> String = { it.id + it.scheme }
private val storedPermitsKeyExtractor: (Details.Permit) -> String = { it.id + it.scheme }
private fun updatePermits(
    received: List<UpdateAwardParams.Award.Supplier.Details.Permit>,
    stored: List<Details.Permit>
): List<Details.Permit> =
    UpdateAwardRules.updateStrategy(
        received, receivedPermitsKeyExtractor,
        stored, storedPermitsKeyExtractor,
        Details.Permit::updateBy,
        UpdateAwardParams.Award.Supplier.Details.Permit::toDomain
    )

private val receivedBankAccountsKeyExtractor: (UpdateAwardParams.Award.Supplier.Details.BankAccount) -> String = { it.identifier.id + it.identifier.scheme }
private val storedBankAccountsKeyExtractor: (Details.BankAccount) -> String = { it.identifier.id + it.identifier.scheme }
private fun updateBankAccounts(
    received: List<UpdateAwardParams.Award.Supplier.Details.BankAccount>,
    stored: List<Details.BankAccount>
): List<Details.BankAccount> =
    UpdateAwardRules.updateStrategy(
        received, receivedBankAccountsKeyExtractor,
        stored, storedBankAccountsKeyExtractor,
        Details.BankAccount::updateBy,
        UpdateAwardParams.Award.Supplier.Details.BankAccount::toDomain
    )

private val receivedAccountIdentifiersKeyExtractor: (UpdateAwardParams.Award.Supplier.Details.BankAccount.AccountIdentification) -> String = { it.id + it.scheme }
private val storedAccountIdentifiersKeyExtractor: (Details.BankAccount.AdditionalAccountIdentifier) -> String = { it.id + it.scheme }
private fun updateAccountIdentifiers(
    received: List<UpdateAwardParams.Award.Supplier.Details.BankAccount.AccountIdentification>,
    stored: List<Details.BankAccount.AdditionalAccountIdentifier>
): List<Details.BankAccount.AdditionalAccountIdentifier> =
    UpdateAwardRules.updateStrategy(
        received, receivedAccountIdentifiersKeyExtractor,
        stored, storedAccountIdentifiersKeyExtractor,
        Details.BankAccount.AdditionalAccountIdentifier::updateBy,
        UpdateAwardParams.Award.Supplier.Details.BankAccount.AccountIdentification::toDomain
    )

private val receivedAwardDocumentsKeyExtractor: (UpdateAwardParams.Award.Document) -> String = { it.id }
private val storedAwardDocumentsKeyExtractor: (Document) -> String = { it.id }
private fun updateAwardDocuments(
    received: List<UpdateAwardParams.Award.Document>,
    stored: List<Document>
): List<Document> =
    UpdateAwardRules.updateStrategy(
        received, receivedAwardDocumentsKeyExtractor,
        stored, storedAwardDocumentsKeyExtractor,
        Document::updateBy,
        UpdateAwardParams.Award.Document::toDomain
    )

private fun Document.updateBy(source: UpdateAwardParams.Award.Document): Document =
    this.copy(
        title = source.title, // FR.COM-4.10.30
        description = source.description ?: this.description, // FR.COM-4.10.31
    )


private fun OrganizationReference.updateBy(source: UpdateAwardParams.Award.Supplier): OrganizationReference {
    val updatedAdditionalIdentifiers = updateIdentifiers(source.additionalIdentifiers, this.additionalIdentifiers.orEmpty())
    val updateDetails = source.details
        .let { this.details?.updateBy(it) ?: source.details.toDomain() }

    return this.copy(
        name = source.name, // FR.COM-4.10.6
        identifier = this.identifier.updateBy(source.identifier), // FR.COM-4.10.6,
        additionalIdentifiers = updatedAdditionalIdentifiers.toMutableList(), // FR.COM-4.10.9
        address = this.address.updateBy(source.address), // FR.COM-4.10.10
        contactPoint = this.contactPoint.updateBy(source.contactPoint), // FR.COM-4.10.11
        persones = updatePersons(source.persons, this.persones.orEmpty()), // FR.COM-4.10.12
        details = updateDetails, // FR.COM-4.10.13
    )
}

private fun Details.updateBy(source: UpdateAwardParams.Award.Supplier.Details): Details {
    val updatedMainEconomicActivities = updateMainEconomicActivities(source.mainEconomicActivities, this.mainEconomicActivities.orEmpty())
    val updateLegalForm = source.legalForm
        ?.let { this.legalForm?.updateBy(it) ?: source.legalForm.toDomain() }
        ?: this.legalForm

    return this.copy(
        typeOfSupplier = source.typeOfSupplier ?: this.typeOfSupplier, // FR.COM-4.10.14
        mainEconomicActivities = updatedMainEconomicActivities, // FR.COM-4.10.15
        scale = source.scale.key, // FR.COM-4.10.16
        permits = updatePermits(source.permits, this.permits.orEmpty()), // FR.COM-4.10.17
        bankAccounts = updateBankAccounts(source.bankAccounts, this.bankAccounts.orEmpty()), // FR.COM-4.10.18
        legalForm = updateLegalForm, // FR.COM-4.10.27
    )
}

private fun Details.BankAccount.updateBy(source: UpdateAwardParams.Award.Supplier.Details.BankAccount): Details.BankAccount {
    val updatedAccountIdentifiers = updateAccountIdentifiers(source.additionalAccountIdentifiers, this.additionalAccountIdentifiers.orEmpty())

    return this.copy(
        description = source.description, // FR.COM-4.10.19
        bankName = source.bankName, // // FR.COM-4.10.20
        address = this.address.updateBy(source.address), // FR.COM-4.10.21
        identifier = this.identifier.updateBy(source.identifier),
        accountIdentification = this.accountIdentification.updateBy(source.accountIdentification),
        additionalAccountIdentifiers = updatedAccountIdentifiers // FR.COM-4.10.26
    )
}

private fun Details.LegalForm.updateBy(source: UpdateAwardParams.Award.Supplier.Details.LegalForm): Details.LegalForm =
    this.copy(
        description = source.description,
        uri = source.uri
    )

private fun Details.BankAccount.Identifier.updateBy(source: UpdateAwardParams.Award.Supplier.Details.BankAccount.Identifier): Details.BankAccount.Identifier =
    this.copy(
        id = source.id, // FR.COM-4.10.23
        scheme = source.scheme, // FR.COM-4.10.22
    )

private fun Details.BankAccount.AccountIdentification.updateBy(source: UpdateAwardParams.Award.Supplier.Details.BankAccount.AccountIdentification): Details.BankAccount.AccountIdentification =
    this.copy(
        id = source.id, // FR.COM-4.10.25
        scheme = source.scheme // FR.COM-4.10.24
    )

private fun UpdateAwardParams.Award.Supplier.Details.BankAccount.AccountIdentification.toDomain() : Details.BankAccount.AdditionalAccountIdentifier =
    Details.BankAccount.AdditionalAccountIdentifier(id = this.id, scheme = this.scheme)

private fun Details.BankAccount.AdditionalAccountIdentifier.updateBy(source: UpdateAwardParams.Award.Supplier.Details.BankAccount.AccountIdentification): Details.BankAccount.AdditionalAccountIdentifier =
    this.copy(
        id = source.id,
        scheme = source.scheme
    )


private fun Details.BankAccount.Address.updateBy(source: UpdateAwardParams.Award.Supplier.Details.BankAccount.Address): Details.BankAccount.Address =
    this.copy(
        streetAddress = source.streetAddress,
        postalCode = source.postalCode ?: this.postalCode,
        addressDetails = this.addressDetails.updateBy(source.addressDetails)
    )

private fun Details.BankAccount.Address.AddressDetails.updateBy(source: UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails): Details.BankAccount.Address.AddressDetails =
    this.copy(
        country = this.country.updateBy(source.country),
        region = this.region.updateBy(source.region),
        locality = this.locality.updateBy(source.locality)
    )

private fun Details.BankAccount.Address.AddressDetails.Country.updateBy(source: UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country): Details.BankAccount.Address.AddressDetails.Country =
    this.copy(
        id = source.id,
        scheme = source.scheme,
        description = source.description,
        uri = source.uri
    )

private fun Details.BankAccount.Address.AddressDetails.Region.updateBy(source: UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region): Details.BankAccount.Address.AddressDetails.Region =
    this.copy(
        id = source.id,
        scheme = source.scheme,
        description = source.description,
        uri = source.uri
    )

private fun Details.BankAccount.Address.AddressDetails.Locality.updateBy(source: UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality): Details.BankAccount.Address.AddressDetails.Locality =
    this.copy(
        id = source.id,
        scheme = source.scheme,
        description = source.description,
        uri = source.uri
    )

private fun MainEconomicActivity.updateBy(source: UpdateAwardParams.Award.Supplier.Details.MainEconomicActivity): MainEconomicActivity =
    this.copy(
        description = source.description,
        uri = source.uri ?: this.uri
    )

private fun Details.Permit.updateBy(source: UpdateAwardParams.Award.Supplier.Details.Permit): Details.Permit =
    this.copy(
        url = source.url ?: this.url,
        permitDetails = this.permitDetails.updateBy(source.permitDetails)
    )

private fun Details.Permit.PermitDetails.updateBy(source: UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails): Details.Permit.PermitDetails =
    this.copy(
        issuedBy = this.issuedBy.updateBy(source.issuedBy),
        issuedThought = this.issuedThought.updateBy(source.issuedThought),
        validityPeriod = this.validityPeriod.updateBy(source.validityPeriod)
    )

private fun Details.Permit.PermitDetails.IssuedBy.updateBy(source: UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedBy): Details.Permit.PermitDetails.IssuedBy =
    this.copy(
        id = source.id,
        name = source.name
    )

private fun Details.Permit.PermitDetails.IssuedThought.updateBy(source: UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedThought): Details.Permit.PermitDetails.IssuedThought =
    this.copy(
        id = source.id,
        name = source.name
    )

private fun Details.Permit.PermitDetails.ValidityPeriod.updateBy(source: UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod): Details.Permit.PermitDetails.ValidityPeriod =
    this.copy(
        startDate = source.startDate,
        endDate = source.endDate ?: this.endDate
    )

private fun OrganizationReference.Person.updateBy(source: UpdateAwardParams.Award.Supplier.Person): OrganizationReference.Person =
    this.copy(
        name = source.name,
        title = source.title,
        identifier = this.identifier.updateBy(source.identifier),
        businessFunctions = updateBusinessFunctions(source.businessFunctions, this.businessFunctions)
    )

private fun OrganizationReference.Person.BusinessFunction.updateBy(source: UpdateAwardParams.Award.Supplier.Person.BusinessFunction): OrganizationReference.Person.BusinessFunction =
    this.copy(
        type = source.type,
        jobTitle = source.jobTitle,
        period = this.period.updateBy(source.period),
        documents = updateBusinessFunctionsDocuments(source.documents, this.documents.orEmpty())
    )

private fun OrganizationReference.Person.BusinessFunction.Document.updateBy(source: UpdateAwardParams.Award.Supplier.Person.BusinessFunction.Document): OrganizationReference.Person.BusinessFunction.Document =
    this.copy(
        documentType = source.documentType.key,
        title = source.title,
        description = source.description ?: this.description
    )

private fun OrganizationReference.Person.BusinessFunction.Period.updateBy(source: UpdateAwardParams.Award.Supplier.Person.BusinessFunction.Period): OrganizationReference.Person.BusinessFunction.Period =
    this.copy(startDate = source.startDate)

private fun OrganizationReference.Person.Identifier.updateBy(source: UpdateAwardParams.Award.Supplier.Person.Identifier): OrganizationReference.Person.Identifier =
    this.copy(
        id = source.id,
        scheme = source.scheme,
        uri = source.uri ?: this.uri
    )

private fun Identifier.updateBy(source: UpdateAwardParams.Award.Supplier.Identifier): Identifier =
    this.copy(
        legalName = source.legalName, // FR.COM-4.10.7
        uri = source.uri ?: this.uri, // FR.COM-4.10.8
    )

private fun Value.updateBy(value: UpdateAwardParams.Award.Value): Value =
    this.copy(amount = value.amount ?: this.amount)

private fun Address.updateBy(source: UpdateAwardParams.Award.Supplier.Address): Address =
    this.copy(
        streetAddress = source.streetAddress,
        postalCode = source.postalCode ?: this.postalCode,
        addressDetails = this.addressDetails.updateBy(source.addressDetails)
    )

private fun AddressDetails.updateBy(source: UpdateAwardParams.Award.Supplier.Address.AddressDetails): AddressDetails =
    this.copy(
        country = this.country.updateBy(source.country),
        region = this.region.updateBy(source.region),
        locality = this.locality.updateBy(source.locality)
    )

private fun CountryDetails.updateBy(source: UpdateAwardParams.Award.Supplier.Address.AddressDetails.Country): CountryDetails =
    this.copy(
        id = source.id,
        scheme = source.scheme,
        description = source.description,
        uri = source.uri
    )

private fun RegionDetails.updateBy(source: UpdateAwardParams.Award.Supplier.Address.AddressDetails.Region): RegionDetails =
    this.copy(
        id = source.id,
        scheme = source.scheme,
        description = source.description,
        uri = source.uri
    )

private fun LocalityDetails.updateBy(source: UpdateAwardParams.Award.Supplier.Address.AddressDetails.Locality): LocalityDetails =
    this.copy(
        id = source.id,
        scheme = source.scheme,
        description = source.description,
        uri = source.uri
    )

private fun ContactPoint.updateBy(source: UpdateAwardParams.Award.Supplier.ContactPoint): ContactPoint =
    this.copy(
        name = source.name,
        email = source.email,
        telephone = source.telephone,
        faxNumber = source.faxNumber ?: this.faxNumber,
        url = source.url ?: this.url
    )