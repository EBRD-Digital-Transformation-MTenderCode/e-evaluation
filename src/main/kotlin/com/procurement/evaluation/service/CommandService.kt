package com.procurement.evaluation.service

import com.procurement.evaluation.application.service.award.AwardCancellationContext
import com.procurement.evaluation.application.service.award.AwardCancellationData
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.application.service.award.CompleteAwardingContext
import com.procurement.evaluation.application.service.award.CompletedAwarding
import com.procurement.evaluation.application.service.award.CreateAwardContext
import com.procurement.evaluation.application.service.award.CreateAwardData
import com.procurement.evaluation.application.service.award.CreateAwardsContext
import com.procurement.evaluation.application.service.award.EvaluateAwardContext
import com.procurement.evaluation.application.service.award.EvaluateAwardData
import com.procurement.evaluation.application.service.award.EvaluatedAward
import com.procurement.evaluation.application.service.award.FinalAwardsStatusByLotsContext
import com.procurement.evaluation.application.service.award.FinalAwardsStatusByLotsData
import com.procurement.evaluation.application.service.award.GetEvaluatedAwardsContext
import com.procurement.evaluation.application.service.award.GetWinningAwardContext
import com.procurement.evaluation.dao.HistoryDao
import com.procurement.evaluation.infrastructure.dto.award.EvaluatedAwardsResponse
import com.procurement.evaluation.infrastructure.dto.award.WinningAwardResponse
import com.procurement.evaluation.infrastructure.dto.award.cancel.request.AwardCancellationRequest
import com.procurement.evaluation.infrastructure.dto.award.cancel.response.AwardCancellationResponse
import com.procurement.evaluation.infrastructure.dto.award.create.request.CreateAwardRequest
import com.procurement.evaluation.infrastructure.dto.award.create.request.CreateAwardsRequest
import com.procurement.evaluation.infrastructure.dto.award.create.response.CreateAwardResponse
import com.procurement.evaluation.infrastructure.dto.award.create.response.CreateAwardsResponse
import com.procurement.evaluation.infrastructure.dto.award.evaluate.request.EvaluateAwardRequest
import com.procurement.evaluation.infrastructure.dto.award.evaluate.response.EvaluateAwardResponse
import com.procurement.evaluation.infrastructure.dto.award.finalize.request.FinalAwardsStatusByLotsRequest
import com.procurement.evaluation.infrastructure.dto.award.finalize.response.FinalAwardsStatusByLotsResponse
import com.procurement.evaluation.infrastructure.dto.convert.convert
import com.procurement.evaluation.model.dto.bpe.CommandMessage
import com.procurement.evaluation.model.dto.bpe.CommandType
import com.procurement.evaluation.model.dto.bpe.ResponseDto
import com.procurement.evaluation.model.dto.bpe.awardId
import com.procurement.evaluation.model.dto.bpe.cpid
import com.procurement.evaluation.model.dto.bpe.lotId
import com.procurement.evaluation.model.dto.bpe.ocid
import com.procurement.evaluation.model.dto.bpe.owner
import com.procurement.evaluation.model.dto.bpe.phase
import com.procurement.evaluation.model.dto.bpe.pmd
import com.procurement.evaluation.model.dto.bpe.stage
import com.procurement.evaluation.model.dto.bpe.startDate
import com.procurement.evaluation.model.dto.bpe.token
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

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
                    cpid = cm.cpid,
                    stage = cm.stage,
                    owner = cm.owner,
                    startDate = cm.startDate,
                    lotId = cm.lotId
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
                    cpid = cm.cpid,
                    stage = cm.stage,
                    token = cm.token,
                    owner = cm.owner,
                    startDate = cm.startDate,
                    awardId = cm.awardId
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
                    cpid = cm.cpid,
                    stage = cm.stage,
                    owner = cm.owner,
                    startDate = cm.startDate,
                    ocid = cm.ocid
                )
                val request = toObject(CreateAwardsRequest::class.java, cm.data)
                val result = awardService.create(context = context, data = request.convert())
                if (log.isDebugEnabled)
                    log.debug("Awards were created. Result: ${toJson(result)}")

                val dataResponse = CreateAwardsResponse()
                if (log.isDebugEnabled)
                    log.debug("Awards were created. Response: ${toJson(dataResponse)}")
                ResponseDto(data = dataResponse)
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
                    cpid = cm.cpid,
                    stage = cm.stage,
                    lotId = cm.lotId
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
                    cpid = cm.cpid,
                    stage = cm.stage,
                    lotId = cm.lotId
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
}
