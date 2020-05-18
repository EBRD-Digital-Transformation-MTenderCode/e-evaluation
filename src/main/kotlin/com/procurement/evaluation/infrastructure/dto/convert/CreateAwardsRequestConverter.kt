package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.CreateAwardsData
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.dto.award.create.request.CreateAwardsRequest
import com.procurement.evaluation.lib.errorIfEmpty
import com.procurement.evaluation.lib.mapIfNotEmpty
import com.procurement.evaluation.lib.orThrow

fun CreateAwardsRequest.convert(): CreateAwardsData = CreateAwardsData(
    awardCriteria = this.awardCriteria,
    awardCriteriaDetails = this.awardCriteriaDetails,
    bids = this.bids
        .mapIfNotEmpty { bid ->
            CreateAwardsData.Bid(
                id = bid.id,
                status = bid.status,
                date = bid.date,
                documents = bid.documents
                    .errorIfEmpty {
                        ErrorException(
                            error = ErrorType.IS_EMPTY,
                            message = "The bid '${bid.id}' contains empty list of documents ."
                        )
                    }
                    ?.map { document ->
                        CreateAwardsData.Bid.Document(
                            id = document.id,
                            description = document.description,
                            documentType = document.documentType,
                            relatedLots = document.relatedLots ?: emptyList(),
                            title = document.title
                        )
                    }.orEmpty(),
                relatedLots = bid.relatedLots,
                requirementResponses = bid.requirementResponses
                    .errorIfEmpty {
                        ErrorException(
                            error = ErrorType.IS_EMPTY,
                            message = "The bid '${bid.id}' contains empty list of the requirement responses."
                        )
                    }
                    ?.map { requirementResponse ->
                        CreateAwardsData.Bid.RequirementResponse(
                            id = requirementResponse.id,
                            title = requirementResponse.title,
                            description = requirementResponse.description,
                            period = requirementResponse.period?.let { period ->
                                CreateAwardsData.Bid.RequirementResponse.Period(
                                    startDate = period.startDate,
                                    endDate = period.endDate
                                )
                            },
                            requirement = requirementResponse.requirement.let { requirement ->
                                CreateAwardsData.Bid.RequirementResponse.Requirement(
                                    id = requirement.id
                                )
                            },
                            value = requirementResponse.value
                        )
                    }
                    .orEmpty(),
                value = bid.value,
                statusDetails = bid.statusDetails,
                tenderers = bid.tenderers
                    .mapIfNotEmpty { tenderer ->
                        CreateAwardsData.Bid.Tenderer(
                            id = tenderer.id,
                            additionalIdentifiers = tenderer.additionalIdentifiers
                                .errorIfEmpty {
                                    ErrorException(
                                        error = ErrorType.IS_EMPTY,
                                        message = "The tenderer '${tenderer.id}' of bid '${bid.id}' contains empty list of additionalIdentifiers."
                                    )
                                }
                                ?.map { additionalIdentifier ->
                                    CreateAwardsData.Bid.Tenderer.AdditionalIdentifier(
                                        id = additionalIdentifier.id,
                                        legalName = additionalIdentifier.legalName,
                                        scheme = additionalIdentifier.scheme,
                                        uri = additionalIdentifier.uri
                                    )
                                }.orEmpty(),
                            address = tenderer.address.let { address ->
                                CreateAwardsData.Bid.Tenderer.Address(
                                    streetAddress = address.streetAddress,
                                    postalCode = address.postalCode,
                                    addressDetails = address.addressDetails.let { addressDetails ->
                                        CreateAwardsData.Bid.Tenderer.Address.AddressDetails(
                                            country = addressDetails.country.let { country ->
                                                CreateAwardsData.Bid.Tenderer.Address.AddressDetails.Country(
                                                    id = country.id,
                                                    uri = country.uri,
                                                    scheme = country.scheme,
                                                    description = country.description
                                                )
                                            },
                                            locality = addressDetails.locality.let { locality ->
                                                CreateAwardsData.Bid.Tenderer.Address.AddressDetails.Locality(
                                                    id = locality.id,
                                                    description = locality.description,
                                                    scheme = locality.scheme,
                                                    uri = locality.uri
                                                )
                                            },
                                            region = addressDetails.region.let { region ->
                                                CreateAwardsData.Bid.Tenderer.Address.AddressDetails.Region(
                                                    id = region.id,
                                                    uri = region.uri,
                                                    scheme = region.scheme,
                                                    description = region.description
                                                )
                                            }
                                        )
                                    }
                                )
                            },
                            contactPoint = tenderer.contactPoint.let { contactPoint ->
                                CreateAwardsData.Bid.Tenderer.ContactPoint(
                                    name = contactPoint.name,
                                    email = contactPoint.email,
                                    faxNumber = contactPoint.faxNumber,
                                    telephone = contactPoint.telephone,
                                    url = contactPoint.url
                                )
                            },
                            name = tenderer.name,
                            details = tenderer.details.let { details ->
                                CreateAwardsData.Bid.Tenderer.Details(
                                    typeOfSupplier = details.typeOfSupplier,
                                    bankAccounts = details.bankAccounts
                                        .errorIfEmpty {
                                            ErrorException(
                                                error = ErrorType.IS_EMPTY,
                                                message = "The tenderer '${tenderer.id}' of bid '${bid.id}' contains empty list of bankAccounts in details object."
                                            )
                                        }
                                        ?.map { bankAccount ->
                                            CreateAwardsData.Bid.Tenderer.Details.BankAccount(
                                                description = bankAccount.description,
                                                identifier = bankAccount.identifier.let { identifier ->
                                                    CreateAwardsData.Bid.Tenderer.Details.BankAccount.Identifier(
                                                        id = identifier.id,
                                                        scheme = identifier.scheme
                                                    )
                                                },
                                                address = bankAccount.address.let { address ->
                                                    CreateAwardsData.Bid.Tenderer.Details.BankAccount.Address(
                                                        streetAddress = address.streetAddress,
                                                        postalCode = address.postalCode,
                                                        addressDetails = address.addressDetails.let { addressDetails ->
                                                            CreateAwardsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails(
                                                                country = addressDetails.country.let { country ->
                                                                    CreateAwardsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Country(
                                                                        id = country.id,
                                                                        scheme = country.scheme,
                                                                        description = country.description,
                                                                        uri = country.uri
                                                                    )
                                                                },
                                                                region = addressDetails.region.let { region ->
                                                                    CreateAwardsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Region(
                                                                        id = region.id,
                                                                        uri = region.uri,
                                                                        description = region.description,
                                                                        scheme = region.scheme
                                                                    )
                                                                },
                                                                locality = addressDetails.locality.let { locality ->
                                                                    CreateAwardsData.Bid.Tenderer.Details.BankAccount.Address.AddressDetails.Locality(
                                                                        id = locality.id,
                                                                        scheme = locality.scheme,
                                                                        description = locality.description,
                                                                        uri = locality.uri
                                                                    )
                                                                }
                                                            )
                                                        }
                                                    )
                                                },
                                                accountIdentification = bankAccount.accountIdentification.let { accountIdentification ->
                                                    CreateAwardsData.Bid.Tenderer.Details.BankAccount.AccountIdentification(
                                                        id = accountIdentification.id,
                                                        scheme = accountIdentification.scheme
                                                    )
                                                },
                                                additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                    .errorIfEmpty {
                                                        ErrorException(
                                                            error = ErrorType.IS_EMPTY,
                                                            message = "The tenderer '${tenderer.id}' of bid '${bid.id}' contains empty list of " +
                                                                "additionalAccountIdentifiers in bankAccount '${bankAccount.identifier.id}'."
                                                        )
                                                    }
                                                    ?.map { additionalAccountIdentifier ->
                                                        CreateAwardsData.Bid.Tenderer.Details.BankAccount.AdditionalAccountIdentifier(
                                                            id = additionalAccountIdentifier.id,
                                                            scheme = additionalAccountIdentifier.scheme
                                                        )
                                                    } ?: emptyList(),
                                                bankName = bankAccount.bankName
                                            )
                                        } ?: emptyList(),
                                    legalForm = details.legalForm?.let { legalForm ->
                                        CreateAwardsData.Bid.Tenderer.Details.LegalForm(
                                            id = legalForm.id,
                                            scheme = legalForm.scheme,
                                            uri = legalForm.uri,
                                            description = legalForm.description
                                        )
                                    },
                                    mainEconomicActivities = details.mainEconomicActivities
                                        .errorIfEmpty {
                                            ErrorException(
                                                error = ErrorType.IS_EMPTY,
                                                message = "The bid '${bid.id}' contains empty list of the main economic activities."
                                            )
                                        }
                                        ?.map { mainEconomicActivity ->
                                            CreateAwardsData.Bid.Tenderer.Details.MainEconomicActivity(
                                                id = mainEconomicActivity.id,
                                                description = mainEconomicActivity.description,
                                                uri = mainEconomicActivity.uri,
                                                scheme = mainEconomicActivity.scheme
                                            )
                                        }.orEmpty(),
                                    permits = details.permits
                                        .errorIfEmpty {
                                            ErrorException(
                                                error = ErrorType.IS_EMPTY,
                                                message = "The tenderer '${tenderer.id}' of bid '${bid.id}' contains empty list of " +
                                                    "permits in details object."
                                            )
                                        }
                                        ?.map { permit ->
                                            CreateAwardsData.Bid.Tenderer.Details.Permit(
                                                id = permit.id,
                                                scheme = permit.scheme,
                                                url = permit.url,
                                                permitDetails = permit.permitDetails.let { permitDetail ->
                                                    CreateAwardsData.Bid.Tenderer.Details.Permit.PermitDetails(
                                                        issuedBy = permitDetail.issuedBy.let { issuedBy ->
                                                            CreateAwardsData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedBy(
                                                                id = issuedBy.id,
                                                                name = issuedBy.name
                                                            )
                                                        },
                                                        issuedThought = permitDetail.issuedThought.let { issuedThought ->
                                                            CreateAwardsData.Bid.Tenderer.Details.Permit.PermitDetails.IssuedThought(
                                                                id = issuedThought.id,
                                                                name = issuedThought.name
                                                            )
                                                        },
                                                        validityPeriod = permitDetail.validityPeriod.let { validityPeriod ->
                                                            CreateAwardsData.Bid.Tenderer.Details.Permit.PermitDetails.ValidityPeriod(
                                                                startDate = validityPeriod.startDate,
                                                                endDate = validityPeriod.endDate
                                                            )
                                                        }
                                                    )
                                                }
                                            )
                                        } ?: emptyList(),
                                    scale = details.scale
                                )
                            },
                            identifier = tenderer.identifier.let { identifier ->
                                CreateAwardsData.Bid.Tenderer.Identifier(
                                    id = identifier.id,
                                    scheme = identifier.scheme,
                                    uri = identifier.uri,
                                    legalName = identifier.legalName
                                )
                            },
                            persones = tenderer.persones
                                .errorIfEmpty {
                                    ErrorException(
                                        error = ErrorType.IS_EMPTY,
                                        message = "The tenderer '${tenderer.id}' of bid '${bid.id}' contains empty list of persones."
                                    )
                                }
                                ?.map { person ->
                                    CreateAwardsData.Bid.Tenderer.Person(
                                        identifier = person.identifier.let { identifier ->
                                            CreateAwardsData.Bid.Tenderer.Person.Identifier(
                                                id = identifier.id,
                                                scheme = identifier.scheme,
                                                uri = identifier.uri
                                            )
                                        },
                                        name = person.name,
                                        title = person.title,
                                        businessFunctions = person.businessFunctions
                                            .mapIfNotEmpty { businessFunction ->
                                                CreateAwardsData.Bid.Tenderer.Person.BusinessFunction(
                                                    id = businessFunction.id,
                                                    period = businessFunction.period.let { period ->
                                                        CreateAwardsData.Bid.Tenderer.Person.BusinessFunction.Period(
                                                            startDate = period.startDate
                                                        )
                                                    },
                                                    documents = businessFunction.documents
                                                        .errorIfEmpty {
                                                            ErrorException(
                                                                error = ErrorType.IS_EMPTY,
                                                                message = "The tenderer '${tenderer.id}' of bid '${bid.id}' contains empty list " +
                                                                    "of documents in businessFunction '${businessFunction.id}'."
                                                            )
                                                        }
                                                        ?.map { document ->
                                                            CreateAwardsData.Bid.Tenderer.Person.BusinessFunction.Document(
                                                                id = document.id,
                                                                title = document.title,
                                                                description = document.description,
                                                                documentType = document.documentType
                                                            )
                                                        } ?: emptyList(),
                                                    jobTitle = businessFunction.jobTitle,
                                                    type = businessFunction.type
                                                )
                                            }
                                            .orThrow {
                                                ErrorException(
                                                    error = ErrorType.IS_EMPTY,
                                                    message = "The tenderer '${tenderer.id}' of bid '${bid.id}' contains empty list of businessFunctions in person '${person.identifier.id}'."
                                                )
                                            }
                                    )
                                }.orEmpty()
                        )
                    }
                    .orThrow {
                        ErrorException(
                            error = ErrorType.IS_EMPTY,
                            message = "The bid '${bid.id}' contains empty list of tenderers."
                        )
                    }
            )
        }
        .orThrow {
            ErrorException(
                error = ErrorType.IS_EMPTY,
                message = "The bids list is empty."
            )
        },
    conversions = this.conversions
        .errorIfEmpty {
            ErrorException(
                error = ErrorType.IS_EMPTY,
                message = "Request contains empty list of conversions."
            )
        }
        ?.map { conversion ->
            CreateAwardsData.Conversion(
                id = conversion.id,
                description = conversion.description,
                coefficients = conversion.coefficients
                    .mapIfNotEmpty { coefficient ->
                        CreateAwardsData.Conversion.Coefficient(
                            id = coefficient.id,
                            value = coefficient.value,
                            coefficient = coefficient.coefficient
                        )
                    }.orThrow {
                        ErrorException(
                            error = ErrorType.IS_EMPTY,
                            message = "Conversion '${conversion.id}' contains empty list of coefficients."
                        )
                    },
                rationale = conversion.rationale,
                relatedItem = conversion.relatedItem,
                relatesTo = conversion.relatesTo
            )
        }.orEmpty(),
    lots = this.lots
        .mapIfNotEmpty { lot ->
            CreateAwardsData.Lot(
                id = lot.id
            )
        }.orThrow {
            ErrorException(
                error = ErrorType.IS_EMPTY,
                message = "Request contains empty list of lots."
            )
        }
)