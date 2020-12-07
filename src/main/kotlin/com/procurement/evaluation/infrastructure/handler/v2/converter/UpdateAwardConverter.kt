package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.update.UpdateAwardParams
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.UpdateAwardRequest
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.model.dto.ocds.Address
import com.procurement.evaluation.model.dto.ocds.AddressDetails
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

fun UpdateAwardRequest.convert(): Result<UpdateAwardParams, DataErrors> =
    UpdateAwardParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        awards = awards.map {
            it.convert().onFailure { fail -> return fail }
        }
    )

fun UpdateAwardRequest.Award.convert(): Result<UpdateAwardParams.Award, DataErrors> =
    UpdateAwardParams.Award.tryCreate(
        id = id,
        internalId = internalId,
        description = description,
        value = value?.convert(),
        suppliers = suppliers.map {
            it.convert().onFailure { fail -> return fail }
        },
        documents = documents?.map {
            it.convert().onFailure { fail -> return fail }
        }
    )

fun UpdateAwardRequest.Award.Value.convert(): UpdateAwardParams.Award.Value =
    UpdateAwardParams.Award.Value(amount = amount)

fun UpdateAwardRequest.Award.Document.convert(): Result<UpdateAwardParams.Award.Document, DataErrors> =
    UpdateAwardParams.Award.Document.tryCreate(
        id = id,
        title = title,
        description = description,
        documentType = documentType
    )

fun UpdateAwardRequest.Award.Supplier.convert(): Result<UpdateAwardParams.Award.Supplier, DataErrors> =
    UpdateAwardParams.Award.Supplier.tryCreate(
        id = id,
        name = name,
        identifier = identifier.convert(),
        additionalIdentifiers = additionalIdentifiers?.map { it.convert() },
        address = address.convert(),
        contactPoint = contactPoint.convert(),
        details = details.convert().onFailure { return it },
        persons = persons?.map {
            it.convert().onFailure { fail -> return fail }
        }
    )

fun UpdateAwardRequest.Award.Supplier.Person.convert(): Result<UpdateAwardParams.Award.Supplier.Person, DataErrors> =
    UpdateAwardParams.Award.Supplier.Person(
        id = id,
        title = title,
        identifier = identifier.convert(),
        name = name,
        businessFunctions = businessFunctions.map {
            it.convert().onFailure { fail -> return fail }
        }
    ).asSuccess()

fun UpdateAwardRequest.Award.Supplier.Person.Identifier.convert(): UpdateAwardParams.Award.Supplier.Person.Identifier =
    UpdateAwardParams.Award.Supplier.Person.Identifier(
        id = id,
        scheme = scheme,
        uri = uri
    )

fun UpdateAwardRequest.Award.Supplier.Person.BusinessFunction.convert(): Result<UpdateAwardParams.Award.Supplier.Person.BusinessFunction, DataErrors> =
    UpdateAwardParams.Award.Supplier.Person.BusinessFunction.tryCreate(
        id = id,
        type = type,
        jobTitle = jobTitle,
        period = period.convert().onFailure { return it },
        documents = documents?.map {
            it.convert().onFailure { fail -> return fail }
        }
    )

fun UpdateAwardRequest.Award.Supplier.Person.BusinessFunction.Period.convert(): Result<UpdateAwardParams.Award.Supplier.Person.BusinessFunction.Period, DataErrors> =
    UpdateAwardParams.Award.Supplier.Person.BusinessFunction.Period.tryCreate(
        startDate = startDate
    )

fun UpdateAwardRequest.Award.Supplier.Person.BusinessFunction.Document.convert(): Result<UpdateAwardParams.Award.Supplier.Person.BusinessFunction.Document, DataErrors> =
    UpdateAwardParams.Award.Supplier.Person.BusinessFunction.Document.tryCreate(
        id = id,
        title = title,
        description = description,
        documentType = documentType
    )

fun UpdateAwardRequest.Award.Supplier.Identifier.convert(): UpdateAwardParams.Award.Supplier.Identifier =
    UpdateAwardParams.Award.Supplier.Identifier(
        id = id,
        scheme = scheme,
        legalName = legalName,
        uri = uri
    )

fun UpdateAwardRequest.Award.Supplier.Address.convert(): UpdateAwardParams.Award.Supplier.Address =
    UpdateAwardParams.Award.Supplier.Address(
        streetAddress = streetAddress,
        postalCode = postalCode,
        addressDetails = addressDetails.convert()
    )

fun UpdateAwardRequest.Award.Supplier.Address.AddressDetails.convert(): UpdateAwardParams.Award.Supplier.Address.AddressDetails =
    UpdateAwardParams.Award.Supplier.Address.AddressDetails(
        country = country.convert(),
        region = region.convert(),
        locality = locality.convert()
    )

fun UpdateAwardRequest.Award.Supplier.Address.AddressDetails.Country.convert(): UpdateAwardParams.Award.Supplier.Address.AddressDetails.Country =
    UpdateAwardParams.Award.Supplier.Address.AddressDetails.Country(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardRequest.Award.Supplier.Address.AddressDetails.Region.convert(): UpdateAwardParams.Award.Supplier.Address.AddressDetails.Region =
    UpdateAwardParams.Award.Supplier.Address.AddressDetails.Region(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardRequest.Award.Supplier.Address.AddressDetails.Locality.convert(): UpdateAwardParams.Award.Supplier.Address.AddressDetails.Locality =
    UpdateAwardParams.Award.Supplier.Address.AddressDetails.Locality(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardRequest.Award.Supplier.ContactPoint.convert(): UpdateAwardParams.Award.Supplier.ContactPoint =
    UpdateAwardParams.Award.Supplier.ContactPoint(
        name = name,
        email = email,
        telephone = telephone,
        faxNumber = faxNumber,
        url = url
    )

fun UpdateAwardRequest.Award.Supplier.Details.convert(): Result<UpdateAwardParams.Award.Supplier.Details, DataErrors> =
    UpdateAwardParams.Award.Supplier.Details.tryCreate(
        scale = scale,
        typeOfSupplier = typeOfSupplier,
        bankAccounts = bankAccounts?.map {
            it.convert().onFailure { fail -> return fail }
        },
        permits = permits?.map {
            it.convert().onFailure { fail -> return fail }
        },
        mainEconomicActivities = mainEconomicActivities?.map { it.convert() },
        legalForm = legalForm?.convert()
    )

fun UpdateAwardRequest.Award.Supplier.Details.BankAccount.convert(): Result<UpdateAwardParams.Award.Supplier.Details.BankAccount, DataErrors> =
    UpdateAwardParams.Award.Supplier.Details.BankAccount.tryCreate(
        bankName = bankName,
        description = description,
        identifier = identifier.convert(),
        address = address.convert(),
        accountIdentification = accountIdentification.convert(),
        additionalAccountIdentifiers = additionalAccountIdentifiers?.map { it.convert() }
    )

fun UpdateAwardRequest.Award.Supplier.Details.BankAccount.Identifier.convert(): UpdateAwardParams.Award.Supplier.Details.BankAccount.Identifier =
    UpdateAwardParams.Award.Supplier.Details.BankAccount.Identifier(id = id, scheme = scheme)

fun UpdateAwardRequest.Award.Supplier.Details.BankAccount.Address.convert(): UpdateAwardParams.Award.Supplier.Details.BankAccount.Address =
    UpdateAwardParams.Award.Supplier.Details.BankAccount.Address(
        streetAddress = streetAddress,
        postalCode = postalCode,
        addressDetails = addressDetails.convert()
    )

fun UpdateAwardRequest.Award.Supplier.Details.BankAccount.Address.AddressDetails.convert(): UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails =
    UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails(
        country = country.convert(),
        region = region.convert(),
        locality = locality.convert()
    )

fun UpdateAwardRequest.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country.convert(): UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country =
    UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardRequest.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region.convert(): UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region =
    UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardRequest.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality.convert(): UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality =
    UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardRequest.Award.Supplier.Details.BankAccount.AccountIdentification.convert(): UpdateAwardParams.Award.Supplier.Details.BankAccount.AccountIdentification =
    UpdateAwardParams.Award.Supplier.Details.BankAccount.AccountIdentification(id = id, scheme = scheme)

fun UpdateAwardRequest.Award.Supplier.Details.MainEconomicActivity.convert(): UpdateAwardParams.Award.Supplier.Details.MainEconomicActivity =
    UpdateAwardParams.Award.Supplier.Details.MainEconomicActivity(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardRequest.Award.Supplier.Details.LegalForm.convert(): UpdateAwardParams.Award.Supplier.Details.LegalForm =
    UpdateAwardParams.Award.Supplier.Details.LegalForm(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardRequest.Award.Supplier.Details.Permit.convert(): Result<UpdateAwardParams.Award.Supplier.Details.Permit, DataErrors> =
    UpdateAwardParams.Award.Supplier.Details.Permit(
        id = id,
        scheme = scheme,
        permitDetails = permitDetails.convert().onFailure { fail -> return fail },
        url = url
    ).asSuccess()

fun UpdateAwardRequest.Award.Supplier.Details.Permit.PermitDetails.convert(): Result<UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails, DataErrors> =
    UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails(
        issuedBy = issuedBy.convert(),
        issuedThought = issuedThought.convert(),
        validityPeriod = validityPeriod.convert().onFailure { fail -> return fail }
    ).asSuccess()

fun UpdateAwardRequest.Award.Supplier.Details.Permit.PermitDetails.IssuedBy.convert(): UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedBy =
    UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedBy(id = id, name = name)

fun UpdateAwardRequest.Award.Supplier.Details.Permit.PermitDetails.IssuedThought.convert(): UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedThought =
    UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedThought(id = id, name = name)

fun UpdateAwardRequest.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod.convert(): Result<UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod, DataErrors> =
    UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod.tryCreate(
        startDate = startDate,
        endDate = endDate
    )

fun UpdateAwardParams.Award.Value.toDomain(): Value = Value(amount = amount, currency = null)

fun UpdateAwardParams.Award.Document.toDomain(): Document =
    Document(
        id = id,
        title = title,
        description = description,
        documentType = documentType,
        relatedLots = emptyList()
    )

fun UpdateAwardParams.Award.Supplier.toDomain(): OrganizationReference =
    OrganizationReference(
        id = id,
        name = name,
        identifier = identifier.toDomain(),
        address = address.toDomain(),
        contactPoint = contactPoint.toDomain(),
        additionalIdentifiers = additionalIdentifiers.map { it.toDomain() }.toMutableList(),
        details = details.toDomain(),
        persones = persons.map { it.toDomain() }
    )

fun UpdateAwardParams.Award.Supplier.Person.toDomain(): OrganizationReference.Person =
    OrganizationReference.Person(
        id = id,
        title = title,
        name = name,
        identifier = identifier.toDomain(),
        businessFunctions = businessFunctions.map { it.toDomain() }
    )

fun UpdateAwardParams.Award.Supplier.Person.Identifier.toDomain(): OrganizationReference.Person.Identifier =
    OrganizationReference.Person.Identifier(
        id = id,
        scheme = scheme,
        uri = uri
    )

fun UpdateAwardParams.Award.Supplier.Person.BusinessFunction.toDomain(): OrganizationReference.Person.BusinessFunction =
    OrganizationReference.Person.BusinessFunction(
        id = id,
        type = type,
        jobTitle = jobTitle,
        period = period.toDomain(),
        documents = documents.map { it.toDomain() }
    )

fun UpdateAwardParams.Award.Supplier.Person.BusinessFunction.Period.toDomain(): OrganizationReference.Person.BusinessFunction.Period =
    OrganizationReference.Person.BusinessFunction.Period(
        startDate = startDate
    )

fun UpdateAwardParams.Award.Supplier.Person.BusinessFunction.Document.toDomain(): OrganizationReference.Person.BusinessFunction.Document =
    OrganizationReference.Person.BusinessFunction.Document(
        id = id,
        documentType = documentType.key,
        title = title,
        description = description
    )

fun UpdateAwardParams.Award.Supplier.Identifier.toDomain(): Identifier =
    Identifier(
        id = id,
        scheme = scheme,
        uri = uri,
        legalName = legalName
    )

fun UpdateAwardParams.Award.Supplier.Address.toDomain(): Address =
    Address(
        streetAddress = streetAddress,
        postalCode = postalCode,
        addressDetails = addressDetails.toDomain()
    )

fun UpdateAwardParams.Award.Supplier.Address.AddressDetails.toDomain(): AddressDetails =
    AddressDetails(
        country = country.toDomain(),
        region = region.toDomain(),
        locality = locality.toDomain()
    )

fun UpdateAwardParams.Award.Supplier.Address.AddressDetails.Country.toDomain(): CountryDetails =
    CountryDetails(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardParams.Award.Supplier.Address.AddressDetails.Region.toDomain(): RegionDetails =
    RegionDetails(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardParams.Award.Supplier.Address.AddressDetails.Locality.toDomain(): LocalityDetails =
    LocalityDetails(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardParams.Award.Supplier.ContactPoint.toDomain(): ContactPoint =
    ContactPoint(
        name = name,
        email = email,
        telephone = telephone,
        faxNumber = faxNumber,
        url = url
    )

fun UpdateAwardParams.Award.Supplier.Details.toDomain(): Details =
    Details(
        scale = scale.key,
        typeOfSupplier = typeOfSupplier,
        bankAccounts = bankAccounts.map { it.toDomain() },
        permits = permits.map { it.toDomain() },
        mainEconomicActivities = mainEconomicActivities.map { it.toDomain() },
        legalForm = legalForm?.toDomain()
    )

fun UpdateAwardParams.Award.Supplier.Details.BankAccount.toDomain(): Details.BankAccount =
    Details.BankAccount(
        bankName = bankName,
        description = description,
        identifier = identifier.toDomain(),
        address = address.toDomain(),
        accountIdentification = accountIdentification.toDomain(),
        additionalAccountIdentifiers = additionalAccountIdentifiers.map {
            Details.BankAccount.AdditionalAccountIdentifier(id = it.id, scheme = it.scheme)
        }
    )

fun UpdateAwardParams.Award.Supplier.Details.BankAccount.AccountIdentification.toDomain(): Details.BankAccount.AccountIdentification =
    Details.BankAccount.AccountIdentification(id = id, scheme = scheme)

fun UpdateAwardParams.Award.Supplier.Details.BankAccount.Identifier.toDomain(): Details.BankAccount.Identifier =
    Details.BankAccount.Identifier(
        id = id,
        scheme = scheme
    )

fun UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.toDomain(): Details.BankAccount.Address =
    Details.BankAccount.Address(
        streetAddress = streetAddress,
        postalCode = postalCode,
        addressDetails = addressDetails.toDomain()
    )

fun UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.toDomain(): Details.BankAccount.Address.AddressDetails =
    Details.BankAccount.Address.AddressDetails(
        country = country.toDomain(),
        region = region.toDomain(),
        locality = locality.toDomain()
    )

fun UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country.toDomain(): Details.BankAccount.Address.AddressDetails.Country =
    Details.BankAccount.Address.AddressDetails.Country(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region.toDomain(): Details.BankAccount.Address.AddressDetails.Region =
    Details.BankAccount.Address.AddressDetails.Region(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality.toDomain(): Details.BankAccount.Address.AddressDetails.Locality =
    Details.BankAccount.Address.AddressDetails.Locality(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardParams.Award.Supplier.Details.Permit.toDomain(): Details.Permit =
    Details.Permit(
        id = id,
        scheme = scheme,
        permitDetails = permitDetails.toDomain(),
        url = url
    )

fun UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.toDomain(): Details.Permit.PermitDetails =
    Details.Permit.PermitDetails(
        issuedBy = issuedBy.toDomain(),
        issuedThought = issuedThought.toDomain(),
        validityPeriod = validityPeriod.toDomain()
    )

fun UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedBy.toDomain(): Details.Permit.PermitDetails.IssuedBy =
    Details.Permit.PermitDetails.IssuedBy(id = id, name = name)

fun UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedThought.toDomain(): Details.Permit.PermitDetails.IssuedThought =
    Details.Permit.PermitDetails.IssuedThought(id = id, name = name)

fun UpdateAwardParams.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod.toDomain(): Details.Permit.PermitDetails.ValidityPeriod =
    Details.Permit.PermitDetails.ValidityPeriod(startDate = startDate, endDate = endDate)

fun UpdateAwardParams.Award.Supplier.Details.MainEconomicActivity.toDomain(): MainEconomicActivity =
    MainEconomicActivity(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun UpdateAwardParams.Award.Supplier.Details.LegalForm.toDomain(): Details.LegalForm =
    Details.LegalForm(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )
