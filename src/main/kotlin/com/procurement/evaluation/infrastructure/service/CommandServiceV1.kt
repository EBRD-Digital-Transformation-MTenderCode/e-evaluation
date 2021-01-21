package com.procurement.evaluation.infrastructure.service

import com.procurement.evaluation.application.service.StatusService
import com.procurement.evaluation.application.service.award.AwardCancellationContext
import com.procurement.evaluation.application.service.award.AwardCancellationResult
import com.procurement.evaluation.application.service.award.AwardService
import com.procurement.evaluation.application.service.award.CheckAwardStatusContext
import com.procurement.evaluation.application.service.award.CheckAwardStatusResult
import com.procurement.evaluation.application.service.award.CreateAwardContext
import com.procurement.evaluation.application.service.award.CreateAwardData
import com.procurement.evaluation.application.service.award.CreateAwardsAuctionEndContext
import com.procurement.evaluation.application.service.award.CreateAwardsContext
import com.procurement.evaluation.application.service.award.CreateUnsuccessfulAwardsContext
import com.procurement.evaluation.application.service.award.CreateUnsuccessfulAwardsResult
import com.procurement.evaluation.application.service.award.CreatedAwardData
import com.procurement.evaluation.application.service.award.CreatedAwardsAuctionEndResult
import com.procurement.evaluation.application.service.award.CreatedAwardsResult
import com.procurement.evaluation.application.service.award.EvaluateAwardContext
import com.procurement.evaluation.application.service.award.EvaluatedAward
import com.procurement.evaluation.application.service.award.FinalAwardsStatusByLotsContext
import com.procurement.evaluation.application.service.award.FinalAwardsStatusByLotsData
import com.procurement.evaluation.application.service.award.FinalizedAwardsStatusByLots
import com.procurement.evaluation.application.service.award.GetEvaluatedAwardsContext
import com.procurement.evaluation.application.service.award.GetNextAwardContext
import com.procurement.evaluation.application.service.award.GetNextAwardResult
import com.procurement.evaluation.application.service.award.GetWinningAwardContext
import com.procurement.evaluation.application.service.award.SetAwardForEvaluationContext
import com.procurement.evaluation.application.service.award.SetAwardForEvaluationResult
import com.procurement.evaluation.application.service.award.StartAwardPeriodContext
import com.procurement.evaluation.application.service.award.StartAwardPeriodResult
import com.procurement.evaluation.application.service.award.StartConsiderationContext
import com.procurement.evaluation.application.service.award.StartConsiderationResult
import com.procurement.evaluation.application.service.award.WinningAward
import com.procurement.evaluation.application.service.lot.GetUnsuccessfulLotsContext
import com.procurement.evaluation.application.service.lot.GetUnsuccessfulLotsResult
import com.procurement.evaluation.application.service.lot.LotService
import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.api.v1.ApiResponseV1
import com.procurement.evaluation.infrastructure.api.v1.CommandMessage
import com.procurement.evaluation.infrastructure.api.v1.CommandTypeV1
import com.procurement.evaluation.infrastructure.api.v1.awardId
import com.procurement.evaluation.infrastructure.api.v1.commandId
import com.procurement.evaluation.infrastructure.api.v1.country
import com.procurement.evaluation.infrastructure.api.v1.cpid
import com.procurement.evaluation.infrastructure.api.v1.lotId
import com.procurement.evaluation.infrastructure.api.v1.ocid
import com.procurement.evaluation.infrastructure.api.v1.operationType
import com.procurement.evaluation.infrastructure.api.v1.owner
import com.procurement.evaluation.infrastructure.api.v1.phase
import com.procurement.evaluation.infrastructure.api.v1.pmd
import com.procurement.evaluation.infrastructure.api.v1.startDate
import com.procurement.evaluation.infrastructure.api.v1.token
import com.procurement.evaluation.infrastructure.handler.HistoryRepository
import com.procurement.evaluation.infrastructure.handler.v1.converter.convert
import com.procurement.evaluation.infrastructure.handler.v1.converter.converter
import com.procurement.evaluation.infrastructure.handler.v1.model.request.AwardCancellationRequest
import com.procurement.evaluation.infrastructure.handler.v1.model.request.CreateAwardRequest
import com.procurement.evaluation.infrastructure.handler.v1.model.request.CreateAwardsAuctionEndRequest
import com.procurement.evaluation.infrastructure.handler.v1.model.request.CreateAwardsRequest
import com.procurement.evaluation.infrastructure.handler.v1.model.request.CreateUnsuccessfulAwardsRequest
import com.procurement.evaluation.infrastructure.handler.v1.model.request.EvaluateAwardRequest
import com.procurement.evaluation.infrastructure.handler.v1.model.request.FinalAwardsStatusByLotsRequest
import com.procurement.evaluation.infrastructure.handler.v1.model.request.GetUnsuccessfulLotsRequest
import com.procurement.evaluation.infrastructure.handler.v1.model.request.SetAwardForEvaluationRequest
import com.procurement.evaluation.infrastructure.handler.v1.model.response.CheckAwardStatusResponse
import com.procurement.evaluation.infrastructure.handler.v1.model.response.CreateAwardResponse
import com.procurement.evaluation.infrastructure.handler.v1.model.response.EvaluatedAwardsResponse
import com.procurement.evaluation.infrastructure.handler.v1.model.response.FinalAwardsStatusByLotsResponse
import com.procurement.evaluation.infrastructure.handler.v1.model.response.WinningAwardResponse
import com.procurement.evaluation.lib.mapIfNotEmpty
import com.procurement.evaluation.lib.orThrow
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toObject
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CommandServiceV1(
    private val historyRepository: HistoryRepository,
    private val statusService: StatusService,
    private val awardService: AwardService,
    private val lotService: LotService
) {

    companion object {
        private val log = LoggerFactory.getLogger(CommandServiceV1::class.java)
    }

    fun execute(cm: CommandMessage): ApiResponseV1 {
        val history = historyRepository.getHistory(cm.commandId, cm.command)
            .orThrow {
                throw RuntimeException("Error of loading history. ${it.description}", it.exception)
            }
        if (history != null) {
            return toObject(ApiResponseV1.Success::class.java, history)
        }
        val dataOfResponse: Any = when (cm.command) {
            CommandTypeV1.CREATE_AWARD -> {
                val context = CreateAwardContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid,
                    owner = cm.owner,
                    startDate = cm.startDate,
                    lotId = cm.lotId
                )

                val request = toObject(CreateAwardRequest::class.java, cm.data)
                val data = CreateAwardData(
                    mdm = request.mdm.let { mdm ->
                        CreateAwardData.Mdm(
                            scales = mdm.scales.toList(),
                            organizationSchemesByCountries = mdm.organizationSchemesByCountries
                                .map { organizationSchemesByCountries ->
                                    CreateAwardData.Mdm.OrganizationSchemesByCountries(
                                        country = organizationSchemesByCountries.country,
                                        schemes = organizationSchemesByCountries.schemes.mapIfNotEmpty { it }
                                            .orThrow {
                                                ErrorException(
                                                    error = ErrorType.IS_EMPTY,
                                                    message = "Mdm contains empty list of 'schemes'."
                                                )
                                            }

                                    )
                                }
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
                awardService.create(context, data)
                    .also { result: CreatedAwardData ->
                        if (log.isDebugEnabled)
                            log.debug("Award was created. Result: ${toJson(result)}")
                    }
                    .let { result: CreatedAwardData ->
                        CreateAwardResponse(
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
                    }
            }
            CommandTypeV1.EVALUATE_AWARD -> {
                val context = EvaluateAwardContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid,
                    token = cm.token,
                    owner = cm.owner,
                    startDate = cm.startDate,
                    awardId = cm.awardId
                )

                val request = toObject(EvaluateAwardRequest::class.java, cm.data)
                awardService.evaluate(context, request.convert())
                    .also {
                        if (log.isDebugEnabled)
                            log.debug("Award was evaluate. Result: ${toJson(it)}")
                    }
                    .convert()
            }
            CommandTypeV1.CREATE_AWARDS -> {
                val context = CreateAwardsContext(
                    cpid = cm.cpid,
                    owner = cm.owner,
                    startDate = cm.startDate,
                    ocid = cm.ocid,
                    pmd = cm.pmd
                )
                val request = toObject(CreateAwardsRequest::class.java, cm.data)
                awardService.create(context = context, data = request.convert())
                    .also { result: CreatedAwardsResult ->
                        if (log.isDebugEnabled)
                            log.debug("Awards were created. Result: ${toJson(result)}")
                    }
                    .convert()
            }
            CommandTypeV1.CREATE_AWARDS_AUCTION_END -> {
                val context = CreateAwardsAuctionEndContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid,
                    owner = cm.owner,
                    startDate = cm.startDate
                )
                val request = toObject(CreateAwardsAuctionEndRequest::class.java, cm.data)
                awardService.createAwardsAuctionEnd(context = context, data = request.convert())
                    .also { result: CreatedAwardsAuctionEndResult ->
                        if (log.isDebugEnabled)
                            log.debug("Awards were created (auction period end). Result: ${toJson(result)}")
                    }
                    .convert()
            }
            CommandTypeV1.AWARDS_CANCELLATION -> {
                val context = AwardCancellationContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid,
                    owner = cm.owner,
                    startDate = cm.startDate,
                    phase = cm.phase
                )
                val request: AwardCancellationRequest = toObject(AwardCancellationRequest::class.java, cm.data)
                awardService.cancellation(context = context, data = request.converter())
                    .also { result: AwardCancellationResult ->
                        if (log.isDebugEnabled)
                            log.debug("Award was cancelled. Result: ${toJson(result)}")
                    }
                    .convert()
            }
            CommandTypeV1.CHECK_AWARD_STATUS -> {
                val context = CheckAwardStatusContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid,
                    token = cm.token,
                    owner = cm.owner,
                    awardId = cm.awardId
                )
                awardService.checkStatus(context)
                    .also { result: CheckAwardStatusResult ->
                        if (log.isDebugEnabled)
                            log.debug("Checked award status. Result: ${toJson(result)}")
                    }
                    .let {
                        CheckAwardStatusResponse()
                    }
            }
            CommandTypeV1.END_AWARD_PERIOD -> {
                when (cm.pmd) {
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV,
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.CD, ProcurementMethod.TEST_CD,
                    ProcurementMethod.DC, ProcurementMethod.TEST_DC,
                    ProcurementMethod.IP, ProcurementMethod.TEST_IP,
                    ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
                    ProcurementMethod.RT, ProcurementMethod.TEST_RT ->  statusService.endAwardPeriod(cm)

                    ProcurementMethod.FA, ProcurementMethod.TEST_FA,
                    ProcurementMethod.CF, ProcurementMethod.TEST_CF,
                    ProcurementMethod.OF, ProcurementMethod.TEST_OF,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP -> throw ErrorException(ErrorType.INVALID_PMD)
                }
            }
            CommandTypeV1.GET_WINNING_AWARD -> {
                val context = GetWinningAwardContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid,
                    lotId = cm.lotId
                )
                awardService.getWinning(context = context)
                    .also { result: WinningAward? ->
                        if (log.isDebugEnabled) {
                            if (result == null)
                                log.debug("Winning award was not found.")
                            else
                                log.debug("Winning award was found with the id: '${result.id}'.")
                        }
                    }
                    .let { result: WinningAward? ->
                        WinningAwardResponse(
                            award = result?.let {
                                WinningAwardResponse.Award(id = it.id)
                            }
                        )
                    }
            }
            CommandTypeV1.GET_EVALUATED_AWARDS -> {
                val context = GetEvaluatedAwardsContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid,
                    lotId = cm.lotId
                )
                awardService.getEvaluated(context = context)
                    .also { result: List<EvaluatedAward> ->
                        if (log.isDebugEnabled) {
                            if (result.isEmpty())
                                log.debug("Evaluated award was not found.")
                            else
                                log.debug("Evaluated awards was found.")
                        }
                    }
                    .let { result: List<EvaluatedAward> ->
                        EvaluatedAwardsResponse(
                            awards = result.map { evaluatedAward ->
                                EvaluatedAwardsResponse.Award(
                                    statusDetails = evaluatedAward.statusDetails,
                                    relatedBid = evaluatedAward.relatedBid
                                )
                            }
                        )
                    }
            }
            CommandTypeV1.GET_AWARDS_FOR_AC -> statusService.getAwardsForAc(cm)
            CommandTypeV1.GET_LOT_FOR_CHECK -> statusService.getLotForCheck(cm)
            CommandTypeV1.GET_AWARD_ID_FOR_CHECK -> statusService.getAwardIdForCheck(cm)
            CommandTypeV1.FINAL_AWARDS_STATUS_BY_LOTS -> {
                val context = FinalAwardsStatusByLotsContext(cpid = cm.cpid, pmd = cm.pmd)
                val request = toObject(FinalAwardsStatusByLotsRequest::class.java, cm.data)
                val data = FinalAwardsStatusByLotsData(
                    lots = request.lots.map { lot ->
                        FinalAwardsStatusByLotsData.Lot(
                            id = lot.id
                        )
                    }
                )

                awardService.finalAwardsStatusByLots(context, data)
                    .also { result: FinalizedAwardsStatusByLots ->
                        if (log.isDebugEnabled)
                            log.debug("Awards were finalized. Result: ${toJson(result)}")
                    }
                    .let { result: FinalizedAwardsStatusByLots ->
                        FinalAwardsStatusByLotsResponse(
                            awards = result.awards.map { award ->
                                FinalAwardsStatusByLotsResponse.Award(
                                    id = award.id,
                                    status = award.status,
                                    statusDetails = award.statusDetails
                                )
                            }
                        )
                    }
            }
            CommandTypeV1.GET_UNSUCCESSFUL_LOTS -> {
                val context = GetUnsuccessfulLotsContext(
                    country = cm.country,
                    pmd = cm.pmd
                )
                val request = toObject(GetUnsuccessfulLotsRequest::class.java, cm.data)
                lotService.getUnsuccessfulLots(context = context, data = request.convert())
                    .also { result: GetUnsuccessfulLotsResult ->
                        if (log.isDebugEnabled)
                            log.debug("Unsuccessful lots. Result: ${toJson(result)}")
                    }
                    .convert()
            }
            CommandTypeV1.SET_AWARD_FOR_EVALUATION -> {
                val context = SetAwardForEvaluationContext(cpid = cm.cpid, ocid = cm.ocid)
                val request = toObject(SetAwardForEvaluationRequest::class.java, cm.data)
                awardService.setAwardForEvaluation(context = context, data = request.convert())
                    .also { result: SetAwardForEvaluationResult ->
                        if (log.isDebugEnabled)
                            log.debug("Set award for evaluation. Result: ${toJson(result)}")
                    }
                    .convert()
            }
            CommandTypeV1.START_AWARD_PERIOD -> {
                when (cm.pmd) {
                    ProcurementMethod.CF, ProcurementMethod.TEST_CF,
                    ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV,
                    ProcurementMethod.OF, ProcurementMethod.TEST_OF,
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.RT, ProcurementMethod.TEST_RT,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV -> {
                        val context = StartAwardPeriodContext(
                            cpid = cm.cpid,
                            ocid = cm.ocid,
                            startDate = cm.startDate
                        )
                        awardService.startAwardPeriod(context = context)
                            .also { result: StartAwardPeriodResult ->
                                if (log.isDebugEnabled)
                                    log.debug("Start award period. Result: ${toJson(result)}")
                            }
                            .convert()
                    }

                    ProcurementMethod.CD, ProcurementMethod.TEST_CD,
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.DC, ProcurementMethod.TEST_DC,
                    ProcurementMethod.FA, ProcurementMethod.TEST_FA,
                    ProcurementMethod.IP, ProcurementMethod.TEST_IP,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP -> throw ErrorException(ErrorType.INVALID_PMD)
                }
            }
            CommandTypeV1.CREATE_UNSUCCESSFUL_AWARDS -> {
                when (cm.pmd) {
                    ProcurementMethod.CD, ProcurementMethod.TEST_CD,
                    ProcurementMethod.CF, ProcurementMethod.TEST_CF,
                    ProcurementMethod.DA, ProcurementMethod.TEST_DA,
                    ProcurementMethod.DC, ProcurementMethod.TEST_DC,
                    ProcurementMethod.GPA, ProcurementMethod.TEST_GPA,
                    ProcurementMethod.IP, ProcurementMethod.TEST_IP,
                    ProcurementMethod.MV, ProcurementMethod.TEST_MV,
                    ProcurementMethod.NP, ProcurementMethod.TEST_NP,
                    ProcurementMethod.OF, ProcurementMethod.TEST_OF,
                    ProcurementMethod.OP, ProcurementMethod.TEST_OP,
                    ProcurementMethod.OT, ProcurementMethod.TEST_OT,
                    ProcurementMethod.RT, ProcurementMethod.TEST_RT,
                    ProcurementMethod.SV, ProcurementMethod.TEST_SV -> {
                        val context = CreateUnsuccessfulAwardsContext(
                            operationType = cm.operationType,
                            startDate = cm.startDate,
                            cpid = cm.cpid,
                            ocid = cm.ocid,
                            owner = cm.owner
                        )
                        val request = toObject(CreateUnsuccessfulAwardsRequest::class.java, cm.data)
                        awardService.createUnsuccessfulAwards(context = context, data = request.convert())
                            .also { result: CreateUnsuccessfulAwardsResult ->
                                if (log.isDebugEnabled)
                                    log.debug("Set award for evaluation. Result: ${toJson(result)}")
                            }
                            .convert()
                    }

                    ProcurementMethod.FA, ProcurementMethod.TEST_FA -> {
                        throw ErrorException(ErrorType.INVALID_PMD)
                    }

                }
            }
            CommandTypeV1.START_CONSIDERATION -> {
                val context = StartConsiderationContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid,
                    token = cm.token,
                    owner = cm.owner,
                    awardId = cm.awardId
                )
                awardService.startConsideration(context)
                    .also { result: StartConsiderationResult ->
                        if (log.isDebugEnabled)
                            log.debug("Started consideration. Result: ${toJson(result)}")
                    }
                    .convert()
            }
            CommandTypeV1.GET_NEXT_AWARD -> {
                val context = GetNextAwardContext(
                    cpid = cm.cpid,
                    ocid = cm.ocid,
                    awardId = cm.awardId
                )
                awardService.getNext(context)
                    .also { result: GetNextAwardResult ->
                        if (log.isDebugEnabled)
                            log.debug("Get next award. Result: ${toJson(result)}")
                    }
                    .convert()
            }
        }
        val response = ApiResponseV1.Success(id = cm.id, version = cm.version, data = dataOfResponse)
            .also {
                if (log.isDebugEnabled)
                    log.debug("Response: ${toJson(it)}")
            }
        historyRepository.saveHistory(cm.id, cm.command, toJson(response))
            .doOnError {
                log.error("Error of save history. ${it.description}", it.exception)
            }
        return response
    }
}
