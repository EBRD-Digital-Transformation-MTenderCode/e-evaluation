package com.procurement.evaluation.infrastructure.handler.v2.converter

import com.procurement.evaluation.application.model.award.validate.ValidateAwardDataParams
import com.procurement.evaluation.application.model.award.validate.ValidateAwardDataParams.Tender
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.infrastructure.handler.v2.model.request.ValidateAwardDataRequest
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess

fun ValidateAwardDataRequest.convert(): Result<ValidateAwardDataParams, DataErrors> =
    ValidateAwardDataParams.tryCreate(
        cpid = cpid,
        ocid = ocid,
        operationType = operationType,
        tender = tender.convert().onFailure { return it },
        awards = awards.map {
            it.convert().onFailure { fail -> return fail }
        }
    )

fun ValidateAwardDataRequest.Tender.convert(): Result<Tender, DataErrors> =
    Tender.tryCreate(lots = lots.map { it.convert() })

fun ValidateAwardDataRequest.Tender.Lot.convert(): Tender.Lot =
    Tender.Lot(id = id, value = value.convert())

fun ValidateAwardDataRequest.Tender.Lot.Value.convert(): Tender.Lot.Value =
    Tender.Lot.Value(amount = amount, currency = currency)

fun ValidateAwardDataRequest.Award.convert(): Result<ValidateAwardDataParams.Award, DataErrors> =
    ValidateAwardDataParams.Award.tryCreate(
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

fun ValidateAwardDataRequest.Award.Value.convert(): ValidateAwardDataParams.Award.Value =
    ValidateAwardDataParams.Award.Value(
        amount = amount,
        currency = currency
    )

fun ValidateAwardDataRequest.Award.Document.convert(): Result<ValidateAwardDataParams.Award.Document, DataErrors> =
    ValidateAwardDataParams.Award.Document.tryCreate(
        id = id,
        title = title,
        description = description,
        documentType = documentType
    )

fun ValidateAwardDataRequest.Award.Supplier.convert(): Result<ValidateAwardDataParams.Award.Supplier, DataErrors> =
    ValidateAwardDataParams.Award.Supplier.tryCreate(
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

fun ValidateAwardDataRequest.Award.Supplier.Person.convert(): Result<ValidateAwardDataParams.Award.Supplier.Person, DataErrors> =
    ValidateAwardDataParams.Award.Supplier.Person(
        id = id,
        title = title,
        identifier = identifier.convert(),
        name = name,
        businessFunctions = businessFunctions.map {
            it.convert().onFailure { fail -> return fail }
        }
    ).asSuccess()

fun ValidateAwardDataRequest.Award.Supplier.Person.Identifier.convert(): ValidateAwardDataParams.Award.Supplier.Person.Identifier =
    ValidateAwardDataParams.Award.Supplier.Person.Identifier(
        id = id,
        scheme = scheme,
        uri = uri
    )

fun ValidateAwardDataRequest.Award.Supplier.Person.BusinessFunction.convert(): Result<ValidateAwardDataParams.Award.Supplier.Person.BusinessFunction, DataErrors> =
    ValidateAwardDataParams.Award.Supplier.Person.BusinessFunction.tryCreate(
        id = id,
        type = type,
        jobTitle = jobTitle,
        period = period.convert().onFailure { return it },
        documents = documents?.map {
            it.convert().onFailure { fail -> return fail }
        }
    )

fun ValidateAwardDataRequest.Award.Supplier.Person.BusinessFunction.Period.convert(): Result<ValidateAwardDataParams.Award.Supplier.Person.BusinessFunction.Period, DataErrors> =
    ValidateAwardDataParams.Award.Supplier.Person.BusinessFunction.Period.tryCreate(
        startDate = startDate
    )

fun ValidateAwardDataRequest.Award.Supplier.Person.BusinessFunction.Document.convert(): Result<ValidateAwardDataParams.Award.Supplier.Person.BusinessFunction.Document, DataErrors> =
    ValidateAwardDataParams.Award.Supplier.Person.BusinessFunction.Document.tryCreate(
        id = id,
        title = title,
        description = description,
        documentType = documentType
    )

fun ValidateAwardDataRequest.Award.Supplier.Identifier.convert(): ValidateAwardDataParams.Award.Supplier.Identifier =
    ValidateAwardDataParams.Award.Supplier.Identifier(
        id = id,
        scheme = scheme,
        legalName = legalName,
        uri = uri
    )

fun ValidateAwardDataRequest.Award.Supplier.Address.convert(): ValidateAwardDataParams.Award.Supplier.Address =
    ValidateAwardDataParams.Award.Supplier.Address(
        streetAddress = streetAddress,
        postalCode = postalCode,
        addressDetails = addressDetails.convert()
    )

fun ValidateAwardDataRequest.Award.Supplier.Address.AddressDetails.convert(): ValidateAwardDataParams.Award.Supplier.Address.AddressDetails =
    ValidateAwardDataParams.Award.Supplier.Address.AddressDetails(
        country = country.convert(),
        region = region.convert(),
        locality = locality.convert()
    )

fun ValidateAwardDataRequest.Award.Supplier.Address.AddressDetails.Country.convert(): ValidateAwardDataParams.Award.Supplier.Address.AddressDetails.Country =
    ValidateAwardDataParams.Award.Supplier.Address.AddressDetails.Country(
        id = id,
        scheme = scheme,
        description = description
    )

fun ValidateAwardDataRequest.Award.Supplier.Address.AddressDetails.Region.convert(): ValidateAwardDataParams.Award.Supplier.Address.AddressDetails.Region =
    ValidateAwardDataParams.Award.Supplier.Address.AddressDetails.Region(
        id = id,
        scheme = scheme,
        description = description,
    )

fun ValidateAwardDataRequest.Award.Supplier.Address.AddressDetails.Locality.convert(): ValidateAwardDataParams.Award.Supplier.Address.AddressDetails.Locality =
    ValidateAwardDataParams.Award.Supplier.Address.AddressDetails.Locality(
        id = id,
        scheme = scheme,
        description = description
    )

fun ValidateAwardDataRequest.Award.Supplier.ContactPoint.convert(): ValidateAwardDataParams.Award.Supplier.ContactPoint =
    ValidateAwardDataParams.Award.Supplier.ContactPoint(
        name = name,
        email = email,
        telephone = telephone,
        faxNumber = faxNumber,
        url = url
    )

fun ValidateAwardDataRequest.Award.Supplier.Details.convert(): Result<ValidateAwardDataParams.Award.Supplier.Details, DataErrors> =
    ValidateAwardDataParams.Award.Supplier.Details.tryCreate(
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

fun ValidateAwardDataRequest.Award.Supplier.Details.BankAccount.convert(): Result<ValidateAwardDataParams.Award.Supplier.Details.BankAccount, DataErrors> =
    ValidateAwardDataParams.Award.Supplier.Details.BankAccount.tryCreate(
        bankName = bankName,
        description = description,
        identifier = identifier.convert(),
        address = address.convert(),
        accountIdentification = accountIdentification.convert(),
        additionalAccountIdentifiers = additionalAccountIdentifiers?.map { it.convert() }
    )

fun ValidateAwardDataRequest.Award.Supplier.Details.BankAccount.Identifier.convert(): ValidateAwardDataParams.Award.Supplier.Details.BankAccount.Identifier =
    ValidateAwardDataParams.Award.Supplier.Details.BankAccount.Identifier(id = id, scheme = scheme)

fun ValidateAwardDataRequest.Award.Supplier.Details.BankAccount.Address.convert(): ValidateAwardDataParams.Award.Supplier.Details.BankAccount.Address =
    ValidateAwardDataParams.Award.Supplier.Details.BankAccount.Address(
        streetAddress = streetAddress,
        postalCode = postalCode,
        addressDetails = addressDetails.convert()
    )

fun ValidateAwardDataRequest.Award.Supplier.Details.BankAccount.Address.AddressDetails.convert(): ValidateAwardDataParams.Award.Supplier.Details.BankAccount.Address.AddressDetails =
    ValidateAwardDataParams.Award.Supplier.Details.BankAccount.Address.AddressDetails(
        country = country.convert(),
        region = region.convert(),
        locality = locality.convert()
    )

fun ValidateAwardDataRequest.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country.convert(): ValidateAwardDataParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country =
    ValidateAwardDataParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Country(
        id = id,
        scheme = scheme,
        description = description
    )

fun ValidateAwardDataRequest.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region.convert(): ValidateAwardDataParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region =
    ValidateAwardDataParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Region(
        id = id,
        scheme = scheme,
        description = description,
    )

fun ValidateAwardDataRequest.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality.convert(): ValidateAwardDataParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality =
    ValidateAwardDataParams.Award.Supplier.Details.BankAccount.Address.AddressDetails.Locality(
        id = id,
        scheme = scheme,
        description = description
    )

fun ValidateAwardDataRequest.Award.Supplier.Details.BankAccount.AccountIdentification.convert(): ValidateAwardDataParams.Award.Supplier.Details.BankAccount.AccountIdentification =
    ValidateAwardDataParams.Award.Supplier.Details.BankAccount.AccountIdentification(id = id, scheme = scheme)

fun ValidateAwardDataRequest.Award.Supplier.Details.MainEconomicActivity.convert(): ValidateAwardDataParams.Award.Supplier.Details.MainEconomicActivity =
    ValidateAwardDataParams.Award.Supplier.Details.MainEconomicActivity(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun ValidateAwardDataRequest.Award.Supplier.Details.LegalForm.convert(): ValidateAwardDataParams.Award.Supplier.Details.LegalForm =
    ValidateAwardDataParams.Award.Supplier.Details.LegalForm(
        id = id,
        scheme = scheme,
        description = description,
        uri = uri
    )

fun ValidateAwardDataRequest.Award.Supplier.Details.Permit.convert(): Result<ValidateAwardDataParams.Award.Supplier.Details.Permit, DataErrors> =
    ValidateAwardDataParams.Award.Supplier.Details.Permit(
        id = id,
        scheme = scheme,
        permitDetails = permitDetails.convert().onFailure { fail -> return fail },
        url = url
    ).asSuccess()

fun ValidateAwardDataRequest.Award.Supplier.Details.Permit.PermitDetails.convert(): Result<ValidateAwardDataParams.Award.Supplier.Details.Permit.PermitDetails, DataErrors> =
    ValidateAwardDataParams.Award.Supplier.Details.Permit.PermitDetails(
        issuedBy = issuedBy.convert(),
        issuedThought = issuedThought.convert(),
        validityPeriod = validityPeriod.convert().onFailure { fail -> return fail }
    ).asSuccess()

fun ValidateAwardDataRequest.Award.Supplier.Details.Permit.PermitDetails.IssuedBy.convert(): ValidateAwardDataParams.Award.Supplier.Details.Permit.PermitDetails.IssuedBy =
    ValidateAwardDataParams.Award.Supplier.Details.Permit.PermitDetails.IssuedBy(id = id, name = name)

fun ValidateAwardDataRequest.Award.Supplier.Details.Permit.PermitDetails.IssuedThought.convert(): ValidateAwardDataParams.Award.Supplier.Details.Permit.PermitDetails.IssuedThought =
    ValidateAwardDataParams.Award.Supplier.Details.Permit.PermitDetails.IssuedThought(id = id, name = name)

fun ValidateAwardDataRequest.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod.convert(): Result<ValidateAwardDataParams.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod, DataErrors> =
    ValidateAwardDataParams.Award.Supplier.Details.Permit.PermitDetails.ValidityPeriod.tryCreate(
        startDate = startDate,
        endDate = endDate
    )