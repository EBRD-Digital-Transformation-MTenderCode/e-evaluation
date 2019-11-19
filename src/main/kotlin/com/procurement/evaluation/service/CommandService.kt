package com.procurement.evaluation.service

import com.procurement.evaluation.application.service.award.AwardCancellationContext
import com.procurement.evaluation.application.service.award.AwardCancellationData
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.application.service.award.CompleteAwardingContext
import com.procurement.evaluation.application.service.award.CompletedAwarding
import com.procurement.evaluation.application.service.award.CreateAwardContext
import com.procurement.evaluation.application.service.award.CreateAwardData
import com.procurement.evaluation.application.service.award.CreateAwardsContext
import com.procurement.evaluation.application.service.award.CreateAwardsData
import com.procurement.evaluation.application.service.award.EvaluateAwardContext
import com.procurement.evaluation.application.service.award.EvaluateAwardData
import com.procurement.evaluation.application.service.award.EvaluatedAward
import com.procurement.evaluation.application.service.award.FinalAwardsStatusByLotsContext
import com.procurement.evaluation.application.service.award.FinalAwardsStatusByLotsData
import com.procurement.evaluation.application.service.award.GetEvaluatedAwardsContext
import com.procurement.evaluation.application.service.award.GetWinningAwardContext
import com.procurement.evaluation.dao.HistoryDao
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.dto.award.EvaluatedAwardsResponse
import com.procurement.evaluation.infrastructure.dto.award.WinningAwardResponse
import com.procurement.evaluation.infrastructure.dto.award.cancel.request.AwardCancellationRequest
import com.procurement.evaluation.infrastructure.dto.award.cancel.response.AwardCancellationResponse
import com.procurement.evaluation.infrastructure.dto.award.create.request.CreateAwardRequest
import com.procurement.evaluation.infrastructure.dto.award.create.response.CreateAwardResponse
import com.procurement.evaluation.infrastructure.dto.award.evaluate.request.EvaluateAwardRequest
import com.procurement.evaluation.infrastructure.dto.award.evaluate.response.EvaluateAwardResponse
import com.procurement.evaluation.infrastructure.dto.award.finalize.request.FinalAwardsStatusByLotsRequest
import com.procurement.evaluation.infrastructure.dto.award.finalize.response.FinalAwardsStatusByLotsResponse
import com.procurement.evaluation.infrastructure.dto.awards.create.request.CreateAwardsRequest
import com.procurement.evaluation.infrastructure.tools.toLocalDateTime
import com.procurement.evaluation.model.dto.bpe.CommandMessage
import com.procurement.evaluation.model.dto.bpe.CommandType
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.model.dto.bpe.cpid
import com.procurement.evaluation.model.dto.bpe.owner
import com.procurement.evaluation.model.dto.bpe.phase
import com.procurement.evaluation.model.dto.bpe.pmd
import com.procurement.evaluation.model.dto.bpe.stage
import com.procurement.evaluation.model.dto.bpe.startDate
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class CommandService(
    private val historyDao: HistoryDao,
    private val createAwardService: CreateAwardService,
    private val updateAwardService: UpdateAwardService,
    private val statusService: StatusService,
    private val awardService: AwardService
) {

    companion object {
        private val log = LoggerFactory.getLogger(CommandService::class.java)
    }

    fun execute(cm: CommandMessage): ResponseDto {
        var historyEntity = historyDao.getHistory(cm.id, cm.command.value())
        if (historyEntity != null) {
            return toObject(ResponseDto::class.java, historyEntity.jsonData)
        }
        val response = when (cm.command) {
            CommandType.CREATE_AWARD -> {
                val context = CreateAwardContext(
                    cpid = getCPID(cm),
                    stage = getStage(cm),
                    owner = getOwner(cm),
                    startDate = getStartDate(cm),
                    lotId = getLotId(cm)
                )

                val request = toObject(CreateAwardRequest::class.java, cm.data)
                val data = CreateAwardData(
                    mdm = request.mdm.let { mdm ->
                        CreateAwardData.Mdm(
                            scales = mdm.scales.toList(),
                            schemes = mdm.schemes.toList()
                        )
                    },
                    award = request.award.let { award ->
                        CreateAwardData.Award(
                            description = award.description,
                            value = award.value.let { value ->
                                CreateAwardData.Award.Value(
                                    amount = value.amount,
                                    currency = value.currency
                                )
                            },
                            suppliers = award.suppliers.map { supplier ->
                                CreateAwardData.Award.Supplier(
                                    name = supplier.name,
                                    identifier = supplier.identifier.let { identifier ->
                                        CreateAwardData.Award.Supplier.Identifier(
                                            scheme = identifier.scheme,
                                            id = identifier.id,
                                            legalName = identifier.legalName,
                                            uri = identifier.uri
                                        )
                                    },
                                    additionalIdentifiers = supplier.additionalIdentifiers?.map { additionalIdentifier ->
                                        CreateAwardData.Award.Supplier.AdditionalIdentifier(
                                            scheme = additionalIdentifier.scheme,
                                            id = additionalIdentifier.id,
                                            legalName = additionalIdentifier.legalName,
                                            uri = additionalIdentifier.uri
                                        )
                                    },
                                    address = supplier.address.let { address ->
                                        CreateAwardData.Award.Supplier.Address(
                                            streetAddress = address.streetAddress,
                                            postalCode = address.postalCode,
                                            addressDetails = address.addressDetails.let { detail ->
                                                CreateAwardData.Award.Supplier.Address.AddressDetails(
                                                    country = detail.country.let { country ->
                                                        CreateAwardData.Award.Supplier.Address.AddressDetails.Country(
                                                            scheme = country.scheme,
                                                            id = country.id,
                                                            description = country.description,
                                                            uri = country.uri
                                                        )
                                                    },
                                                    region = detail.region.let { region ->
                                                        CreateAwardData.Award.Supplier.Address.AddressDetails.Region(
                                                            scheme = region.scheme,
                                                            id = region.id,
                                                            description = region.description,
                                                            uri = region.uri
                                                        )
                                                    },
                                                    locality = detail.locality.let { locality ->
                                                        CreateAwardData.Award.Supplier.Address.AddressDetails.Locality(
                                                            scheme = locality.scheme,
                                                            id = locality.id,
                                                            description = locality.description,
                                                            uri = locality.uri
                                                        )
                                                    }
                                                )
                                            }
                                        )
                                    },
                                    contactPoint = supplier.contactPoint.let { contactPoint ->
                                        CreateAwardData.Award.Supplier.ContactPoint(
                                            name = contactPoint.name,
                                            email = contactPoint.email,
                                            telephone = contactPoint.telephone,
                                            faxNumber = contactPoint.faxNumber,
                                            url = contactPoint.url
                                        )
                                    },
                                    details = supplier.details.let { details ->
                                        CreateAwardData.Award.Supplier.Details(
                                            scale = details.scale
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
                val result = awardService.create(context, data)

                if (log.isDebugEnabled)
                    log.debug("Award was created. Result: ${toJson(result)}")

                val dataResponse = CreateAwardResponse(
                    token = result.token,
                    awardPeriod = result.awardPeriod?.let { awardPeriod ->
                        CreateAwardResponse.AwardPeriod(
                            startDate = awardPeriod.startDate
                        )
                    },
                    lotAwarded = result.lotAwarded,
                    award = result.award.let { award ->
                        CreateAwardResponse.Award(
                            id = award.id,
                            date = award.date,
                            status = award.status,
                            statusDetails = award.statusDetails,
                            relatedLots = award.relatedLots.toList(),
                            description = award.description,
                            value = award.value.let { value ->
                                CreateAwardResponse.Award.Value(
                                    amount = value.amount,
                                    currency = value.currency
                                )
                            },
                            suppliers = award.suppliers.map { supplier ->
                                CreateAwardResponse.Award.Supplier(
                                    id = supplier.id,
                                    name = supplier.name,
                                    identifier = supplier.identifier.let { identifier ->
                                        CreateAwardResponse.Award.Supplier.Identifier(
                                            scheme = identifier.scheme,
                                            id = identifier.id,
                                            legalName = identifier.legalName,
                                            uri = identifier.uri
                                        )
                                    },
                                    additionalIdentifiers = supplier.additionalIdentifiers?.map { additionalIdentifier ->
                                        CreateAwardResponse.Award.Supplier.AdditionalIdentifier(
                                            scheme = additionalIdentifier.scheme,
                                            id = additionalIdentifier.id,
                                            legalName = additionalIdentifier.legalName,
                                            uri = additionalIdentifier.uri
                                        )
                                    },
                                    address = supplier.address.let { address ->
                                        CreateAwardResponse.Award.Supplier.Address(
                                            streetAddress = address.streetAddress,
                                            postalCode = address.postalCode,
                                            addressDetails = address.addressDetails.let { detail ->
                                                CreateAwardResponse.Award.Supplier.Address.AddressDetails(
                                                    country = detail.country.let { country ->
                                                        CreateAwardResponse.Award.Supplier.Address.AddressDetails.Country(
                                                            scheme = country.scheme,
                                                            id = country.id,
                                                            description = country.description,
                                                            uri = country.uri
                                                        )
                                                    },
                                                    region = detail.region.let { region ->
                                                        CreateAwardResponse.Award.Supplier.Address.AddressDetails.Region(
                                                            scheme = region.scheme,
                                                            id = region.id,
                                                            description = region.description,
                                                            uri = region.uri
                                                        )
                                                    },
                                                    locality = detail.locality.let { locality ->
                                                        CreateAwardResponse.Award.Supplier.Address.AddressDetails.Locality(
                                                            scheme = locality.scheme,
                                                            id = locality.id,
                                                            description = locality.description,
                                                            uri = locality.uri
                                                        )
                                                    }
                                                )
                                            }
                                        )
                                    },
                                    contactPoint = supplier.contactPoint.let { contactPoint ->
                                        CreateAwardResponse.Award.Supplier.ContactPoint(
                                            name = contactPoint.name,
                                            email = contactPoint.email,
                                            telephone = contactPoint.telephone,
                                            faxNumber = contactPoint.faxNumber,
                                            url = contactPoint.url
                                        )
                                    },
                                    details = supplier.details.let { details ->
                                        CreateAwardResponse.Award.Supplier.Details(
                                            scale = details.scale
                                        )
                                    }
                                )
                            }
                        )
                    }
                )
                if (log.isDebugEnabled)
                    log.debug("Award was created. Response: ${toJson(dataResponse)}")
                ResponseDto(data = dataResponse)
            }
            CommandType.EVALUATE_AWARD -> {
                val context = EvaluateAwardContext(
                    cpid = getCPID(cm),
                    stage = getStage(cm),
                    token = getToken(cm),
                    owner = getOwner(cm),
                    startDate = getStartDate(cm),
                    awardId = getAwardId(cm)
                )

                val request = toObject(EvaluateAwardRequest::class.java, cm.data)
                val data = EvaluateAwardData(
                    award = request.award.let { award ->
                        EvaluateAwardData.Award(
                            statusDetails = award.statusDetails,
                            description = award.description,
                            documents = award.documents?.map { document ->
                                EvaluateAwardData.Award.Document(
                                    id = document.id,
                                    title = document.title,
                                    description = document.description,
                                    relatedLots = document.relatedLots?.toList(),
                                    documentType = document.documentType
                                )
                            }
                        )
                    }
                )
                val result = awardService.evaluate(context, data)
                if (log.isDebugEnabled)
                    log.debug("Award was evaluate. Result: ${toJson(result)}")

                val dataResponse = EvaluateAwardResponse(
                    award = result.award.let { award ->
                        EvaluateAwardResponse.Award(
                            id = award.id,
                            date = award.date,
                            description = award.description,
                            status = award.status,
                            statusDetails = award.statusDetails,
                            relatedLots = award.relatedLots.toList(),
                            value = award.value.let { value ->
                                EvaluateAwardResponse.Award.Value(
                                    amount = value.amount,
                                    currency = value.currency
                                )
                            },
                            suppliers = award.suppliers.map { supplier ->
                                EvaluateAwardResponse.Award.Supplier(
                                    id = supplier.id,
                                    name = supplier.name
                                )
                            },
                            documents = award.documents?.map { document ->
                                EvaluateAwardResponse.Award.Document(
                                    id = document.id,
                                    title = document.title,
                                    description = document.description,
                                    relatedLots = document.relatedLots?.toList(),
                                    documentType = document.documentType
                                )
                            }
                        )
                    }
                )
                if (log.isDebugEnabled)
                    log.debug("Award was evaluate. Response: ${toJson(dataResponse)}")
                ResponseDto(data = dataResponse)
            }
            CommandType.CREATE_AWARDS -> {
                val context = CreateAwardsContext(
                    cpid = getCPID(cm),
                    stage = getStage(cm),
                    owner = getOwner(cm),
                    startDate = getStartDate(cm),
                    ocid = getOCID(cm)
                )
                val request = toObject(CreateAwardsRequest::class.java, cm.data)
                val data = CreateAwardsData(
                    awardCriteria = request.awardCriteria,
                    awardCriteriaDetails = request.awardCriteriaDetails,
                    bids = request.bids.map { bid ->
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
                            requirementResponses = bid.requirementResponses?.map { requirementResponse ->
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
                            } ?: emptyList(),
                            value = bid.value.let { value ->
                                CreateAwardsData.Bid.Value(
                                    amount = value.amount,
                                    currency = value.currency
                                )
                            },
                            statusDetails = bid.statusDetails,
                            tenderers = bid.tenderers.map { tenderer ->
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
                        )
                    },
                    conversions = request.conversions?.map { conversion ->
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
                    lots = request.lots.map { lot ->
                        CreateAwardsData.Lot(
                            id = lot.id
                        )
                    }
                )
                awardService.create(context = context, data = data)
            }
            CommandType.CREATE_AWARDS_AUCTION -> createAwardService.createAwardsAuction(cm)
            CommandType.CREATE_AWARDS_AUCTION_END -> createAwardService.createAwardsAuctionEnd(cm)
            CommandType.CREATE_AWARDS_BY_LOT_AUCTION -> createAwardService.createAwardsByLotsAuction(cm)
            CommandType.AWARD_BY_BID -> updateAwardService.awardByBid(cm)
            CommandType.SET_FINAL_STATUSES -> statusService.setFinalStatuses(cm)
            CommandType.PREPARE_CANCELLATION -> statusService.prepareCancellation(cm)
            CommandType.AWARDS_CANCELLATION -> {
                val context = AwardCancellationContext(
                    cpid = cm.cpid,
                    owner = cm.owner,
                    stage = cm.stage,
                    phase = cm.phase,
                    startDate = cm.startDate
                )
                val request = toObject(AwardCancellationRequest::class.java, cm.data)
                val data = AwardCancellationData(
                    lots = request.lots.map { lot ->
                        AwardCancellationData.Lot(id = lot.id)
                    }
                )
                val result = statusService.awardsCancellation(context = context, data = data)
                if (log.isDebugEnabled)
                    log.debug("Award was cancelled. Result: ${toJson(result)}")
                val dataResponse = AwardCancellationResponse(
                    awards = result.awards.map { award ->
                        AwardCancellationResponse.Award(
                            id = award.id,
                            title = award.title,
                            description = award.description,
                            date = award.date,
                            status = award.status,
                            statusDetails = award.statusDetails,
                            relatedLots = award.relatedLots?.toList()
                        )
                    }
                )
                if (log.isDebugEnabled)
                    log.debug("Award was cancelled. Response: ${toJson(dataResponse)}")
                ResponseDto(data = dataResponse)
            }
            CommandType.CHECK_AWARD_VALUE -> statusService.checkAwardValue(cm)
            CommandType.END_AWARD_PERIOD -> statusService.endAwardPeriod(cm)
            CommandType.SET_INITIAL_AWARDS_STATUS -> updateAwardService.setInitialAwardsStatuses(cm)
            CommandType.GET_WINNING_AWARD -> {
                val context = GetWinningAwardContext(
                    cpid = getCPID(cm),
                    stage = getStage(cm),
                    lotId = getLotId(cm)
                )
                val award = awardService.getWinning(context = context)
                if (log.isDebugEnabled) {
                    if (award == null)
                        log.debug("Winning award was not found.")
                    else
                        log.debug("Winning award was found with the id: '${award.id}'.")
                }
                ResponseDto(
                    data = WinningAwardResponse(
                        award = award?.let {
                            WinningAwardResponse.Award(id = it.id)
                        }
                    )
                )
            }
            CommandType.GET_EVALUATED_AWARDS -> {
                val context = GetEvaluatedAwardsContext(
                    cpid = getCPID(cm),
                    stage = getStage(cm),
                    lotId = getLotId(cm)
                )
                val evaluatedAwards: List<EvaluatedAward> = awardService.getEvaluated(context = context)
                if (log.isDebugEnabled) {
                    if (evaluatedAwards.isEmpty())
                        log.debug("Evaluated award was not found.")
                    else
                        log.debug("Evaluated awards was found.")
                }
                ResponseDto(
                    data = EvaluatedAwardsResponse(
                        awards = evaluatedAwards.map { evaluatedAward ->
                            EvaluatedAwardsResponse.Award(
                                statusDetails = evaluatedAward.statusDetails,
                                relatedBid = evaluatedAward.relatedBid
                            )
                        }
                    )
                )
            }
            CommandType.GET_AWARDS_FOR_AC -> statusService.getAwardsForAc(cm)
            CommandType.GET_LOT_FOR_CHECK -> statusService.getLotForCheck(cm)
            CommandType.GET_AWARD_ID_FOR_CHECK -> statusService.getAwardIdForCheck(cm)
            CommandType.FINAL_AWARDS_STATUS_BY_LOTS -> {
                val context = FinalAwardsStatusByLotsContext(
                    cpid = cm.cpid,
                    pmd = cm.pmd
                )
                val request = toObject(FinalAwardsStatusByLotsRequest::class.java, cm.data)
                val data = FinalAwardsStatusByLotsData(
                    lots = request.lots.map { lot ->
                        FinalAwardsStatusByLotsData.Lot(
                            id = lot.id
                        )
                    }
                )

                val result = awardService.finalAwardsStatusByLots(context, data)
                if (log.isDebugEnabled)
                    log.debug("Awards were finalized. Result: ${toJson(result)}")

                val dataResponse = FinalAwardsStatusByLotsResponse(
                    awards = result.awards.map { award ->
                        FinalAwardsStatusByLotsResponse.Award(
                            id = award.id,
                            status = award.status,
                            statusDetails = award.statusDetails
                        )
                    }
                )
                if (log.isDebugEnabled)
                    log.debug("Awards were finalized. Response: ${toJson(dataResponse)}")
                ResponseDto(data = dataResponse)
            }
            CommandType.COMPLETE_AWARDING -> {
                val context = CompleteAwardingContext(
                    cpid = cm.cpid,
                    startDate = cm.startDate
                )

                val result = awardService.completeAwarding(context)
                if (log.isDebugEnabled)
                    log.debug("Award was completed. Result: ${toJson(result)}")

                val dataResponse = CompletedAwarding(
                    awardPeriod = CompletedAwarding.AwardPeriod(
                        endDate = result.awardPeriod.endDate
                    )
                )
                if (log.isDebugEnabled)
                    log.debug("Award was completed. Response: ${toJson(dataResponse)}")
                ResponseDto(data = dataResponse)
            }
        }
        historyEntity = historyDao.saveHistory(cm.id, cm.command.value(), response)
        return toObject(ResponseDto::class.java, historyEntity.jsonData)
    }

    private fun getCPID(cm: CommandMessage): String = cm.context.cpid
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'cpid' attribute in context.")

    private fun getOCID(cm: CommandMessage): String = cm.context.ocid
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'ocid' attribute in context.")

    private fun getStage(cm: CommandMessage): String = cm.context.stage
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'stage' attribute in context.")

    private fun getStartDate(cm: CommandMessage): LocalDateTime = cm.context.startDate?.toLocalDateTime()
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'startDate' attribute in context.")

    private fun getToken(cm: CommandMessage): UUID = cm.context.token
        ?.let { id ->
            try {
                UUID.fromString(id)
            } catch (exception: Exception) {
                throw ErrorException(error = ErrorType.INVALID_FORMAT_TOKEN)
            }
        } ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'token' attribute in context.")

    private fun getOwner(cm: CommandMessage): String = cm.context.owner
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'owner' attribute in context.")

    private fun getLotId(cm: CommandMessage): UUID = cm.context.id
        ?.let { id ->
            try {
                UUID.fromString(id)
            } catch (exception: Exception) {
                throw ErrorException(error = ErrorType.INVALID_FORMAT_LOT_ID)
            }
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'token' attribute in context.")

    private fun getAwardId(cm: CommandMessage): UUID = cm.context.id
        ?.let { id ->
            try {
                UUID.fromString(id)
            } catch (exception: Exception) {
                throw ErrorException(error = ErrorType.INVALID_FORMAT_AWARD_ID)
            }
        }
        ?: throw ErrorException(error = ErrorType.CONTEXT, message = "Missing the 'id' attribute in context.")
}