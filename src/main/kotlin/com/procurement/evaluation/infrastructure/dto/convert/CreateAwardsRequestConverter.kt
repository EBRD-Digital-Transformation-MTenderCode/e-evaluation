package com.procurement.evaluation.infrastructure.dto.convert

import com.procurement.evaluation.application.service.award.CreateAwardsData
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.dto.awards.create.request.CreateAwardsRequest
import com.procurement.evaluation.lib.errorIfEmpty
import com.procurement.evaluation.lib.mapIfNotEmpty
import com.procurement.evaluation.lib.orThrow

fun CreateAwardsRequest.convert(): CreateAwardsData = CreateAwardsData(
    awardCriteria = this.awardCriteria,
    awardCriteriaDetails = this.awardCriteriaDetails,
    bids = this.bids.map { bid ->
        CreateAwardsData.Bid(
            id = bid.id,
            status = bid.status,
            date = bid.date,
            documents = bid.documents?.map { document ->
                CreateAwardsData.Bid.Document(
                    id = document.id,
                    description = document.description,
                    documentType = document.documentType,
                    relatedLots = document.relatedLots ?: emptyList(),
                    title = document.title
                )
            } ?: emptyList(),
            relatedLots = bid.relatedLots,
            requirementResponses = bid.requirementResponses
                .errorIfEmpty {
                    ErrorException(
                        error = ErrorType.IS_EMPTY,
                        message = "The bid '${bid.id}' contain empty list of the requirement responses."
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
                        additionalIdentifiers = tenderer.additionalIdentifiers?.map { additionalIdentifier ->
                            CreateAwardsData.Bid.Tenderer.AdditionalIdentifier(
                                id = additionalIdentifier.id,
                                legalName = additionalIdentifier.legalName,
                                scheme = additionalIdentifier.scheme,
                                uri = additionalIdentifier.uri
                            )
                        } ?: emptyList(),
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
                                bankAccounts = details.bankAccounts?.map { bankAccount ->
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
                                        additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers?.map { additionalAccountIdentifier ->
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
                                mainEconomicActivities = details.mainEconomicActivities,
                                permits = details.permits?.map { permit ->
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
                        persones = tenderer.persones?.map { person ->
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
                                businessFunctions = person.businessFunctions.map { businessFunction ->
                                    CreateAwardsData.Bid.Tenderer.Person.BusinessFunction(
                                        id = businessFunction.id,
                                        period = businessFunction.period.let { period ->
                                            CreateAwardsData.Bid.Tenderer.Person.BusinessFunction.Period(
                                                startDate = period.startDate
                                            )
                                        },
                                        documents = businessFunction.documents?.map { document ->
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
                            )
                        } ?: emptyList()
                    )
                }
                .orThrow {
                    ErrorException(
                        error = ErrorType.IS_EMPTY,
                        message = "The electronic auction with id: '${detail.id}' contain empty list of the electronic auction modalities."
                    )
                }
        )
    },
    conversions = this.conversions?.map { conversion ->
        CreateAwardsData.Conversion(
            id = conversion.id,
            description = conversion.description,
            coefficients = conversion.coefficients.map { coefficient ->
                CreateAwardsData.Conversion.Coefficient(
                    id = coefficient.id,
                    value = coefficient.value,
                    coefficient = coefficient.coefficient
                )
            },
            rationale = conversion.rationale,
            relatedItem = conversion.relatedItem,
            relatesTo = conversion.relatesTo
        )
    } ?: emptyList(),
    lots = this.lots.map { lot ->
        CreateAwardsData.Lot(
            id = lot.id
        )
    }
)
