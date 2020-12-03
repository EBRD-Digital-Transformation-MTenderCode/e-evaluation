package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.create.CreateAwardParams
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.CreateAwardRequest
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

fun CreateAwardRequest.convert(): Result<CreateAwardParams, DataErrors> =
    CreateAwardParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        date = date,
        owner = owner,
        tender = tender.convert().onFailure { return it },
        awards = awards.map {
            it.convert().onFailure { fail -> return fail }
        }
    )

fun CreateAwardRequest.Tender.convert(): Result<CreateAwardParams.Tender, DataErrors> =
    CreateAwardParams.Tender.tryCreate(lots = lots.map { it.convert() })

fun CreateAwardRequest.Tender.Lot.convert(): CreateAwardParams.Tender.Lot =
    CreateAwardParams.Tender.Lot(id = id)

fun CreateAwardRequest.Award.convert(): Result<CreateAwardParams.Award, DataErrors> =
    CreateAwardParams.Award.tryCreate(
        id = id,
        internalId = internalId,
        description = description,
        value = value.convert(),
        suppliers = suppliers.map {
            it.convert().onFailure { fail -> return fail }
        },
        documents = documents?.map {
            it.convert().onFailure { fail -> return fail }
        }
    )

fun CreateAwardRequest.Award.Value.convert(): CreateAwardParams.Award.Value =
    CreateAwardParams.Award.Value(
        amount = amount,
        currency = currency
    )

fun CreateAwardRequest.Award.Document.convert(): Result<CreateAwardParams.Award.Document, DataErrors> =
    CreateAwardParams.Award.Document.tryCreate(
        id = id,
        title = title,
        description = description,
        documentType = documentType
    )

fun CreateAwardRequest.Award.Supplier.convert(): Result<CreateAwardParams.Award.Supplier, DataErrors> =
    CreateAwardParams.Award.Supplier.tryCreate(
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

fun CreateAwardRequest.Award.Supplier.Person.convert(): Result<CreateAwardParams.Award.Supplier.Person, DataErrors> =
    CreateAwardParams.Award.Supplier.Person(
        id = id,
        title = title,
        identifier = identifier.convert(),
        name = name,
        businessFunctions = businessFunctions.map {
            it.convert().onFailure { fail -> return fail }
        }
    ).asSuccess()

fun CreateAwardRequest.Award.Supplier.Person.Identifier.convert(): CreateAwardParams.Award.Supplier.Person.Identifier =
    CreateAwardParams.Award.Supplier.Person.Identifier(
        id = id,
        scheme = scheme,
        uri = uri
    )

fun CreateAwardRequest.Award.Supplier.Person.BusinessFunction.convert(): Result<CreateAwardParams.Award.Supplier.Person.BusinessFunction, DataErrors> =
    CreateAwardParams.Award.Supplier.Person.BusinessFunction.tryCreate(
        id = id,
        type = type,
        jobTitle = jobTitle,
        period = period.convert().onFailure { return it },
        documents = documents?.map {
            it.convert().onFailure { fail -> return fail }
        }
    )

fun CreateAwardRequest.Award.Supplier.Person.BusinessFunction.Period.convert(): Result<CreateAwardParams.Award.Supplier.Person.BusinessFunction.Period, DataErrors> =
    CreateAwardParams.Award.Supplier.Person.BusinessFunction.Period.tryCreate(
        startDate = startDate
    )

fun CreateAwardRequest.Award.Supplier.Person.BusinessFunction.Document.convert(): Result<CreateAwardParams.Award.Supplier.Person.BusinessFunction.Document, DataErrors> =
    CreateAwardParams.Award.Supplier.Person.BusinessFunction.Document.tryCreate(
        id = id,
        title = title,
        description = description,
        documentType = documentType
    )

fun CreateAwardRequest.Award.Supplier.Identifier.convert(): CreateAwardParams.Award.Supplier.Identifier =
    CreateAwardParams.Award.Supplier.Identifier(
        id = id,
        scheme = scheme,
        legalName = legalName,
        uri = uri
    )

fun CreateAwardRequest.Award.Supplier.Address.convert(): CreateAwardParams.Award.Supplier.Address =
    CreateAwardParams.Award.Supplier.Address(
        streetAddress = streetAddress,
        postalCode = postalCode,
        addressDetails = addressDetails.convert()
    )

fun CreateAwardRequest.Award.Supplier.Address.AddressDetails.convert(): CreateAwardParams.Award.Supplier.Address.AddressDetails =
    CreateAwardParams.Award.Supplier.Address.AddressDetails(
        country = country.convert(),
        region = region.convert(),
        locality = locality.convert()
    )

fun CreateAwardRequest.Award.Supplier.Address.AddressDetails.Country.convert(): CreateAwardParams.Award.Supplier.Address.AddressDetails.Country =
    CreateAwardParams.Award.Supplier.Address.AddressDetails.Country(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardRequest.Award.Supplier.Address.AddressDetails.Region.convert(): CreateAwardParams.Award.Supplier.Address.AddressDetails.Region =
    CreateAwardParams.Award.Supplier.Address.AddressDetails.Region(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardRequest.Award.Supplier.Address.AddressDetails.Locality.convert(): CreateAwardParams.Award.Supplier.Address.AddressDetails.Locality =
    CreateAwardParams.Award.Supplier.Address.AddressDetails.Locality(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardRequest.Award.Supplier.ContactPoint.convert(): CreateAwardParams.Award.Supplier.ContactPoint =
    CreateAwardParams.Award.Supplier.ContactPoint(
        name = name,
        email = email,
        telephone = telephone,
        faxNumber = faxNumber,
        url = url
    )

fun CreateAwardRequest.Award.Supplier.Details.convert(): Result<CreateAwardParams.Award.Supplier.Details, DataErrors> =
    CreateAwardParams.Award.Supplier.Details.tryCreate(
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

fun CreateAwardRequest.Award.Supplier.Details.BankAccount.convert(): Result<CreateAwardParams.Award.Supplier.Details.BankAccount, DataErrors> =
    CreateAwardParams.Award.Supplier.Details.BankAccount.tryCreate(
        bankName = bankName,
        description = description,
        identifier = identifier.convert(),
        address = address.convert(),
        accountIdentification = accountIdentification.convert(),
        additionalAccountIdentifiers = additionalAccountIdentifiers?.map { it.convert() }
    )

fun CreateAwardRequest.Award.Supplier.Details.BankAccount.Identifier.convert(): CreateAwardParams.Award.Supplier.Details.BankAccount.Identifier =
    CreateAwardParams.Award.Supplier.Details.BankAccount.Identifier(id = id, scheme = scheme)

fun CreateAwardRequest.Award.Supplier.Details.BankAccount.Address.convert(): CreateAwardParams.Award.Supplier.Details.BankAccount.Address =
    CreateAwardParams.Award.Supplier.Details.BankAccount.Address(
        streetAddress = streetAddress,
        postalCode = postalCode,
        addressDetails = addressDetails.convert()
    )

fun CreateAwardRequest.Award.Supplier.Details.BankAccount.Address.AddressDetails.convert(): CreateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails =
    CreateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails(
        country = country.convert(),
        region = region.convert(),
        locality = locality.convert()
    )

fun CreateAwardRequest.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country.convert(): CreateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country =
    CreateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardRequest.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region.convert(): CreateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region =
    CreateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardRequest.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality.convert(): CreateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality =
    CreateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardRequest.Award.Supplier.Details.BankAccount.AccountIdentification.convert(): CreateAwardParams.Award.Supplier.Details.BankAccount.AccountIdentification =
    CreateAwardParams.Award.Supplier.Details.BankAccount.AccountIdentification(id = id, scheme = scheme)

fun CreateAwardRequest.Award.Supplier.Details.MainEconomicActivity.convert(): CreateAwardParams.Award.Supplier.Details.MainEconomicActivity =
    CreateAwardParams.Award.Supplier.Details.MainEconomicActivity(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardRequest.Award.Supplier.Details.LegalForm.convert(): CreateAwardParams.Award.Supplier.Details.LegalForm =
    CreateAwardParams.Award.Supplier.Details.LegalForm(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardRequest.Award.Supplier.Details.Permit.convert(): Result<CreateAwardParams.Award.Supplier.Details.Permit, DataErrors> =
    CreateAwardParams.Award.Supplier.Details.Permit(
        id = id,
        scheme = scheme,
        permitDetails = permitDetails.convert().onFailure { fail -> return fail },
        url = url
    ).asSuccess()

fun CreateAwardRequest.Award.Supplier.Details.Permit.PermitDetails.convert(): Result<CreateAwardParams.Award.Supplier.Details.Permit.PermitDetails, DataErrors> =
    CreateAwardParams.Award.Supplier.Details.Permit.PermitDetails(
        issuedBy = issuedBy.convert(),
        issuedThought = issuedThought.convert(),
        validityPeriod = validityPeriod.convert().onFailure { fail -> return fail }
    ).asSuccess()

fun CreateAwardRequest.Award.Supplier.Details.Permit.PermitDetails.IssuedBy.convert(): CreateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedBy =
    CreateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedBy(id = id, name = name)

fun CreateAwardRequest.Award.Supplier.Details.Permit.PermitDetails.IssuedThought.convert(): CreateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedThought =
    CreateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedThought(id = id, name = name)

fun CreateAwardRequest.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod.convert(): Result<CreateAwardParams.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod, DataErrors> =
    CreateAwardParams.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod.tryCreate(
        startDate = startDate,
        endDate = endDate
    )

fun CreateAwardParams.Award.Value.toDomain(): Value = Value(amount = amount, currency = currency)

fun CreateAwardParams.Award.Document.toDomain(): Document =
    Document(
        id = id,
        title = title,
        description = description,
        documentType = documentType,
        relatedLots = emptyList()
    )

fun CreateAwardParams.Award.Supplier.toDomain(): OrganizationReference =
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

fun CreateAwardParams.Award.Supplier.Person.toDomain(): OrganizationReference.Person =
    OrganizationReference.Person(
        id = id,
        title = title,
        name = name,
        identifier = identifier.toDomain(),
        businessFunctions = businessFunctions.map { it.toDomain() }
    )

fun CreateAwardParams.Award.Supplier.Person.Identifier.toDomain(): OrganizationReference.Person.Identifier =
    OrganizationReference.Person.Identifier(
        id = id,
        scheme = scheme,
        uri = uri
    )

fun CreateAwardParams.Award.Supplier.Person.BusinessFunction.toDomain(): OrganizationReference.Person.BusinessFunction =
    OrganizationReference.Person.BusinessFunction(
        id = id,
        type = type,
        jobTitle = jobTitle,
        period = period.toDomain(),
        documents = documents.map { it.toDomain() }
    )

fun CreateAwardParams.Award.Supplier.Person.BusinessFunction.Period.toDomain(): OrganizationReference.Person.BusinessFunction.Period =
    OrganizationReference.Person.BusinessFunction.Period(
        startDate = startDate
    )

fun CreateAwardParams.Award.Supplier.Person.BusinessFunction.Document.toDomain(): OrganizationReference.Person.BusinessFunction.Document =
    OrganizationReference.Person.BusinessFunction.Document(
        id = id,
        documentType = documentType.key,
        title = title,
        description = description
    )

fun CreateAwardParams.Award.Supplier.Identifier.toDomain(): Identifier =
    Identifier(
        id = id,
        scheme = scheme,
        uri = uri,
        legalName = legalName
    )

fun CreateAwardParams.Award.Supplier.Address.toDomain(): Address =
    Address(
        streetAddress = streetAddress,
        postalCode = postalCode,
        addressDetails = addressDetails.toDomain()
    )

fun CreateAwardParams.Award.Supplier.Address.AddressDetails.toDomain(): AddressDetails =
    AddressDetails(
        country = country.toDomain(),
        region = region.toDomain(),
        locality = locality.toDomain()
    )

fun CreateAwardParams.Award.Supplier.Address.AddressDetails.Country.toDomain(): CountryDetails =
    CountryDetails(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardParams.Award.Supplier.Address.AddressDetails.Region.toDomain(): RegionDetails =
    RegionDetails(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardParams.Award.Supplier.Address.AddressDetails.Locality.toDomain(): LocalityDetails =
    LocalityDetails(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardParams.Award.Supplier.ContactPoint.toDomain(): ContactPoint =
    ContactPoint(
        name = name,
        email = email,
        telephone = telephone,
        faxNumber = faxNumber,
        url = url
    )

fun CreateAwardParams.Award.Supplier.Details.toDomain(): Details =
    Details(
        scale = scale.key,
        typeOfSupplier = typeOfSupplier,
        bankAccounts = bankAccounts.map { it.toDomain() },
        permits = permits.map { it.toDomain() },
        mainEconomicActivities = mainEconomicActivities.map { it.toDomain() },
        legalForm = legalForm?.toDomain()
    )

fun CreateAwardParams.Award.Supplier.Details.BankAccount.toDomain(): Details.BankAccount =
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

fun CreateAwardParams.Award.Supplier.Details.BankAccount.AccountIdentification.toDomain(): Details.BankAccount.AccountIdentification =
    Details.BankAccount.AccountIdentification(id = id, scheme = scheme)

fun CreateAwardParams.Award.Supplier.Details.BankAccount.Identifier.toDomain(): Details.BankAccount.Identifier =
    Details.BankAccount.Identifier(
        id = id,
        scheme = scheme
    )

fun CreateAwardParams.Award.Supplier.Details.BankAccount.Address.toDomain(): Details.BankAccount.Address =
    Details.BankAccount.Address(
        streetAddress = streetAddress,
        postalCode = postalCode,
        addressDetails = addressDetails.toDomain()
    )

fun CreateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.toDomain(): Details.BankAccount.Address.AddressDetails =
    Details.BankAccount.Address.AddressDetails(
        country = country.toDomain(),
        region = region.toDomain(),
        locality = locality.toDomain()
    )

fun CreateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country.toDomain(): Details.BankAccount.Address.AddressDetails.Country =
    Details.BankAccount.Address.AddressDetails.Country(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region.toDomain(): Details.BankAccount.Address.AddressDetails.Region =
    Details.BankAccount.Address.AddressDetails.Region(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality.toDomain(): Details.BankAccount.Address.AddressDetails.Locality =
    Details.BankAccount.Address.AddressDetails.Locality(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardParams.Award.Supplier.Details.Permit.toDomain(): Details.Permit =
    Details.Permit(
        id = id,
        scheme = scheme,
        permitDetails = permitDetails.toDomain(),
        url = url
    )

fun CreateAwardParams.Award.Supplier.Details.Permit.PermitDetails.toDomain(): Details.Permit.PermitDetails =
    Details.Permit.PermitDetails(
        issuedBy = issuedBy.toDomain(),
        issuedThought = issuedThought.toDomain(),
        validityPeriod = validityPeriod.toDomain()
    )

fun CreateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedBy.toDomain(): Details.Permit.PermitDetails.IssuedBy =
    Details.Permit.PermitDetails.IssuedBy(id = id, name = name)

fun CreateAwardParams.Award.Supplier.Details.Permit.PermitDetails.IssuedThought.toDomain(): Details.Permit.PermitDetails.IssuedThought =
    Details.Permit.PermitDetails.IssuedThought(id = id, name = name)

fun CreateAwardParams.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod.toDomain(): Details.Permit.PermitDetails.ValidityPeriod =
    Details.Permit.PermitDetails.ValidityPeriod(startDate = startDate.toString(), endDate = endDate.toString())

fun CreateAwardParams.Award.Supplier.Details.MainEconomicActivity.toDomain(): MainEconomicActivity =
    MainEconomicActivity(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun CreateAwardParams.Award.Supplier.Details.LegalForm.toDomain(): Details.LegalForm =
    Details.LegalForm(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )
