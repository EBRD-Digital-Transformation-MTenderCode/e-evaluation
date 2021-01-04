package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.application.exception.SaveEntityException
import com.procurement.evaluation.application.model.award.access.CheckAccessToAwardParams
import com.procurement.evaluation.application.model.award.check.state.CheckAwardStateErrors
import com.procurement.evaluation.application.model.award.check.state.CheckAwardStateParams
import com.procurement.evaluation.application.model.award.close.awardperiod.CloseAwardPeriodParams
import com.procurement.evaluation.application.model.award.create.CreateAwardParams
import com.procurement.evaluation.application.model.award.find.FindAwardsForProtocolParams
import com.procurement.evaluation.application.model.award.get.GetAwardByIdsParams
import com.procurement.evaluation.application.model.award.requirement.response.AddRequirementResponseParams
import com.procurement.evaluation.application.model.award.start.awardperiod.StartAwardPeriodParams
import com.procurement.evaluation.application.model.award.state.GetAwardStateByIdsParams
import com.procurement.evaluation.application.model.award.tenderer.CheckRelatedTendererParams
import com.procurement.evaluation.application.model.award.unsuccessful.CreateUnsuccessfulAwardsParams
import com.procurement.evaluation.application.model.award.update.UpdateAwardErrors
import com.procurement.evaluation.application.model.award.update.UpdateAwardParams
import com.procurement.evaluation.application.model.award.validate.ValidateAwardDataParams
import com.procurement.evaluation.application.repository.award.AwardRepository
import com.procurement.evaluation.application.repository.award.model.AwardEntity
import com.procurement.evaluation.application.repository.period.AwardPeriodRepository
import com.procurement.evaluation.application.service.GenerationService
import com.procurement.evaluation.application.service.RulesService
import com.procurement.evaluation.application.service.Transform
import com.procurement.evaluation.application.service.award.rules.CheckAwardsStateRules
import com.procurement.evaluation.application.service.award.rules.UpdateAwardRules
import com.procurement.evaluation.application.service.award.rules.ValidateAwardDataRules
import com.procurement.evaluation.application.service.award.strategy.CloseAwardPeriodStrategy
import com.procurement.evaluation.application.service.award.strategy.CreateUnsuccessfulAwardsStrategy
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.domain.model.data.CoefficientRate
import com.procurement.evaluation.domain.model.data.CoefficientValue
import com.procurement.evaluation.domain.model.data.RequirementRsValue
import com.procurement.evaluation.domain.model.document.DocumentId
import com.procurement.evaluation.domain.model.enums.OperationType
import com.procurement.evaluation.domain.model.enums.Stage
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.domain.model.money.Money
import com.procurement.evaluation.domain.model.state.States
import com.procurement.evaluation.domain.util.extension.doOnFalse
import com.procurement.evaluation.domain.util.extension.mapResult
import com.procurement.evaluation.domain.util.extension.mapResultPair
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.exception.ErrorType.ALREADY_HAVE_ACTIVE_AWARDS
import com.procurement.evaluation.exception.ErrorType.AWARD_NOT_FOUND
import com.procurement.evaluation.exception.ErrorType.DATA_NOT_FOUND
import com.procurement.evaluation.exception.ErrorType.INVALID_OWNER
import com.procurement.evaluation.exception.ErrorType.INVALID_STAGE
import com.procurement.evaluation.exception.ErrorType.INVALID_STATUS
import com.procurement.evaluation.exception.ErrorType.INVALID_STATUS_DETAILS
import com.procurement.evaluation.exception.ErrorType.INVALID_TOKEN
import com.procurement.evaluation.exception.ErrorType.RELATED_LOTS
import com.procurement.evaluation.exception.ErrorType.SUPPLIER_IS_NOT_UNIQUE_IN_AWARD
import com.procurement.evaluation.exception.ErrorType.SUPPLIER_IS_NOT_UNIQUE_IN_LOT
import com.procurement.evaluation.exception.ErrorType.UNKNOWN_SCALE_SUPPLIER
import com.procurement.evaluation.exception.ErrorType.UNKNOWN_SCHEME_IDENTIFIER
import com.procurement.evaluation.exception.ErrorType.UNKNOWN_SUPPLIER_COUNTRY
import com.procurement.evaluation.exception.ErrorType.WRONG_NUMBER_OF_SUPPLIERS
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.infrastructure.fail.error.ValidationError
import com.procurement.evaluation.infrastructure.handler.v2.converter.toDomain
import com.procurement.evaluation.infrastructure.handler.v2.model.request.UpdateAwardResult
import com.procurement.evaluation.infrastructure.handler.v2.model.response.CloseAwardPeriodResult
import com.procurement.evaluation.infrastructure.handler.v2.model.response.CreateAwardResult
import com.procurement.evaluation.infrastructure.handler.v2.model.response.FindAwardsForProtocolResult
import com.procurement.evaluation.infrastructure.handler.v2.model.response.GetAwardByIdsResult
import com.procurement.evaluation.infrastructure.handler.v2.model.response.GetAwardStateByIdsResult
import com.procurement.evaluation.lib.errorIfBlank
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.Result.Companion.failure
import com.procurement.evaluation.lib.functional.Result.Companion.success
import com.procurement.evaluation.lib.functional.Validated
import com.procurement.evaluation.lib.functional.asFailure
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.lib.functional.asValidationError
import com.procurement.evaluation.lib.toSetBy
import com.procurement.evaluation.lib.uniqueBy
import com.procurement.evaluation.model.dto.ocds.Address
import com.procurement.evaluation.model.dto.ocds.AddressDetails
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.AwardCriteria
import com.procurement.evaluation.model.dto.ocds.AwardCriteriaDetails
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.ContactPoint
import com.procurement.evaluation.model.dto.ocds.ConversionsRelatesTo
import com.procurement.evaluation.model.dto.ocds.CountryDetails
import com.procurement.evaluation.model.dto.ocds.Details
import com.procurement.evaluation.model.dto.ocds.Document
import com.procurement.evaluation.model.dto.ocds.DocumentType
import com.procurement.evaluation.model.dto.ocds.Identifier
import com.procurement.evaluation.model.dto.ocds.LocalityDetails
import com.procurement.evaluation.model.dto.ocds.MainEconomicActivity
import com.procurement.evaluation.model.dto.ocds.OrganizationReference
import com.procurement.evaluation.model.dto.ocds.Phase
import com.procurement.evaluation.model.dto.ocds.RegionDetails
import com.procurement.evaluation.model.dto.ocds.RequirementResponse
import com.procurement.evaluation.model.dto.ocds.Value
import com.procurement.evaluation.model.dto.ocds.asMoney
import com.procurement.evaluation.model.dto.ocds.asValue
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toObject
import com.procurement.evaluation.utils.tryToObject
import org.springframework.stereotype.Service
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*
import com.procurement.evaluation.infrastructure.handler.v2.model.response.StartAwardPeriodResult as StartAwardPeriodResultV2

interface AwardService {
    fun create(context: CreateAwardContext, data: CreateAwardData): CreatedAwardData

    fun create(context: CreateAwardsContext, data: CreateAwardsData): CreatedAwardsResult

    fun createAwardsAuctionEnd(
        context: CreateAwardsAuctionEndContext,
        data: CreateAwardsAuctionEndData
    ): CreatedAwardsAuctionEndResult

    fun evaluate(context: EvaluateAwardContext, data: EvaluateAwardData): EvaluateAwardResult

    fun getWinning(context: GetWinningAwardContext): WinningAward?

    fun getEvaluated(context: GetEvaluatedAwardsContext): List<EvaluatedAward>

    fun finalAwardsStatusByLots(
        context: FinalAwardsStatusByLotsContext,
        data: FinalAwardsStatusByLotsData
    ): FinalizedAwardsStatusByLots

    fun setAwardForEvaluation(
        context: SetAwardForEvaluationContext,
        data: SetAwardForEvaluationData
    ): SetAwardForEvaluationResult

    fun startAwardPeriod(
        context: StartAwardPeriodContext
    ): StartAwardPeriodResult

    fun createUnsuccessfulAwards(
        context: CreateUnsuccessfulAwardsContext,
        data: CreateUnsuccessfulAwardsData
    ): CreateUnsuccessfulAwardsResult

    fun checkStatus(context: CheckAwardStatusContext): CheckAwardStatusResult

    fun startConsideration(context: StartConsiderationContext): StartConsiderationResult

    fun getNext(context: GetNextAwardContext): GetNextAwardResult

    fun cancellation(context: AwardCancellationContext, data: AwardCancellationData): AwardCancellationResult

    fun getAwardState(params: GetAwardStateByIdsParams): Result<List<GetAwardStateByIdsResult>, Failure>

    fun getAwardByIds(params: GetAwardByIdsParams): Result<GetAwardByIdsResult, Failure>

    fun findAwardsForProtocol(params: FindAwardsForProtocolParams): Result<FindAwardsForProtocolResult?, Failure>

    fun checkAccessToAward(params: CheckAccessToAwardParams): Validated<Failure>

    fun checkAwardState(params: CheckAwardStateParams): Validated<Failure>

    fun checkRelatedTenderer(params: CheckRelatedTendererParams): Validated<Failure>

    fun validateAwardData(params: ValidateAwardDataParams): Validated<Failure>

    fun createAward(params: CreateAwardParams): Result<CreateAwardResult, Failure>

    fun updateAward(params: UpdateAwardParams): Result<UpdateAwardResult, Failure>

    fun addRequirementResponse(params: AddRequirementResponseParams): Validated<Failure>

    fun createUnsuccessfulAwards(params: CreateUnsuccessfulAwardsParams)
        : Result<List<com.procurement.evaluation.infrastructure.handler.v2.model.response.CreateUnsuccessfulAwardsResult>, Failure>

    fun closeAwardPeriod(params: CloseAwardPeriodParams): Result<CloseAwardPeriodResult, Failure>

    fun startAwardPeriod(params: StartAwardPeriodParams): Result<StartAwardPeriodResultV2, Failure>
}

@Service
class AwardServiceImpl(
    private val generationService: GenerationService,
    private val awardRepository: AwardRepository,
    private val awardPeriodRepository: AwardPeriodRepository,
    private val transform: Transform,
    private val rulesService: RulesService
) : AwardService {

    val createUnsuccessfulAwardsStrategy = CreateUnsuccessfulAwardsStrategy(
        awardRepository = awardRepository,
        generationService = generationService
    )

    val closeAwardPeriodStrategy = CloseAwardPeriodStrategy(awardPeriodRepository = awardPeriodRepository)

    /**
     * BR-7.10.1 General Rule
     *
     * 1. Checks award.statusDetails in saved Awards object in DB by rule BR-7.10.7;
     * 2. FOR award.suppliers object from Request system:
     *   a. Validates supplier.identifier.schema by rule VR-7.10.6;
     *   b. Validates supplier.identifier.scale by rule VR-7.10.7;
     *   c. Generates award.supplier.ID by rule BR-7.10.3 and put them to list suppliersIdList;
     * 3. Validates suppliersIdList (got before) by rule VR-7.10.8;
     * 4. Create new Award object according to the following order:
     *   a. Gets next fields and objects from Request:
     *     i.   award.Description;
     *     ii.  award.Value;
     *     iii. award.Supplier;
     *   b. Calculates next fields according to the next order:
     *     i.   Generates unique award.ID as a timeBasedUUID;
     *     ii.  Adds to award.relatedLots value from ID parameter of Request;
     *     iii. Sets award.status & award.statusDetails by rule BR-7.10.2;
     *     iV.  Sets award.date == startDate value from the context of Request;
     *     v.   Adds award.supplier.ID (generated before) for every supplier object;
     *     vi.  Generates Token value by Award;
     *   c. Saves created Award in DB with next fields of table:
     *     i.   Owner field == Owner value from the context of Request;
     *     ii.  CPID field == CPID value from the context of Request;
     *     iii. Stage field == Stage value from the context of Request;
     *     iv.  Token field (generated of step 2.f);
     * 5. Returns created Award for Response;
     * 6. Returns created Token for Response;
     * 7. Checks the availability of awardPeriod object in DB by CPID and Stage values from Request:
     *   a. IF there is no awardPeriod, eEvaluation executes next steps:
     *     i.   Sets awardPeriod.startDate == startDate value from the context of Request;
     *     ii.  Saves awardPeriod in DB with CPID and Stage values;
     *     iii. Returns created awardPeriod object for Response;
     *   b. ELSE (awardPeriod was found in DB)
     *        eEvaluation does not perform any operation;
     */
    override fun create(context: CreateAwardContext, data: CreateAwardData): CreatedAwardData {
        val cpid = context.cpid
        val ocid = context.ocid

        //VR-7.10.9
        checkNumberSuppliers(suppliers = data.award.suppliers)

        // VR-7.10.6
        checkSchemeOfIdentifier(data)

        // VR-7.10.7
        checkScaleOfSupplier(data)

        //VR-7.10.8(1)
        checkSuppliersIdentifiers(suppliers = data.award.suppliers)

        val awardsEntities = awardRepository.findBy(cpid = cpid).orThrow { it.exception }

        val awards = awardsEntities.map { toObject(Award::class.java, it.jsonData) }

        //VR-7.10.8(2)
        checkSuppliers(lotId = context.lotId, suppliers = data.award.suppliers, awards = awards)

        // BR-7.10.7
        val lotAwarded = getLotAwarded(awards = awards, lotId = context.lotId)

        val awardId = generationService.awardId()
        val token = generationService.token()
        val award = Award(
            id = awardId.toString(),
            token = token.toString(),
            title = null,
            description = data.award.description,
            date = context.startDate,
            // BR-7.10.2
            status = AwardStatus.PENDING,
            // BR-7.10.2
            statusDetails = AwardStatusDetails.EMPTY,
            relatedLots = listOf(context.lotId.toString()),
            relatedBid = null,
            bidDate = null,
            value = data.award.value.let { value ->
                Value(
                    amount = value.amount,
                    currency = value.currency
                )
            },
            suppliers = data.award.suppliers.map { supplier ->
                OrganizationReference(
                    id = supplier.identifier.let { identifier ->
                        supplierId(identifierScheme = identifier.scheme, identifierId = identifier.id)
                    },
                    name = supplier.name,
                    identifier = supplier.identifier.let { identifier ->
                        Identifier(
                            scheme = identifier.scheme,
                            id = identifier.id,
                            legalName = identifier.legalName,
                            uri = identifier.uri
                        )
                    },
                    additionalIdentifiers = supplier.additionalIdentifiers?.asSequence()
                        ?.map { additionalIdentifier ->
                            Identifier(
                                scheme = additionalIdentifier.scheme,
                                id = additionalIdentifier.id,
                                legalName = additionalIdentifier.legalName,
                                uri = additionalIdentifier.uri
                            )
                        }
                        ?.toMutableList(),
                    address = supplier.address.let { address ->
                        Address(
                            streetAddress = address.streetAddress,
                            postalCode = address.postalCode,
                            addressDetails = address.addressDetails.let { detail ->
                                AddressDetails(
                                    country = detail.country.let { country ->
                                        CountryDetails(
                                            scheme = country.scheme,
                                            id = country.id,
                                            description = country.description,
                                            uri = country.uri
                                        )
                                    },
                                    region = detail.region.let { region ->
                                        RegionDetails(
                                            scheme = region.scheme,
                                            id = region.id,
                                            description = region.description,
                                            uri = region.uri
                                        )
                                    },
                                    locality = detail.locality.let { locality ->
                                        LocalityDetails(
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
                        ContactPoint(
                            name = contactPoint.name,
                            email = contactPoint.email,
                            telephone = contactPoint.telephone,
                            faxNumber = contactPoint.faxNumber,
                            url = contactPoint.url
                        )
                    },
                    details = Details(
                        scale = supplier.details.scale,
                        permits = null,
                        mainEconomicActivities = null,
                        legalForm = null,
                        bankAccounts = null,
                        typeOfSupplier = null
                    ),
                    persones = null
                )
            },
            documents = null,
            items = null,
            weightedValue = null,
            internalId = null
        )

        val prevAwardPeriodStart = awardPeriodRepository.findBy(cpid = cpid, ocid = ocid)
            .orThrow { it.exception }
            ?.startDate

        val newAwardEntity = AwardEntity(
            cpid = cpid,
            ocid = ocid,
            status = award.status,
            statusDetails = award.statusDetails,
            token = UUID.fromString(award.token),
            owner = context.owner,
            jsonData = toJson(award)
        )

        //FIXME Consistency cannot be guaranteed
        val awardPeriodStart = if (prevAwardPeriodStart != null) {
            prevAwardPeriodStart
        } else {
            val newAwardPeriodStart = context.startDate
            val wasApplied = awardPeriodRepository.saveStart(cpid = cpid, ocid = ocid, start = newAwardPeriodStart)
                .orThrow { SaveEntityException(message = "Error writing start date of the award period.", cause = it.exception) }

            if (!wasApplied)
                throw SaveEntityException(message = "An error occurred when writing a record(s) of the start award period '$newAwardPeriodStart' by cpid '$cpid' and ocid '$ocid' to the database. Record is already.")

            newAwardPeriodStart
        }

        val wasApplied = awardRepository.save(cpid = cpid, award = newAwardEntity)
            .orThrow { fail -> SaveEntityException(message = "Error writing new award to database.", cause = fail.exception) }

        if(!wasApplied)
            throw SaveEntityException("An error occurred when writing a record(s) of the award by cpid '$cpid' and ocid '${newAwardEntity.ocid}' to the database. Record is already.")

        return getCreatedAwardData(award, awardPeriodStart, lotAwarded)
    }

    /**
     * BR-7.10.3 Supplier.[ID] (award)
     *
     * eEvaluation concatenates: ({supplier.identifier.scheme} "-" {supplier.identifier.id}) and saves value as supplier.ID.
     */
    private fun supplierId(identifierScheme: String, identifierId: String): String =
        "$identifierScheme-$identifierId"

    /**
     * BR-7.10.7
     *
     * "statusDetails" (awards) "lotAwarded"
     *
     * Selects all Award objects in DB where award.relatedLots == ID from the context of Request && cpid == CPID from the context of Request:
     * a. IF [list of selected Awards != NULL]
     *    then: eEvaluation checks award.status && award.statusDetails in all selected Awards found previously:
     *   i.  IF [there is award where award.status == "pending" && award.statusDetails =="active"]
     *         then: eEvaluation does not perform any operation;
     *   ii. ELSE [there is NO award with award.status == "pending" && award.statusDetails == "active"]
     *       eEvaluation checks the availability of saved award by lot with award.status == "pending" && statusDetails == "empty":
     *       1. IF [there is saved award or awards by lot with award.status == "pending" && award.statusDetails == "empty"]
     *          then: eEvaluation does not perform any operation;
     *       2. ELSE [no saved award by lot with  award.status == "pending" && award.statusDetails == "empty"]
     *          eEvaluation sets the value "lotAwarded" attribute = FALSE & adds it to Response;
     * b. ELSE [awards list = NULL]
     *    then: eEvaluation does not perform any operation;
     */
    private fun getLotAwarded(awards: List<Award>, lotId: UUID): Boolean? {
        if (awards.isEmpty()) return null

        val selectedAwards = awards.filter {
            lotId.toString() in it.relatedLots
        }
        if (selectedAwards.isEmpty()) return null

        val thereIsActiveAward = selectedAwards.any {
            it.status == AwardStatus.PENDING && it.statusDetails == AwardStatusDetails.ACTIVE
        }
        if (thereIsActiveAward) return null

        val thereIsEmptyAward = selectedAwards.any {
            it.status == AwardStatus.PENDING && it.statusDetails == AwardStatusDetails.EMPTY
        }
        return if (thereIsEmptyAward)
            null
        else
            false
    }

    /**
     * VR-7.10.6 Schema (Supplier.Identifier)
     *
     * eEvaluation compares supplier.identifier.schema value with schemas array from Request:
     * IF oneOf schemas value == supplier.identifier.schema value
     *   validation is successful;
     * ELSE
     *   eEvaluation throws Exception: "Undefined identifier schema";
     */
    private fun checkSchemeOfIdentifier(data: CreateAwardData) {
        val schemesByCountries = data.mdm.organizationSchemesByCountries
            .associateBy(
                keySelector = { it.country },
                valueTransform = {
                    it.schemes.toSetBy { scheme ->
                        scheme.toUpperCase()
                    }
                }
            )

        data.award.suppliers
            .forEach { supplier ->
                val schemes = schemesByCountries[supplier.address.addressDetails.country.id]
                    ?: throw ErrorException(error = UNKNOWN_SUPPLIER_COUNTRY)
                if (supplier.identifier.scheme.toUpperCase() !in schemes) {
                    throw ErrorException(error = UNKNOWN_SCHEME_IDENTIFIER)
                }
            }
    }

    /**
     * VR-7.10.7 Scale (Supplier)
     *
     * eEvaluation compares supplier.scale value with scales array from Request:
     * IF oneOf scales value == supplier.scale value
     *   validation is successful;
     * ELSE
     *   eEvaluation throws Exception: "Undefined supplier scale";
     */
    private fun checkScaleOfSupplier(data: CreateAwardData) {
        val scales = data.mdm.scales
            .asSequence()
            .map { it.toUpperCase() }
            .toSet()

        val invalidScale = data.award.suppliers.any { supplier ->
            supplier.details.scale.toUpperCase() !in scales
        }

        if (invalidScale)
            throw ErrorException(error = UNKNOWN_SCALE_SUPPLIER)
    }

    /**
     * VR-7.10.8 ID (Supplier)
     *
     * 1. eEvaluation checks supplier.ID value uniqueness in list (got before):
     *   a. IF [there is no repeated supplier.ID] then:
     *        validation is successful;
     *   b. ELSE [there are same ID]
     *        eEvaluation throws Exception: "Supplier Identifiers should be unique in Award";
     */
    private fun checkSuppliersIdentifiers(suppliers: List<CreateAwardData.Award.Supplier>) {
        val suppliersIds: MutableSet<String> = mutableSetOf()
        suppliers.forEach { supplier ->
            val id = supplierId(identifierScheme = supplier.identifier.scheme, identifierId = supplier.identifier.id)
            if (!suppliersIds.add(id))
                throw ErrorException(error = SUPPLIER_IS_NOT_UNIQUE_IN_AWARD)
        }
    }

    /**
     * VR-7.10.8 ID (Supplier)
     *
     * 2. Selects all Award objects in DB where
     *     award.relatedLots == ID from the context of Request
     *     && cpid == CPID from the context of Request
     *     && award.status == "pending":
     *   a. IF [list of selected Awards != NULL] then:
     *     i.   Selects all Award objects in DB where
     *            award.relatedLots == ID from the context of Request
     *            && cpid == CPID && award.status == "pending"
     *            and saves them to list in memory;
     *     ii.  forEach award object from list (got before) system get.award.supplier.ID from every supplier object
     *          in award and puts them to dbSupplierIdList;
     *     iii. Compares dbSupplierIdListlist (got before) with list of supplier.ID of Award from Request:
     *       1. IF [oneOf ID (from dbSupplierIdList) == oneOf supplier.ID list from request]
     *            eEvaluation throws Exception: "One supplier can not submit more then one offer per lot";
     *       2. ELSE [no same ID in compared lists]
     *            validation is successful;
     *   b. ELSE [awards list = NULL]
     *        validation is successful;
     */
    private fun checkSuppliers(lotId: UUID, suppliers: List<CreateAwardData.Award.Supplier>, awards: List<Award>) {
        val lotIdAsString = lotId.toString()
        val awardsByLot = awards.filter {
            lotIdAsString in it.relatedLots && it.status == AwardStatus.PENDING
        }
        if (awardsByLot.isEmpty()) return

        val suppliersIds = awardsByLot.asSequence()
            .flatMap { award ->
                award.suppliers!!.asSequence()
                    .map { supplier ->
                        supplierId(identifierScheme = supplier.identifier.scheme, identifierId = supplier.identifier.id)
                    }
            }
            .toSet()

        suppliers.forEach { supplier ->
            val supplierId = supplierId(
                identifierScheme = supplier.identifier.scheme,
                identifierId = supplier.identifier.id
            )
            if (supplierId in suppliersIds)
                throw ErrorException(error = SUPPLIER_IS_NOT_UNIQUE_IN_LOT)
        }
    }

    /**
     * VR-7.10.9 supplier (award)
     *
     * 1. eEvaluation checks the availability of one Supplier object in award.suppliers array from Request:
     *   a. IF there is only one Supplier object in Request
     *      validation is successful;
     *   b. ELSE system throws Exception:
     *      "At least one supplier should be defined";
     */
    private fun checkNumberSuppliers(suppliers: List<CreateAwardData.Award.Supplier>) {
        if (suppliers.isEmpty())
            throw ErrorException(
                error = WRONG_NUMBER_OF_SUPPLIERS,
                message = "At least one supplier should be defined."
            )
        if (suppliers.size > 1)
            throw ErrorException(
                error = WRONG_NUMBER_OF_SUPPLIERS,
                message = "Only one supplier should by transferred."
            )
    }

    private fun getCreatedAwardData(
        award: Award,
        awardPeriodStart: LocalDateTime,
        lotAwarded: Boolean?
    ): CreatedAwardData = CreatedAwardData(
        token = Token.fromString(award.token!!),
        awardPeriod = CreatedAwardData.AwardPeriod(startDate = awardPeriodStart),
        lotAwarded = lotAwarded,
        award = CreatedAwardData.Award(
            id = award.id,
            date = award.date!!,
            status = award.status,
            statusDetails = award.statusDetails,
            relatedLots = award.relatedLots.toList(),
            description = award.description,
            value = award.value!!.let { value ->
                CreatedAwardData.Award.Value(
                    amount = value.amount,
                    currency = value.currency!!
                )
            },
            suppliers = award.suppliers!!.map { supplier ->
                CreatedAwardData.Award.Supplier(
                    id = supplier.id,
                    name = supplier.name,
                    identifier = supplier.identifier.let { identifier ->
                        CreatedAwardData.Award.Supplier.Identifier(
                            scheme = identifier.scheme,
                            id = identifier.id,
                            legalName = identifier.legalName,
                            uri = identifier.uri
                        )
                    },
                    additionalIdentifiers = supplier.additionalIdentifiers?.map { additionalIdentifier ->
                        CreatedAwardData.Award.Supplier.AdditionalIdentifier(
                            scheme = additionalIdentifier.scheme,
                            id = additionalIdentifier.id,
                            legalName = additionalIdentifier.legalName,
                            uri = additionalIdentifier.uri
                        )
                    },
                    address = supplier.address.let { address ->
                        CreatedAwardData.Award.Supplier.Address(
                            streetAddress = address.streetAddress,
                            postalCode = address.postalCode,
                            addressDetails = address.addressDetails.let { detail ->
                                CreatedAwardData.Award.Supplier.Address.AddressDetails(
                                    country = detail.country.let { country ->
                                        CreatedAwardData.Award.Supplier.Address.AddressDetails.Country(
                                            scheme = country.scheme!!,
                                            id = country.id,
                                            description = country.description!!,
                                            uri = country.uri!!
                                        )
                                    },
                                    region = detail.region.let { region ->
                                        CreatedAwardData.Award.Supplier.Address.AddressDetails.Region(
                                            scheme = region.scheme!!,
                                            id = region.id,
                                            description = region.description!!,
                                            uri = region.uri!!
                                        )
                                    },
                                    locality = detail.locality.let { locality ->
                                        CreatedAwardData.Award.Supplier.Address.AddressDetails.Locality(
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
                        CreatedAwardData.Award.Supplier.ContactPoint(
                            name = contactPoint.name,
                            email = contactPoint.email,
                            telephone = contactPoint.telephone,
                            faxNumber = contactPoint.faxNumber,
                            url = contactPoint.url
                        )
                    },
                    details = supplier.details.let { details ->
                        CreatedAwardData.Award.Supplier.Details(
                            scale = details!!.scale
                        )
                    }
                )
            }
        )
    )

    /**
     * BR-7.10.4
     *
     * eEvaluation performs next steps:
     * 1. Validates token & ID (award.ID) values from the context of Request by rule VR-7.10.1;
     * 2. Validates Owner value from the context of Request by rule VR-7.10.2;
     * 3. Validates award.statusDetails value from Request by rule VR-7.10.4;
     * 4. Validates award.documents.documentType value in every Documents from Request by rule VR-7.10.3;
     * 5. Validates award.documents.relatedLots value in every Documents from Request by rule VR-7.10.5;
     * 6. Finds saved version of Award in DB by ID (award.ID) & Stage values from the context of Request;
     * 7. Updated Award found before according to the next order:
     *   a. Updates award.Description == award.description from Request;
     *   b. Proceeds award.Documents by rule BR-7.10.5;
     *   c. Proceeds award.statusDetails by rule BR-7.10.6;
     *   d. Sets award.Date == startDate from the context of Request;
     * 8. Returns updated Award for Response getting next fields:
     *   - Award.ID;
     *   - Award.relatedLots;
     *   - Award.description;
     *   - Award.status;
     *   - Award.statusDetails;
     *   - Award.supplier.ID;
     *   - Award.supplier.name;
     *   - Award.value;
     *   - Award.date;
     *   - Award.documents;
     */
    override fun evaluate(context: EvaluateAwardContext, data: EvaluateAwardData): EvaluateAwardResult {
        data.validateTextAttributes()
        val cpid = context.cpid

        val awardEntity = awardRepository.findBy(cpid = cpid, ocid = context.ocid, token = context.token)
            .orThrow { it.exception }
            ?: throw ErrorException(error = AWARD_NOT_FOUND)

        //VR-7.10.1
        if (context.token != awardEntity.token)
            throw ErrorException(error = INVALID_TOKEN)

        //VR-7.10.2
        if (context.owner != awardEntity.owner)
            throw ErrorException(error = INVALID_OWNER)

        val award = toObject(Award::class.java, awardEntity.jsonData)

        //VR-7.10.1
        if (context.awardId != AwardId.fromString(award.id))
            throw ErrorException(error = AWARD_NOT_FOUND)

        //FVR-7.1.2.1.4
        checkStatusDetails(context = context, data = data, award = award)

        //FVR-7.1.2.2.1, FVR-7.1.2.2.2, FVR-7.1.2.2.3, FVR-7.1.2.2.4
        checkDocuments(data = data, award = award)

        //FR-7.1.2.1.1
        val updatedDocuments = updateDocuments(data = data, award = award)

        val updatedAward = award.copy(
            description = data.award.description,
            //FR-7.1.2.1.1
            documents = updatedDocuments,

            statusDetails = data.award.statusDetails,
            date = context.startDate
        )

        val updatedAwardEntity = awardEntity.copy(
            statusDetails = updatedAward.statusDetails,
            jsonData = toJson(updatedAward)
        )

        val wasApplied = awardRepository.update(cpid = cpid, updatedAward = updatedAwardEntity)
            .orThrow { it.exception }

        if(!wasApplied)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the award by cpid '$cpid' and ocid '${updatedAwardEntity.ocid}' and token to the database. Record is already.")


        return getEvaluateAwardResult(updatedAward = updatedAward)
    }

    private fun checkStatusDetails(
        context: EvaluateAwardContext,
        data: EvaluateAwardData,
        award: Award
    ) {
        val stage = context.ocid.stage
        when (data.award.statusDetails) {
            AwardStatusDetails.UNSUCCESSFUL -> {
                checkStatusDetailsForStage(stage = stage, statusDetails = award.statusDetails)
            }
            AwardStatusDetails.ACTIVE -> {
                checkStatusDetailsForStage(stage = stage, statusDetails = award.statusDetails)
                checkRelatedAwards(stage = stage, context = context, award = award)
            }

            AwardStatusDetails.PENDING,
            AwardStatusDetails.CONSIDERATION,
            AwardStatusDetails.EMPTY,
            AwardStatusDetails.AWAITING,
            AwardStatusDetails.NO_OFFERS_RECEIVED,
            AwardStatusDetails.LOT_CANCELLED,
            AwardStatusDetails.LACK_OF_QUALIFICATIONS,
            AwardStatusDetails.LACK_OF_SUBMISSIONS -> throw ErrorException(
                error = INVALID_STATUS_DETAILS,
                message = "Invalid status details of award from request (${data.award.statusDetails.key})."
            )
        }
    }

    private fun checkStatusDetailsForStage(stage: Stage, statusDetails: AwardStatusDetails) {
        when (stage) {
            Stage.EV,
            Stage.TP,
            Stage.PC -> {
                when (statusDetails) {
                    AwardStatusDetails.UNSUCCESSFUL,
                    AwardStatusDetails.ACTIVE,
                    AwardStatusDetails.CONSIDERATION -> Unit

                    AwardStatusDetails.PENDING,
                    AwardStatusDetails.EMPTY,
                    AwardStatusDetails.AWAITING,
                    AwardStatusDetails.NO_OFFERS_RECEIVED,
                    AwardStatusDetails.LOT_CANCELLED,
                    AwardStatusDetails.LACK_OF_QUALIFICATIONS,
                    AwardStatusDetails.LACK_OF_SUBMISSIONS -> throw ErrorException(
                        error = INVALID_STATUS_DETAILS,
                        message = "Invalid status details of award from database (${statusDetails.key}) by stage '${stage}'."
                    )
                }
            }
            Stage.NP -> {
                when (statusDetails) {
                    AwardStatusDetails.UNSUCCESSFUL,
                    AwardStatusDetails.ACTIVE,
                    AwardStatusDetails.EMPTY -> Unit

                    AwardStatusDetails.CONSIDERATION,
                    AwardStatusDetails.PENDING,
                    AwardStatusDetails.AWAITING,
                    AwardStatusDetails.NO_OFFERS_RECEIVED,
                    AwardStatusDetails.LOT_CANCELLED,
                    AwardStatusDetails.LACK_OF_SUBMISSIONS,
                    AwardStatusDetails.LACK_OF_QUALIFICATIONS -> throw ErrorException(
                        error = INVALID_STATUS_DETAILS,
                        message = "Invalid status details of award from database (${statusDetails.key}) by stage 'NP'."
                    )
                }
            }
            Stage.PN,
            Stage.FS,
            Stage.FE,
            Stage.EI,
            Stage.AC -> throw ErrorException(error = INVALID_STAGE)
        }
    }

    private fun checkRelatedAwards(stage: Stage, context: EvaluateAwardContext, award: Award) {
        when (stage) {
            Stage.EV,
            Stage.TP,
            Stage.NP -> {
                val lots = award.relatedLots.toSet()
                val relatedAwards = awardRepository.findBy(cpid = context.cpid, ocid = context.ocid)
                    .orThrow { it.exception }
                    .asSequence()
                    .map { entity ->
                        toObject(Award::class.java, entity.jsonData)
                    }
                    .filter {
                        if (AwardId.fromString(it.id) == context.awardId)
                            false
                        else
                            lots.containsAll(it.relatedLots)
                    }
                    .toList()

                if (isNotAcceptableStatusDetails(relatedAwards))
                    throw ErrorException(error = ALREADY_HAVE_ACTIVE_AWARDS)
            }
            Stage.AC,
            Stage.EI,
            Stage.FE,
            Stage.FS,
            Stage.PN,
            Stage.PC -> Unit
        }
    }

    private fun isNotAcceptableStatusDetails(awards: List<Award>) =
        awards.any { award -> award.statusDetails == AwardStatusDetails.ACTIVE }

    private fun checkDocuments(data: EvaluateAwardData, award: Award) {
        val documentIdsIsNotUnique = !data.award.documents.uniqueBy { it.id }
        if (documentIdsIsNotUnique)
            throw throw ErrorException(error = ErrorType.DUPLICATE_ID, message = "Ids of documents are not unique.")

        data.award.documents.forEach { document ->
            when (document.documentType) {
                DocumentType.AWARD_NOTICE,
                DocumentType.EVALUATION_REPORTS,
                DocumentType.SHORTLISTED_FIRMS,
                DocumentType.WINNING_BID,
                DocumentType.COMPLAINTS,
                DocumentType.BIDDERS,
                DocumentType.CONFLICT_OF_INTEREST,
                DocumentType.CANCELLATION_DETAILS,
                DocumentType.CONTRACT_DRAFT,
                DocumentType.CONTRACT_ARRANGEMENTS,
                DocumentType.CONTRACT_SCHEDULE,
                DocumentType.SUBMISSION_DOCUMENTS -> Unit
            }
        }

        val relatedLotsFromDocuments: Set<LotId> = data.award.documents
            .asSequence()
            .flatMap { it.relatedLots.asSequence() }
            .toSet()

        if (relatedLotsFromDocuments.isNotEmpty()) {
            val relatedLotsFromAward: Set<LotId> = award.relatedLots
                .asSequence()
                .map { relatedLot ->
                    LotId.fromString(relatedLot)
                }
                .toSet()

            if (!relatedLotsFromDocuments.containsAll(relatedLotsFromAward))
                throw throw ErrorException(error = RELATED_LOTS)
        }
    }

    private fun updateDocuments(data: EvaluateAwardData, award: Award): List<Document> {
        val requestDocumentsById: Map<DocumentId, EvaluateAwardData.Award.Document> = data.award.documents
            .associateBy { it.id }
        if (requestDocumentsById.isEmpty())
            return award.documents ?: emptyList()

        val awardDocumentsById: Map<DocumentId, Document> = award.documents
            ?.associateBy { it.id }
            ?: emptyMap()
        return if (awardDocumentsById.isEmpty())
            requestDocumentsById.values
                .map {
                    convertToDocument(it)
                }
        else {
            val documentIds: Set<DocumentId> = requestDocumentsById.keys + awardDocumentsById.keys
            documentIds.map { id ->
                requestDocumentsById[id]
                    ?.let { requestDocument ->
                        awardDocumentsById[id]
                            ?.copy(
                                title = requestDocument.title,
                                description = requestDocument.description,
                                documentType = requestDocument.documentType
                            )
                            ?: convertToDocument(requestDocument)
                    }
                    ?: awardDocumentsById.getValue(id)
            }
        }
    }

    private fun convertToDocument(document: EvaluateAwardData.Award.Document): Document = Document(
        id = document.id,
        documentType = document.documentType,
        title = document.title,
        description = document.description,
        relatedLots = document.relatedLots.map { it.toString() }
    )

    private fun getEvaluateAwardResult(updatedAward: Award) = EvaluateAwardResult(
        award = EvaluateAwardResult.Award(
            id = AwardId.fromString(updatedAward.id),
            internalId = updatedAward.internalId,
            date = updatedAward.date!!,
            description = updatedAward.description,
            status = updatedAward.status,
            statusDetails = updatedAward.statusDetails,
            relatedLots = updatedAward.relatedLots
                .map { LotId.fromString(it) },
            relatedBid = updatedAward.relatedBid?.let { BidId.fromString(it) },
            value = updatedAward.value!!,
            suppliers = updatedAward.suppliers!!
                .map { supplier ->
                    EvaluateAwardResult.Award.Supplier(
                        id = supplier.id,
                        name = supplier.name
                    )
                },
            documents = updatedAward.documents
                ?.map { document ->
                    EvaluateAwardResult.Award.Document(
                        id = document.id,
                        documentType = document.documentType,
                        title = document.title,
                        description = document.description,
                        relatedLots = document.relatedLots
                            ?.map { LotId.fromString(it) }
                            .orEmpty()
                    )
                }
                .orEmpty(),
            weightedValue = updatedAward.weightedValue?.asMoney
        )
    )

    /**
     * BR-7.6.3 "ID" (award) (check Award before CAN create)
     *
     * eEvaluation performs next steps:
     * 1. Selects all awards objects in DB by values of CPID && Stage from the context of Request && ID (lot.ID)
     *    from the context of comunda and saves them (awards) as a list to memory;
     * 2. Checks award.status && award.statusDetails in every award object from list selected before:
     *   a. IF [there is award where award.status == "pending" && award.statusDetails == "active"] then: eEvaluation performs next operations:
     *     i.   Finds award object where award.status == "pending" && award.statusDetails == "active" in list generated on step 1;
     *     ii.  Get.award.ID value from award object found before and returns it for Response;
     *     iii. Sets parameter awardingSucces == TRUE and returns it for Response;
     * ELSE IF [all awards in list have award.status == "pending" && award.statusDetails == "unsuccessful"] then:
     *   eEvaluation sets parameter awardingSucces == FALSE and returns it for Response;
     */
    override fun getWinning(context: GetWinningAwardContext): WinningAward? {
        val awardEntities: List<AwardEntity> = awardRepository.findBy(cpid = context.cpid, ocid = context.ocid)
            .orThrow { it.exception }

        if (awardEntities.isEmpty())
            throw ErrorException(DATA_NOT_FOUND)

        return getAwardsByLotId(lotId = context.lotId, entities = awardEntities)
            .apply {
                forEach { award -> checkValueAmount(award.value!!, context.ocid.stage) }
            }
            .findActiveAward()
            ?.let { award ->
                WinningAward(id = UUID.fromString(award.id))
            }
    }

    /**
     * VR-7.6.2
     *
     * 1. eEvaluation analyzes the quantity of Awards by lot:
     *   a. IF [there 1 or more award in list by lot]  then:
     *      validation is successful;
     *   b. ELSE [no awards in list] then:
     *      system throws Exception: "No awards for awarding finishing";
     */
    private fun getAwardsByLotId(lotId: UUID, entities: List<AwardEntity>): List<Award> {
        val awardsByLotId = entities.toSequenceOfAwards()
            .filterByLotId(lotId = lotId)
            .toList()
        if (awardsByLotId.isEmpty())
            throw ErrorException(error = AWARD_NOT_FOUND, message = "No awards for awarding finishing.")

        return awardsByLotId
    }

    /**
     * VR-7.6.3 status statusDetails (award)
     *
     * 1. eEvaluation checks award.status && award.statusDetails in every award object from list selected before:
     *   a. IF [there is award where award.status == "pending" && award.statusDetails == "active"] then:
     *      validation is successful;
     *   b. ELSE IF [all awards in list have award.status == "pending" && award.statusDetails == "unsuccessful"] then:
     *      validation is successful;
     *   c. ELSE then:
     *      system throws Exception: "Awarding by lot is not finished";
     */
    private fun List<Award>.findActiveAward(): Award? {
        val awardsByStatusDetails: Map<AwardStatusDetails, List<Award>> = this.groupBy { award ->
            if (award.status != AwardStatus.PENDING)
                throw ErrorException(error = INVALID_STATUS)
            award.statusDetails
        }

        val activeAwards: List<Award> = awardsByStatusDetails[AwardStatusDetails.ACTIVE] ?: emptyList()
        if (activeAwards.isNotEmpty()) {
            if (activeAwards.size > 1) {
                val idsBadAwards = activeAwards.joinToString(
                    separator = "', ",
                    prefix = "['",
                    postfix = "']",
                    transform = { award -> award.id }
                )
                throw ErrorException(
                    error = INVALID_STATUS_DETAILS,
                    message = "More than one award has an active status details[$idsBadAwards]"
                )
            }
            return activeAwards.first()
        }

        val unsuccessfulAwards: List<Award> = awardsByStatusDetails[AwardStatusDetails.UNSUCCESSFUL] ?: emptyList()
        if (unsuccessfulAwards.size != this.size)
            throw ErrorException(error = INVALID_STATUS_DETAILS)

        return null
    }

    /**
     * VR-7.6.4
     *
     * eEvaluation analyzes the availability of lot.value in lot object:
     * IF [award.value.amount are available] then: validation successful;
     * else [award.value.amount, aren’t available] then:  eEvaluation thrown Exception: "Award should have amount";
     */

    private fun checkValueAmount(value: Value, stage: Stage) {
        when (stage) {
            Stage.NP -> {
                if (value.amount == null)
                    throw ErrorException(error = ErrorType.AMOUNT, message = "Award should have amount. ")
            }
            Stage.AC,
            Stage.EI,
            Stage.EV,
            Stage.FE,
            Stage.FS,
            Stage.PC,
            Stage.PN,
            Stage.TP -> Unit
        }
    }

    /**
     * CR-7.1.1.1
     *
     * eEvaluation executes next operations:
     * 1. Finds all awards objects in DB by values of CPID && Stage from the context of Request && ID (lot.ID)
     *    from the context of comunda;
     * 2. Selects awards from list (got before) where award.statusDetails == "active" || "unsuccessful";
     * 3. Returns all awards (selected before) as awards array for response up to next data model:
     *   - award.statusDetails;
     *   - award.relatedBid;
     */
    override fun getEvaluated(context: GetEvaluatedAwardsContext): List<EvaluatedAward> =
        awardRepository.findBy(cpid = context.cpid, ocid = context.ocid)
            .orThrow { it.exception }
            .toSequenceOfAwards()
            .filterByLotId(lotId = context.lotId)
            .filter { award ->
                award.statusDetails == AwardStatusDetails.ACTIVE || award.statusDetails == AwardStatusDetails.UNSUCCESSFUL
            }
            .map { award ->
                EvaluatedAward(
                    statusDetails = award.statusDetails,
                    relatedBid = UUID.fromString(award.relatedBid!!)
                )
            }
            .toList()

    private fun List<AwardEntity>.toSequenceOfAwards(): Sequence<Award> = this.asSequence()
        .map { entity ->
            toObject(Award::class.java, entity.jsonData)
        }

    private fun Sequence<Award>.filterByLotId(lotId: UUID): Sequence<Award> {
        val lotIdAsString = lotId.toString()
        return this.filter { award ->
            award.relatedLots.contains(lotIdAsString)
        }
    }

    /**
     * BR-7.9.2 "status" "statusDetails" (Award) (set final status by lots)
     *
     * 1. Finds all Awards objects in DB by values of Stage && CPID from the context of Request and saves them as a list to memory;
     * 2. FOR every lot.ID value from list got in Request, eEvaluation executes next steps:
     *   a. Selects awards from list (got on step 1) where award.relatedLots == lots.[id] and saves them as a list to memory;
     *   b. Selects awards from list (got on step 2.a) with award.status == "pending" && award.statusDetails == "unsuccessful" and saves them as a list to memory;
     *   c. FOR every award from list got on step 2.b:
     *     i.   Sets award.status == "unsuccessful" && award.statusDetails ==  "empty";
     *     ii.  Saves updated Award to DB;
     *     iii. Returns it for Response as award.ID && award.status && award.statusDetails;
     *   d. Selects awards from list (got on step 2.a) with award.status == "pending" && award.statusDetails == "active" and saves them as a list to memory;
     *   e. FOR every award from list got on step 2.d:
     *     i.   Sets award.status == "active" && award.statusDetails ==  "empty";
     *     ii.  Saves updated Award to DB;
     *     iii. Returns it for Response as award.ID && award.status && award.statusDetails;
     */
    override fun finalAwardsStatusByLots(
        context: FinalAwardsStatusByLotsContext,
        data: FinalAwardsStatusByLotsData
    ): FinalizedAwardsStatusByLots {
        fun isActive(status: AwardStatus, details: AwardStatusDetails) =
            status == AwardStatus.PENDING && details == AwardStatusDetails.ACTIVE

        fun isUnsuccessful(status: AwardStatus, details: AwardStatusDetails) =
            status == AwardStatus.PENDING && details == AwardStatusDetails.UNSUCCESSFUL

        fun isValidStatuses(entity: AwardEntity): Boolean {
            val status = entity.status
            val details = entity.statusDetails
            return isActive(status, details) || isUnsuccessful(status, details)
        }

        fun Award.updatingStatuses(): Award = when {
            isActive(this.status, this.statusDetails) -> this.copy(
                status = AwardStatus.ACTIVE,
                statusDetails = AwardStatusDetails.EMPTY
            )
            isUnsuccessful(this.status, this.statusDetails) -> this.copy(
                status = AwardStatus.UNSUCCESSFUL,
                statusDetails = AwardStatusDetails.EMPTY
            )
            else -> throw IllegalStateException("No processing for award with status: '${this.status}' and details: '${this.statusDetails}'.")
        }

        val lotsIds: Set<UUID> = data.lots.asSequence()
            .map { it.id }
            .toSet()

        val updatedAwards = loadAwards(cpid = context.cpid)
            .filter { entity ->
                isValidStatuses(entity)
            }
            .map { entity ->
                val award = toObject(Award::class.java, entity.jsonData)
                award to entity
            }
            .filter { (award, _) ->
                award.relatedLots.any { lotsIds.contains(UUID.fromString(it)) }
            }
            .map { (award, entity) ->
                val updatedAward = award.updatingStatuses()

                val updatedEntity = entity.copy(
                    status = updatedAward.status,
                    statusDetails = updatedAward.statusDetails,
                    jsonData = toJson(updatedAward)
                )

                updatedAward to updatedEntity
            }
            .toMap()

        val wasApplied = awardRepository.update(cpid = context.cpid, updatedAwards = updatedAwards.values)
            .orThrow { fail ->
                throw SaveEntityException(message = "Error writing updated award to database.", cause = fail.exception)
            }

        if(!wasApplied)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the awards by cpid '${context.cpid}' to the database. Record(s) is not exists.")

        return FinalizedAwardsStatusByLots(
            awards = updatedAwards.keys.map { award ->
                FinalizedAwardsStatusByLots.Award(
                    id = UUID.fromString(award.id),
                    status = award.status,
                    statusDetails = award.statusDetails
                )
            }
        )
    }

    private fun loadAwards(cpid: Cpid): Sequence<AwardEntity> =
        awardRepository.findBy(cpid = cpid)
            .orThrow { it.exception }
            .takeIf { it.isNotEmpty() }
            ?.asSequence()
            ?: throw ErrorException(error = AWARD_NOT_FOUND)

    override fun create(context: CreateAwardsContext, data: CreateAwardsData): CreatedAwardsResult {
        val lotsIds: Set<String> = data.lots.toSetBy { it.id }
        val matchedBids = data.bids
            .asSequence()
            .filter { bid ->
                bid.relatedLots.any { relatedLot -> relatedLot in lotsIds }
            }
            .toList()

        val conversionsByRelatedItem = data.conversions
            .asSequence()
            .filter { conversion ->
                conversion.relatesTo == ConversionsRelatesTo.REQUIREMENT
            }
            .associateBy { conversion ->
                conversion.relatedItem
            }

        val createdAwards = matchedBids.map { bid ->
            val canCalculateWeightedValue = canCalculateWeightedValue(
                data.awardCriteria,
                data.awardCriteriaDetails,
                BidId.fromString(bid.id)
            )
            val weightedValue = if (canCalculateWeightedValue)
                calculateWeightedValue(bid, conversionsByRelatedItem)
            else
                null
            generateAward(bid, context, weightedValue)
        }
        val entities = createdAwards.map { award ->
            AwardEntity(
                cpid = context.cpid,
                ocid = context.ocid,
                token = UUID.fromString(award.token),
                statusDetails = award.statusDetails,
                status = award.status,
                owner = context.owner,
                jsonData = toJson(award)
            )
        }

        val result = CreatedAwardsResult(
            awards = createdAwards.map { award ->
                CreatedAwardsResult.Award(
                    token = Token.fromString(award.token!!),
                    id = AwardId.fromString(award.id)
                )
            }
        )

        awardRepository.save(context.cpid, entities)
            .orThrow { it.exception }

        return result
    }

    override fun createAwardsAuctionEnd(
        context: CreateAwardsAuctionEndContext,
        data: CreateAwardsAuctionEndData
    ): CreatedAwardsAuctionEndResult {
        val lotsIds: Set<LotId> = data.lots.toSetBy { it.id }
        val matchedBids = data.bids
            .asSequence()
            .filter { bid ->
                bid.relatedLots.any { relatedLot -> relatedLot in lotsIds }
            }
            .toList()

        val conversionsByRelatedItem = data.conversions
            .asSequence()
            .filter { conversion ->
                conversion.relatesTo == ConversionsRelatesTo.REQUIREMENT
            }
            .associateBy { conversion ->
                conversion.relatedItem
            }

        val electronicAuctionsByLots: Map<LotId, CreateAwardsAuctionEndData.ElectronicAuctions.Detail> =
            data.electronicAuctions.details
                .associateBy { it.relatedLot }

        val createdAwards = matchedBids.map { bid ->
            val canCalculateWeightedValue = canCalculateWeightedValue(
                data.awardCriteria,
                data.awardCriteriaDetails,
                bid.id
            )
            val awardValue = defineAwardValue(bid, electronicAuctionsByLots)
            val weightedValue = if (canCalculateWeightedValue) {
                val coefficientRates: List<CoefficientRate> = getCoefficients(bid, conversionsByRelatedItem)
                awardValue.calculateWeightedValue(coefficientRates)
            } else
                null
            generateAwardAuctionEnd(bid, context, weightedValue, awardValue)
        }
        val entities = createdAwards.map { award ->
            AwardEntity(
                cpid = context.cpid,
                ocid = context.ocid,
                token = UUID.fromString(award.token),
                statusDetails = award.statusDetails,
                status = award.status,
                owner = context.owner,
                jsonData = toJson(award)
            )
        }

        val result = CreatedAwardsAuctionEndResult(
            awards = createdAwards.map { award ->
                CreatedAwardsAuctionEndResult.Award(
                    token = Token.fromString(award.token!!),
                    id = AwardId.fromString(award.id)
                )
            }
        )

        awardRepository.save(context.cpid, entities)
            .orThrow { it.exception }

        return result
    }

    override fun setAwardForEvaluation(
        context: SetAwardForEvaluationContext,
        data: SetAwardForEvaluationData
    ): SetAwardForEvaluationResult {

        val awardEntityByAwardId: MutableMap<AwardId, AwardEntity> = mutableMapOf()
        val awards: MutableList<Award> = mutableListOf()
        awardRepository.findBy(cpid = context.cpid, ocid = context.ocid)
            .orThrow { it.exception }
            .forEach { entity ->
                val award: Award = toObject(Award::class.java, entity.jsonData)
                val prev = awardEntityByAwardId.put(LotId.fromString(award.id), entity)
                if (prev != null)
                    throw ErrorException(
                        error = ErrorType.INVALID_AWARD_ID,
                        message = "The duplicate of the award identifier '${award.id}' by cpid: '${context.cpid}' and ocid: '${context.ocid}'."
                    )

                awards.add(award)
            }

        val ratingAwards: List<Award> = groupingAwardsByLotId(awards = awards)
            .asSequence()
            .flatMap { (_, awards) ->
                val ratedAwards = awards.rating(
                    awardCriteria = data.awardCriteria,
                    awardCriteriaDetails = data.awardCriteriaDetails
                )
                selectAward(ratedAwards)
                    .asSequence()
            }
            .toList()

        val updatedAwardEntities = ratingAwards.asSequence()
            .filter { award ->
                award.status == AwardStatus.PENDING && award.statusDetails == AwardStatusDetails.AWAITING
            }
            .map { award ->
                val awardId = AwardId.fromString(award.id)
                val entity = awardEntityByAwardId.getValue(awardId)
                entity.copy(
                    status = award.status,
                    statusDetails = award.statusDetails,
                    jsonData = toJson(award)
                )
            }
            .toList()

        val result = SetAwardForEvaluationResult(
            awards = ratingAwards.asSequence()
                .filter { award ->
                    award.status == AwardStatus.PENDING &&
                        (award.statusDetails == AwardStatusDetails.EMPTY
                            || award.statusDetails == AwardStatusDetails.AWAITING)

                }
                .map { award ->
                    SetAwardForEvaluationResult.Award(
                        id = AwardId.fromString(award.id),
                        title = award.title,
                        date = award.date!!,
                        status = award.status,
                        statusDetails = award.statusDetails,
                        relatedLots = award.relatedLots
                            .map { LotId.fromString(it) },
                        relatedBid = award.relatedBid
                            ?.let { BidId.fromString(it) },
                        value = award.value?.asMoney,
                        suppliers = award.suppliers
                            ?.map { supplier ->
                                SetAwardForEvaluationResult.Award.Supplier(
                                    id = supplier.id,
                                    name = supplier.name
                                )
                            }
                            .orEmpty(),
                        weightedValue = award.weightedValue?.asMoney
                    )
                }
                .toList()
        )

        val wasApplied = awardRepository.update(cpid = context.cpid, updatedAwards = updatedAwardEntities)
            .orThrow { fail ->
                throw SaveEntityException(message = "Error writing updated award to database.", cause = fail.exception)
            }

        if(!wasApplied)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the awards by cpid '${context.cpid}' to the database. Record(s) is not exists.")

        return result
    }

    override fun startAwardPeriod(context: StartAwardPeriodContext): StartAwardPeriodResult {
        val wasApplied = awardPeriodRepository.saveStart(cpid = context.cpid, ocid = context.ocid, start = context.startDate)
            .orThrow { SaveEntityException(message = "Error writing start date of the award period.", cause = it.exception) }

        if (!wasApplied)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the start award period '${context.startDate}' by cpid '${context.cpid}' and ocid '${context.ocid}' to the database. Record is already.")

        return StartAwardPeriodResult(
            StartAwardPeriodResult.AwardPeriod(
                startDate = context.startDate
            )
        )
    }

    override fun createUnsuccessfulAwards(
        context: CreateUnsuccessfulAwardsContext,
        data: CreateUnsuccessfulAwardsData
    ): CreateUnsuccessfulAwardsResult {

        fun defineStatusDetails(operationType: OperationType): AwardStatusDetails {
            return when (operationType) {
                OperationType.TENDER_PERIOD_END_AUCTION,
                OperationType.TENDER_PERIOD_END_EV,
                OperationType.TENDER_UNSUCCESSFUL -> AwardStatusDetails.NO_OFFERS_RECEIVED

                OperationType.CANCEL_TENDER_EV -> AwardStatusDetails.LOT_CANCELLED

                OperationType.APPLY_QUALIFICATION_PROTOCOL -> AwardStatusDetails.LACK_OF_SUBMISSIONS
            }
        }

        val awardsByUnsuccessfulLots = data.lots
            .map { lot ->
                Award(
                    id = generationService.awardId().toString(),
                    title = "The contract/lot is not awarded",
                    description = "Other reasons (discontinuation of procedure)",
                    status = AwardStatus.UNSUCCESSFUL,
                    statusDetails = defineStatusDetails(context.operationType),
                    relatedLots = listOf(lot.id.toString()),
                    date = context.startDate,
                    token = generationService.token().toString(),
                    value = null,
                    items = null,
                    bidDate = null,
                    documents = null,
                    suppliers = null,
                    relatedBid = null,
                    weightedValue = null,
                    internalId = null
                )
            }

        val awardsEntities = awardsByUnsuccessfulLots.map { award ->
            AwardEntity(
                cpid = context.cpid,
                ocid = context.ocid,
                owner = context.owner,
                token = Token.fromString(award.token!!),
                status = award.status,
                statusDetails = award.statusDetails,
                jsonData = toJson(award)
            )
        }

        val response = CreateUnsuccessfulAwardsResult(
            awards = awardsByUnsuccessfulLots.map { award ->
                CreateUnsuccessfulAwardsResult.Award(
                    id = UUID.fromString(award.id),
                    token = Token.fromString(award.token!!),
                    title = award.title!!,
                    description = award.description!!,
                    status = award.status,
                    statusDetails = award.statusDetails,
                    date = award.date!!,
                    relatedLots = award.relatedLots.map { UUID.fromString(it) }
                )

            }
        )
        awardRepository.save(cpid = context.cpid, awards = awardsEntities)
            .orThrow { it.exception }

        return response
    }

    override fun checkStatus(context: CheckAwardStatusContext): CheckAwardStatusResult {
        val awardEntity = awardRepository.findBy(cpid = context.cpid, ocid = context.ocid, token = context.token)
            .orThrow { it.exception }
            ?: throw ErrorException(
                error = AWARD_NOT_FOUND,
                message = "Record of the award by cpid '${context.cpid}', ocid '${context.ocid}' and token '${context.token}' not found."
            )

        awardEntity.checkOwner(context.owner)

        val award = toObject(Award::class.java, awardEntity.jsonData)
        if (award.id != context.awardId.toString())
            throw ErrorException(
                error = AWARD_NOT_FOUND,
                message = "Award by id '${context.awardId} not found."
            )

        if (award.status != AwardStatus.PENDING)
            throw ErrorException(
                error = INVALID_STATUS,
                message = "Award has invalid status: '${award.status}'. Require status: '${AwardStatus.PENDING}'"
            )

        if (award.statusDetails != AwardStatusDetails.AWAITING)
            throw ErrorException(
                error = INVALID_STATUS_DETAILS,
                message = "Award has invalid status details: '${award.statusDetails}'. Require status details: '${AwardStatusDetails.AWAITING}'"
            )

        return CheckAwardStatusResult()
    }

    override fun startConsideration(context: StartConsiderationContext): StartConsiderationResult {
        val awardEntity = awardRepository.findBy(cpid = context.cpid, ocid = context.ocid, token = context.token)
            .orThrow { it.exception }
            ?.also { entity ->
                entity.checkOwner(context.owner)
            }
            ?: throw ErrorException(error = AWARD_NOT_FOUND)

        val award = toObject(Award::class.java, awardEntity.jsonData)
            .takeIf { award ->
                award.id == context.awardId.toString()
            }
            ?: throw ErrorException(error = AWARD_NOT_FOUND)

        //FReq-1.4.3.1
        val updatedAward = award.copy(
            statusDetails = AwardStatusDetails.CONSIDERATION
        )

        val updatedAwardEntity = awardEntity.copy(
            statusDetails = updatedAward.statusDetails,
            jsonData = toJson(updatedAward)
        )

        val result = StartConsiderationResult(
            award = StartConsiderationResult.Award(
                id = AwardId.fromString(updatedAward.id),
                statusDetails = updatedAward.statusDetails,
                relatedBid = BidId.fromString(updatedAward.relatedBid)
            )
        )

        val wasApplied = awardRepository.update(cpid = context.cpid, updatedAward = updatedAwardEntity)
            .orThrow { it.exception }

        if (!wasApplied)
            throw SaveEntityException(message = "An error occurred when writing a record(s) of the award by cpid '${context.cpid}' and ocid '${updatedAwardEntity.ocid}' and token to the database. Record is already.")

        return result
    }

    override fun getNext(context: GetNextAwardContext): GetNextAwardResult {
        val allAwardsToEntities: Map<Award, AwardEntity> =
            awardRepository.findBy(cpid = context.cpid, ocid = context.ocid)
                .orThrow { it.exception }
                .takeIf { it.isNotEmpty() }
                ?.asSequence()
                ?.map { entity ->
                    val award = toObject(Award::class.java, entity.jsonData)
                    award to entity
                }
                ?.toMap()
                ?: throw ErrorException(error = AWARD_NOT_FOUND)

        val award: Award = allAwardsToEntities.keys
            .find { AwardId.fromString(it.id) == context.awardId }
            ?: throw ErrorException(error = AWARD_NOT_FOUND)

        val relatedLots: Set<LotId> = award.relatedLots.toSetBy { LotId.fromString(it) }
        val awardsToEntities: Map<Award, AwardEntity> = allAwardsToEntities.filter { (award, _) ->
            relatedLots.containsAll(award.relatedLots.map { AwardId.fromString(it) })
        }

        val updatedAward: Award? = when (award.statusDetails) {
            AwardStatusDetails.UNSUCCESSFUL -> getAwardForUnsuccessfulStatusDetails(awards = awardsToEntities.keys)
            AwardStatusDetails.ACTIVE -> getAwardForActiveStatusDetails(context.ocid.stage, awards = awardsToEntities.keys)

            AwardStatusDetails.AWAITING,
            AwardStatusDetails.CONSIDERATION,
            AwardStatusDetails.EMPTY,
            AwardStatusDetails.LACK_OF_QUALIFICATIONS,
            AwardStatusDetails.LACK_OF_SUBMISSIONS,
            AwardStatusDetails.LOT_CANCELLED,
            AwardStatusDetails.NO_OFFERS_RECEIVED,
            AwardStatusDetails.PENDING -> throw ErrorException(
                error = INVALID_STATUS_DETAILS,
                message = "Invalid status details of award from request (${award.statusDetails.key})."
            )
        }

        return if (updatedAward != null) {
            val result = GetNextAwardResult(
                award = GetNextAwardResult.Award(
                    id = AwardId.fromString(updatedAward.id),
                    statusDetails = updatedAward.statusDetails,
                    relatedBid = AwardId.fromString(updatedAward.relatedBid!!)
                )
            )
            val awardEntitiesByAwardId: Map<AwardId, AwardEntity> = awardsToEntities.asSequence()
                .associateBy(keySelector = { AwardId.fromString(it.key.id) }, valueTransform = { it.value })
            val updatedAwardEntity = awardEntitiesByAwardId.getValue(AwardId.fromString(updatedAward.id))
                .copy(
                    status = updatedAward.status,
                    statusDetails = updatedAward.statusDetails,
                    jsonData = toJson(updatedAward)
                )
            val wasApplied = awardRepository.update(cpid = context.cpid, updatedAward = updatedAwardEntity)
                .orThrow { it.exception }

            if (!wasApplied)
                throw SaveEntityException(message = "An error occurred when writing a record(s) of the award by cpid '${context.cpid}' and ocid '${updatedAwardEntity.ocid}' and token to the database. Record is already.")

            result
        } else
            GetNextAwardResult(award = null)
    }

    /**
     * BR-7.5.8
     * When StandStill.endDate has come, eEvaluation analyzes the value of phase parameter from context of Request:
     * 1. IF { phase == "TENDERING" || "CLARIFICATION" || "NEGOTIATION" || "EMPTY" eEvaluation creates Awards by
     * cancelled Lots by rule BR-7.5.9 and adds them to Response;
     * 2. IF { phase != "TENDERING" || "CLARIFICATION" || "NEGOTIATION" || "EMPTY", eEvaluation throws Exception
     */
    override fun cancellation(context: AwardCancellationContext, data: AwardCancellationData): AwardCancellationResult {
        when (context.phase) {
            Phase.TENDERING,
            Phase.CLARIFICATION,
            Phase.NEGOTIATION,
            Phase.EMPTY -> {
                // BR-7.5.9
                val unsuccessfulAwards: List<Award> = generateUnsuccessfulAwards(lots = data.lots, context = context)
                val entities = unsuccessfulAwards.map { award ->
                    AwardEntity(
                        cpid = context.cpid,
                        token = Token.fromString(award.token!!),
                        owner = context.owner,
                        ocid = context.ocid,
                        status = award.status,
                        statusDetails = award.statusDetails,
                        jsonData = toJson(award)
                    )
                }
                val result = AwardCancellationResult(
                    awards = unsuccessfulAwards.map { award ->
                        AwardCancellationResult.Award(
                            id = AwardId.fromString(award.id),
                            title = award.title,
                            description = award.description,
                            date = award.date,
                            status = award.status,
                            statusDetails = award.statusDetails,
                            relatedLots = award.relatedLots
                                .map { LotId.fromString(it) }
                        )
                    }
                )
                awardRepository.save(cpid = context.cpid, awards = entities)
                    .orThrow { it.exception }

                return result
            }
            Phase.AWARDING -> {
                throw ErrorException(
                    error = ErrorType.INVALID_PHASE,
                    message = "Command 'awardsCancellation' can not be executed with phase ${context.phase} "
                )
            }
        }
    }

    override fun getAwardState(params: GetAwardStateByIdsParams): Result<List<GetAwardStateByIdsResult>, Failure> {
        val awardEntities = awardRepository.findBy(
            cpid = params.cpid,
            ocid = params.ocid
        ).onFailure { return it }

        val awardsIds = params.awardIds.toSetBy { it.toString() }

        val resultingAwards = awardEntities
            .mapResultPair { award -> award.jsonData.tryToObject(Award::class.java) }
            .mapFailure {
                Failure.Incident.Transform.ParseFromDatabaseIncident(
                    jsonData = it.element.jsonData,
                    exception = it.fail.exception
                )
            }
            .onFailure { return it }
            .filter { award -> testContains(award.id, awardsIds) }

        val resultingAwardIds = resultingAwards.toSetBy { it.id }
        val absentAwardsIds = awardsIds - resultingAwardIds

        if (absentAwardsIds.isNotEmpty())
            return failure(
                ValidationError.AwardNotFoundOnGetAwardState(
                    AwardId.fromString(absentAwardsIds.first())
                )
            )

        return resultingAwards.map { award ->
            GetAwardStateByIdsResult(
                id = AwardId.fromString(award.id),
                status = award.status,
                statusDetails = award.statusDetails
            )
        }.asSuccess()
    }

    override fun getAwardByIds(params: GetAwardByIdsParams): Result<GetAwardByIdsResult, Failure> {
        val receivedIds = params.awards.map { it.id }

        return awardRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .mapResult { it.jsonData.tryToObject(Award::class.java) }
            .onFailure { return it }
            .filter { it.id in receivedIds }
            .map { GetAwardByIdsResult.ResponseConverter.fromDomain(it) }
            .let { GetAwardByIdsResult(awards = it) }
            .asSuccess()
    }

    override fun findAwardsForProtocol(params: FindAwardsForProtocolParams): Result<FindAwardsForProtocolResult?, Failure> {
        val receivedRelatedLotIds = params.tender.lots.map { it.id }

        fun Award.isRelatedLotMatched() : Boolean = this.relatedLots.any { it in receivedRelatedLotIds }
        fun Award.isSuccessfullyAwarded() : Boolean = this.status == AwardStatus.PENDING && this.statusDetails == AwardStatusDetails.ACTIVE

        return awardRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .mapResult { it.jsonData.tryToObject(Award::class.java) }
            .onFailure { return it }
            .filter { it.isSuccessfullyAwarded() && it.isRelatedLotMatched() }
            .map { FindAwardsForProtocolResult.ResponseConverter.fromDomain(it) }
            .takeIf { it.isNotEmpty() }
            ?.let { awards -> FindAwardsForProtocolResult(awards = awards) }
            .asSuccess()
    }


    override fun checkAccessToAward(params: CheckAccessToAwardParams): Validated<Failure> {
        val awardEntities = awardRepository.findBy(cpid = params.cpid, ocid = params.ocid)
            .onFailure { return it.reason.asValidationError() }

        val awardEntityById = awardEntities
            .associate { entity ->
                val award = entity.jsonData
                    .tryToObject(Award::class.java)
                    .mapFailure {
                        Failure.Incident.Transform.ParseFromDatabaseIncident(entity.jsonData, it.exception)
                    }
                    .onFailure { return it.reason.asValidationError() }
                award.id to entity
            }

        val awardEntity = awardEntityById.get(params.awardId.toString())
            ?: return ValidationError.AwardNotFoundOnCheckAccess(params.awardId).asValidationError()


        if (awardEntity.owner != params.owner)
            return ValidationError.InvalidOwner().asValidationError()

        if (awardEntity.token != params.token)
            return ValidationError.InvalidToken().asValidationError()

        return Validated.ok()
    }

    override fun checkAwardState(params: CheckAwardStateParams): Validated<Failure> {

        val storedAwards = awardRepository.findBy(params.cpid, ocid = params.ocid)
            .onFailure {
                return Failure.Incident.Database.DatabaseInteractionIncident(it.reason.exception).asValidationError()
            }
            .mapResult { entity -> entity.jsonData.tryToObject(Award::class.java) }
            .onFailure { return it.reason.asValidationError() }

        val filteredAwards = CheckAwardsStateRules.filterAwards(storedAwards, params)
            .onFailure { return it.reason.asValidationError() }

        val validStates = rulesService.findValidStates(params.country, params.pmd, params.operationType)
            .onFailure { return it.reason.asValidationError() }

        filteredAwards.forEach { award ->
            val state = States.State(award.status, award.statusDetails)
            if (state !in validStates)
                return CheckAwardStateErrors.InvalidAwardState(award.id, state).asValidationError()
        }

        return Validated.ok()
    }

    override fun checkRelatedTenderer(params: CheckRelatedTendererParams): Validated<Failure> {
        val awardEntities = awardRepository.findBy(cpid = params.cpid, ocid = params.ocid)
            .onFailure { return it.reason.asValidationError() }
            .takeIf { it.isNotEmpty() }
            ?: return ValidationError.AwardNotFoundOnCheckRelatedTenderer(params.awardId).asValidationError()

        val award = awardEntities
            .mapResultPair { entity -> entity.jsonData.tryToObject(Award::class.java) }
            .mapFailure {
                Failure.Incident.Transform.ParseFromDatabaseIncident(
                    jsonData = it.element.jsonData,
                    exception = it.fail.exception
                )
            }
            .onFailure { return it.reason.asValidationError() }
            .firstOrNull { award -> award.id == params.awardId.toString() }
            ?: return ValidationError.AwardNotFoundOnCheckRelatedTenderer(params.awardId).asValidationError()

        if (award.suppliers == null || award.suppliers.isEmpty()) {
            return ValidationError.TendererNotLinkedToAwardOnCheckRelatedTenderer().asValidationError()
        }

        award.suppliers
            .firstOrNull { supplier -> supplier.id == params.relatedTendererId }
            ?: return ValidationError.TendererNotLinkedToAwardOnCheckRelatedTenderer().asValidationError()

        val previousRequirementResponseIsPresent = award.requirementResponses
            .any { requirementResponse ->
                requirementResponse.relatedTenderer.id == params.relatedTendererId &&
                    requirementResponse.requirement.id == params.requirementId &&
                    requirementResponse.responder.id == params.responderId
            }

        if (previousRequirementResponseIsPresent)
            return ValidationError.DuplicateRequirementResponseOnCheckRelatedTenderer().asValidationError()

        return Validated.ok()
    }

    override fun validateAwardData(params: ValidateAwardDataParams): Validated<Failure> {
        val receivedLot = params.tender.lots.first()
        val receivedAward = params.awards.first()
        val receivedSuppliers = receivedAward.suppliers.map { it.id }

        if (ValidateAwardDataRules.isNeedToCheckSuppliers(params.operationType)) {
            val storedAwards = awardRepository.findBy(params.cpid, params.ocid)
                .onFailure {
                    return Failure.Incident.Database.DatabaseInteractionIncident(it.reason.exception)
                        .asValidationError()
                }
                .map { it.jsonData.tryToObject(Award::class.java).onFailure { return it.reason.asValidationError() } }

            ValidateAwardDataRules.Supplier.checkAwardAlreadyExists(storedAwards, receivedLot.id, receivedSuppliers)
                .onFailure { return it }

        }

        params.awards.forEach { award ->

            ValidateAwardDataRules.Value.validate(award.value, receivedLot, params.operationType)
                .onFailure { return it }

            ValidateAwardDataRules.Supplier.validate(award)
                .onFailure { return it }

            ValidateAwardDataRules.Document.validate(award.documents)
                .onFailure { return it }
        }


        return Validated.ok()
    }

    override fun createAward(params: CreateAwardParams): Result<CreateAwardResult, Failure> {
        val receivedAward = params.awards.first()
        val receivedLot = params.tender.lots.first()

        val generatedToken = Token.randomUUID()
        val createdAward = Award(
            id = receivedAward.id,                 // FR.COM-4.9.1
            internalId = receivedAward.internalId, // FR.COM-4.9.2
            date = params.date,            // FR.COM-4.9.3
            status = AwardStatus.PENDING,  // FR.COM-4.9.4
            statusDetails = AwardStatusDetails.EMPTY,  // FR.COM-4.9.4
            description = receivedAward.description,   // FR.COM-4.9.5
            value = receivedAward.value.toDomain(),   // FR.COM-4.9.6
            suppliers = receivedAward.suppliers.map { it.toDomain() },   // FR.COM-4.9.7
            documents = receivedAward.documents.map { it.toDomain() },   // FR.COM-4.9.8
            relatedLots = listOf(receivedLot.id),  // FR.COM-4.9.8
            token = generatedToken.toString(),     // FR.COM-4.9.10
            title = null,
            weightedValue = null,
            relatedBid = null,
            bidDate = null,
            items = emptyList()
        )

        val json = transform.trySerialization(createdAward)
            .onFailure { return it }

        val awardEntity = AwardEntity(
            cpid = params.cpid,
            ocid = params.ocid,
            status = createdAward.status,
            statusDetails = createdAward.statusDetails,
            token = generatedToken,
            owner = params.owner,
            jsonData = json,
        )

        // FR.COM-4.9.11
        awardRepository.save(cpid = params.cpid, award = awardEntity)

        return CreateAwardResult.ResponseConverter
            .fromDomain(createdAward)
            .let { CreateAwardResult(generatedToken, listOf(it)) }
            .asSuccess()
    }

    override fun updateAward(params: UpdateAwardParams): Result<UpdateAwardResult, Failure> {
        val receivedAward = params.awards.first()

        val storedAwards = awardRepository.findBy(params.cpid, params.ocid)
            .onFailure { return it }
            .associateBy { it.jsonData.tryToObject(Award::class.java).onFailure { return it } }
            .filter { (award, _) -> award.id == receivedAward.id }

        val storedAward =
            if (storedAwards.isEmpty())
                return UpdateAwardErrors.AwardNotFound().asFailure()
            else
                storedAwards.keys.first()

        val awardEntity = storedAwards
            .mapKeys { it.key.id }
            .getValue(storedAward.id)

        val updatedAward = UpdateAwardRules.update(target = storedAward, source = receivedAward)

        val json = transform.trySerialization(updatedAward).onFailure { return it }
        val updatedAwardEntity = awardEntity.copy(jsonData = json)

        val wasApplied = awardRepository.update(params.cpid, updatedAwardEntity)
            .onFailure { return it }

        if (!wasApplied)
            return Failure.Incident.Database.DatabaseConsistencyIncident("Record not exists.").asFailure()

        return UpdateAwardResult.ResponseConverter.fromDomain(updatedAward)
            .let { UpdateAwardResult(listOf(it)) }
            .asSuccess()
    }

    override fun closeAwardPeriod(params: CloseAwardPeriodParams): Result<CloseAwardPeriodResult, Failure> =
        closeAwardPeriodStrategy.execute(params = params)

    override fun createUnsuccessfulAwards(params: CreateUnsuccessfulAwardsParams) =
        createUnsuccessfulAwardsStrategy.execute(params = params)

    override fun addRequirementResponse(params: AddRequirementResponseParams): Validated<Failure> {
        val awardEntities = awardRepository.findBy(cpid = params.cpid, ocid = params.ocid)
            .onFailure { return it.reason.asValidationError() }

        val awardEntityById = awardEntities
            .associate { entity ->
                val award = entity.jsonData
                    .tryToObject(Award::class.java)
                    .mapFailure {
                        Failure.Incident.Transform.ParseFromDatabaseIncident(entity.jsonData, it.exception)
                    }
                    .onFailure { return it.reason.asValidationError() }
                award.id to entity
            }

        val awardEntity = awardEntityById.get(params.award.id.toString())
            ?: return ValidationError.AwardNotFoundOnAddRequirementRs(params.award.id).asValidationError()

        val award = awardEntity.jsonData
            .tryToObject(Award::class.java)
            .mapFailure {
                Failure.Incident.Transform.ParseFromDatabaseIncident(
                    jsonData = awardEntity.jsonData,
                    exception = it.exception
                )
            }
            .onFailure { return it.reason.asValidationError() }

        val requirementResponse = convertToAwardRequirementResponse(params)

        val updatedAward = award.copy(
            requirementResponses = award.requirementResponses + requirementResponse
        )

        val updatedAwardEntity = awardEntity.copy(
            jsonData = toJson(updatedAward)
        )

        awardRepository.update(cpid = params.cpid, updatedAward = updatedAwardEntity)
            .onFailure { return it.reason.asValidationError() }
            .doOnFalse {
                return Failure.Incident.Database.DatabaseConsistencyIncident(
                        "An error occurred upon updating a record(s) of the awards by cpid '${updatedAwardEntity.cpid}'. Record(s) does not exist."
                    ).asValidationError()
            }

        return Validated.ok()
    }

    override fun startAwardPeriod(params: StartAwardPeriodParams): Result<StartAwardPeriodResultV2, Failure> {
        val wasApplied = awardPeriodRepository.saveStart(cpid = params.cpid, ocid = params.ocid, start = params.date)
            .onFailure {
                return Failure.Incident.Database.DatabaseInteractionIncident(it.reason.exception).asFailure()
            }

        if (!wasApplied) {
            return Failure.Incident.Database.DatabaseConsistencyIncident(
                "Cannot save start award period. Record by cpid '${params.cpid}' and ocid '${params.ocid}' already exists."
            ).asFailure()
        }

        val response = StartAwardPeriodResultV2(
            tender = StartAwardPeriodResultV2.Tender(
                awardPeriod = StartAwardPeriodResultV2.Tender.AwardPeriod(
                    startDate = params.date
                )
            )
        )

        return success(response)
    }

    private fun <T> testContains(value: T, patterns: Set<T>): Boolean =
        if (patterns.isNotEmpty()) value in patterns else true

    private fun convertToAwardRequirementResponse(params: AddRequirementResponseParams): RequirementResponse =
        params.award.requirementResponse.let { requirementRs ->
            RequirementResponse(
                id = requirementRs.id,
                responder = requirementRs.responder.let { responder ->
                    RequirementResponse.Responder(
                        id = responder.id,
                        name = responder.name
                    )
                },
                requirement = requirementRs.requirement.let { requirement ->
                    RequirementResponse.Requirement(
                        id = requirement.id
                    )
                },
                relatedTenderer = requirementRs.relatedTenderer.let { relatedTenderer ->
                    RequirementResponse.RelatedTenderer(
                        id = relatedTenderer.id
                    )
                },
                value = requirementRs.value
            )
        }

    private fun generateUnsuccessfulAwards(
        lots: List<AwardCancellationData.Lot>,
        context: AwardCancellationContext
    ): List<Award> = lots.map { lot ->
        Award(
            // BR-7.5.1
            id = generationService.awardId().toString(),
            // BR-7.5.2
            relatedLots = listOf(lot.id),
            // BR-7.5.3
            status = AwardStatus.UNSUCCESSFUL,
            statusDetails = AwardStatusDetails.EMPTY,
            // BR-7.5.4
            date = context.startDate,
            // BR-7.5.5
            title = "The contract/lot is not awarded",
            // BR-7.5.6
            description = "Other reasons (discontinuation of procedure)",
            token = generationService.token().toString(),
            value = null,
            relatedBid = null,
            bidDate = null,
            suppliers = null,
            documents = null,
            items = null,
            weightedValue = null,
            internalId = null
        )
    }

    private fun getAwardForUnsuccessfulStatusDetails(awards: Collection<Award>): Award? {
        val awardsByStatusDetails: Map<AwardStatusDetails, List<Award>> = awards.groupBy { it.statusDetails }
        val existsActive = awardsByStatusDetails.existsActive
        val existsConsideration = awardsByStatusDetails.existsConsideration
        val existsAwaiting = awardsByStatusDetails.existsAwaiting
        val existsEmpty = awardsByStatusDetails.existsEmpty

        if (existsActive || existsConsideration || existsAwaiting) return null
        if (!existsActive && !existsConsideration && !existsAwaiting && !existsEmpty) return null
        if (!existsActive && !existsConsideration && !existsAwaiting && existsEmpty)
            return ratingByValueOrWeightedValue(awards)
                .first { it.statusDetails == AwardStatusDetails.EMPTY }
                .copy(statusDetails = AwardStatusDetails.AWAITING)

        return null
    }

    private fun getAwardForActiveStatusDetails(stage: Stage, awards: Collection<Award>): Award? {
        when(stage){
            Stage.EV,
            Stage.TP -> {
                val awardsByStatusDetails: Map<AwardStatusDetails, List<Award>> = awards.groupBy { it.statusDetails }
                val existsConsideration = awardsByStatusDetails.existsConsideration
                val existsAwaiting = awardsByStatusDetails.existsAwaiting

                return if (existsConsideration || existsAwaiting) {
                    ratingByValueOrWeightedValue(awards)
                        .first { it.statusDetails == AwardStatusDetails.CONSIDERATION || it.statusDetails == AwardStatusDetails.AWAITING }
                        .copy(statusDetails = AwardStatusDetails.EMPTY)
                } else
                    null
            }
            Stage.PC -> {
                val awardsByStatusDetails: Map<AwardStatusDetails, List<Award>> = awards.groupBy { it.statusDetails }

                val existsConsideration = awardsByStatusDetails.existsConsideration
                val existsAwaiting = awardsByStatusDetails.existsAwaiting
                val existsEmpty = awardsByStatusDetails.existsEmpty

                if (existsConsideration || existsAwaiting || existsEmpty.not()) return null

                return ratingByValueOrWeightedValue(awards)
                    .first { it.statusDetails == AwardStatusDetails.EMPTY }
                    .copy(statusDetails = AwardStatusDetails.AWAITING)

            }
            Stage.PN,
            Stage.FS,
            Stage.FE,
            Stage.EI,
            Stage.AC,
            Stage.NP -> throw ErrorException(INVALID_STAGE)
        }
    }

    private val Map<AwardStatusDetails, List<Award>>.existsActive: Boolean
        get() = this[AwardStatusDetails.ACTIVE]?.isNotEmpty() ?: false

    private val Map<AwardStatusDetails, List<Award>>.existsConsideration: Boolean
        get() = this[AwardStatusDetails.CONSIDERATION]?.isNotEmpty() ?: false

    private val Map<AwardStatusDetails, List<Award>>.existsAwaiting: Boolean
        get() = this[AwardStatusDetails.AWAITING]?.isNotEmpty() ?: false

    private val Map<AwardStatusDetails, List<Award>>.existsEmpty: Boolean
        get() = this[AwardStatusDetails.EMPTY]?.isNotEmpty() ?: false

    private fun groupingAwardsByLotId(awards: List<Award>): Map<LotId, List<Award>> =
        mutableMapOf<LotId, MutableList<Award>>()
            .apply {
                awards.forEach { award ->
                    award.relatedLots.forEach { lotId ->
                        this.computeIfAbsent(LotId.fromString(lotId)) { mutableListOf() }
                            .apply { add(award) }
                    }
                }
            }

    private fun List<Award>.rating(
        awardCriteria: AwardCriteria,
        awardCriteriaDetails: AwardCriteriaDetails
    ): List<Award> {
        return when (awardCriteriaDetails) {
            AwardCriteriaDetails.AUTOMATED -> {
                when (awardCriteria) {
                    AwardCriteria.COST_ONLY,
                    AwardCriteria.QUALITY_ONLY,
                    AwardCriteria.RATED_CRITERIA -> {
                        //BR-1.4.1.4, BR-1.4.1.5
                        ratingByWeightedValue(awards = this)
                    }

                    AwardCriteria.PRICE_ONLY -> ratingByValue(awards = this)
                }
            }

            AwardCriteriaDetails.MANUAL -> {
                when (awardCriteria) {
                    AwardCriteria.COST_ONLY,
                    AwardCriteria.QUALITY_ONLY,
                    AwardCriteria.RATED_CRITERIA,
                    AwardCriteria.PRICE_ONLY -> {
                        //BR-1.4.1.6, BR-1.4.1.5
                        ratingByValue(awards = this)
                    }
                }
            }
        }
    }

    /**
     * BR-1.4.1.4
     * Если оценка будет производится с учетом неценовых критериев (awardCriteria), то наиболее выгодным предложением
     * (Bids) по лоту следует считать то, которое формирует наименьшее значение приведенной цены (weightedValue).
     *
     * BR-1.4.1.5
     * Если есть два и больше предложения (Bids) в рамках одного лота (Lots) с одинаковыми значениями ставки (Amount)
     * или приведенной цены предложения, больший приоритет при рассмотрении Заказчик должен отдать тому предложению,
     * последнее обновление (bidDate) которого произошло раньше, чем во всех остальных предложениях.
     */
    private fun ratingByWeightedValue(awards: List<Award>): List<Award> =
        awards.sortedWith(weightedValueComparator)

    /**
     * BR-1.4.1.6
     * Если оценка производится исключительно с учетом цены (awardCriteria) предложения (Bids), то наиболее выгодным
     * предложением по лоту следует считать то, которое обладает наименьшим значением ставки (Amount).
     *
     * BR-1.4.1.5
     * Если есть два и больше предложения (Bids) в рамках одного лота (Lots) с одинаковыми значениями ставки (Amount)
     * или приведенной цены предложения, больший приоритет при рассмотрении Заказчик должен отдать тому предложению,
     * последнее обновление (bidDate) которого произошло раньше, чем во всех остальных предложениях.
     */
    private fun ratingByValue(awards: List<Award>): List<Award> = awards.sortedWith(valueComparator)

    private fun ratingByValueOrWeightedValue(awards: Collection<Award>): List<Award> =
        awards.sortedWith(valueOrWeightedValueComparator)

    private fun selectAward(sortedAwards: List<Award>): List<Award> {
        fun Award.isSuitable(): Boolean =
            this.status == AwardStatus.PENDING && this.statusDetails == AwardStatusDetails.EMPTY

        fun Award.select(): Award = this.copy(statusDetails = AwardStatusDetails.AWAITING)

        var awardIsSelected = false
        return sortedAwards.map { award ->
            if (awardIsSelected)
                award
            else {
                if (award.isSuitable()) {
                    awardIsSelected = true
                    award.select()
                } else
                    award
            }
        }
    }

    private fun generateAward(bid: CreateAwardsData.Bid, context: CreateAwardsContext, weightedValue: Money?) =
        Award(
            id = generationService.awardId().toString(),
            status = AwardStatus.PENDING,
            statusDetails = AwardStatusDetails.EMPTY,
            relatedBid = bid.id,
            relatedLots = bid.relatedLots,
            value = bid.value.asValue,
            suppliers = bid.tenderers.map { tenderer ->
                OrganizationReference(
                    id = tenderer.id,
                    name = tenderer.name,
                    identifier = tenderer.identifier
                        .let { identifier ->
                            Identifier(
                                id = identifier.id,
                                uri = identifier.uri,
                                scheme = identifier.scheme,
                                legalName = identifier.legalName
                            )
                        },
                    additionalIdentifiers = tenderer.additionalIdentifiers
                        .map { additionalIdentifier ->
                            Identifier(
                                id = additionalIdentifier.id,
                                legalName = additionalIdentifier.legalName,
                                scheme = additionalIdentifier.scheme,
                                uri = additionalIdentifier.uri
                            )
                        }
                        .toMutableList(),
                    address = tenderer.address
                        .let { address ->
                            Address(
                                streetAddress = address.streetAddress,
                                postalCode = address.postalCode,
                                addressDetails = address.addressDetails
                                    .let { addressDetails ->
                                        AddressDetails(
                                            country = addressDetails.country
                                                .let { country ->
                                                    CountryDetails(
                                                        id = country.id,
                                                        uri = country.uri,
                                                        scheme = country.scheme,
                                                        description = country.description
                                                    )
                                                },
                                            locality = addressDetails.locality
                                                .let { locality ->
                                                    LocalityDetails(
                                                        id = locality.id,
                                                        description = locality.description,
                                                        scheme = locality.scheme,
                                                        uri = locality.uri
                                                    )
                                                },
                                            region = addressDetails.region
                                                .let { region ->
                                                    RegionDetails(
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
                    contactPoint = tenderer.contactPoint
                        .let { contactPoint ->
                            ContactPoint(
                                name = contactPoint.name,
                                email = contactPoint.email,
                                faxNumber = contactPoint.faxNumber,
                                telephone = contactPoint.telephone,
                                url = contactPoint.url
                            )
                        },
                    persones = tenderer.persones
                        .map { person ->
                            OrganizationReference.Person(
                                id = null,
                                identifier = person.identifier
                                    .let { identifier ->
                                        OrganizationReference.Person.Identifier(
                                            id = identifier.id,
                                            scheme = identifier.scheme,
                                            uri = identifier.uri
                                        )
                                    },
                                name = person.name,
                                title = person.title,
                                businessFunctions = person.businessFunctions
                                    .map { businessFunction ->
                                        OrganizationReference.Person.BusinessFunction(
                                            id = businessFunction.id,
                                            period = businessFunction.period
                                                .let { period ->
                                                    OrganizationReference.Person.BusinessFunction.Period(
                                                        startDate = period.startDate
                                                    )
                                                },
                                            documents = businessFunction.documents
                                                .map { document ->
                                                    OrganizationReference.Person.BusinessFunction.Document(
                                                        id = document.id,
                                                        title = document.title,
                                                        description = document.description,
                                                        documentType = document.documentType
                                                    )
                                                },
                                            jobTitle = businessFunction.jobTitle,
                                            type = businessFunction.type
                                        )
                                    }
                            )
                        },
                    details = tenderer.details
                        .let { details ->
                            Details(
                                typeOfSupplier = details.typeOfSupplier,
                                bankAccounts = details.bankAccounts
                                    .map { bankAccount ->
                                        Details.BankAccount(
                                            description = bankAccount.description,
                                            identifier = bankAccount.identifier
                                                .let { identifier ->
                                                    Details.BankAccount.Identifier(
                                                        id = identifier.id,
                                                        scheme = identifier.scheme
                                                    )
                                                },
                                            address = bankAccount.address
                                                .let { address ->
                                                    Details.BankAccount.Address(
                                                        streetAddress = address.streetAddress,
                                                        postalCode = address.postalCode,
                                                        addressDetails = address.addressDetails
                                                            .let { addressDetails ->
                                                                Details.BankAccount.Address.AddressDetails(
                                                                    country = addressDetails.country
                                                                        .let { country ->
                                                                            Details.BankAccount.Address.AddressDetails.Country(
                                                                                id = country.id,
                                                                                scheme = country.scheme,
                                                                                description = country.description,
                                                                                uri = country.uri
                                                                            )
                                                                        },
                                                                    region = addressDetails.region
                                                                        .let { region ->
                                                                            Details.BankAccount.Address.AddressDetails.Region(
                                                                                id = region.id,
                                                                                uri = region.uri,
                                                                                description = region.description,
                                                                                scheme = region.scheme
                                                                            )
                                                                        },
                                                                    locality = addressDetails.locality
                                                                        .let { locality ->
                                                                            Details.BankAccount.Address.AddressDetails.Locality(
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
                                            accountIdentification = bankAccount.accountIdentification
                                                .let { accountIdentification ->
                                                    Details.BankAccount.AccountIdentification(
                                                        id = accountIdentification.id,
                                                        scheme = accountIdentification.scheme
                                                    )
                                                },
                                            additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                                .map { additionalAccountIdentifier ->
                                                    Details.BankAccount.AdditionalAccountIdentifier(
                                                        id = additionalAccountIdentifier.id,
                                                        scheme = additionalAccountIdentifier.scheme
                                                    )
                                                },
                                            bankName = bankAccount.bankName
                                        )
                                    },
                                legalForm = details.legalForm
                                    ?.let { legalForm ->
                                        Details.LegalForm(
                                            id = legalForm.id,
                                            scheme = legalForm.scheme,
                                            uri = legalForm.uri,
                                            description = legalForm.description
                                        )
                                    },
                                mainEconomicActivities = details.mainEconomicActivities
                                    .map { mainEconomicActivity ->
                                        MainEconomicActivity(
                                            id = mainEconomicActivity.id,
                                            description = mainEconomicActivity.description,
                                            uri = mainEconomicActivity.uri,
                                            scheme = mainEconomicActivity.scheme
                                        )
                                    },
                                permits = details.permits
                                    .map { permit ->
                                        Details.Permit(
                                            id = permit.id,
                                            scheme = permit.scheme,
                                            url = permit.url,
                                            permitDetails = permit.permitDetails
                                                .let { permitDetail ->
                                                    Details.Permit.PermitDetails(
                                                        issuedBy = permitDetail.issuedBy
                                                            .let { issuedBy ->
                                                                Details.Permit.PermitDetails.IssuedBy(
                                                                    id = issuedBy.id,
                                                                    name = issuedBy.name
                                                                )
                                                            },
                                                        issuedThought = permitDetail.issuedThought
                                                            .let { issuedThought ->
                                                                Details.Permit.PermitDetails.IssuedThought(
                                                                    id = issuedThought.id,
                                                                    name = issuedThought.name
                                                                )
                                                            },
                                                        validityPeriod = permitDetail.validityPeriod
                                                            .let { validityPeriod ->
                                                                Details.Permit.PermitDetails.ValidityPeriod(
                                                                    startDate = validityPeriod.startDate,
                                                                    endDate = validityPeriod.endDate
                                                                )
                                                            }
                                                    )
                                                }
                                        )
                                    },
                                scale = details.scale
                            )
                        }
                )
            },
            date = context.startDate,
            bidDate = bid.date,
            weightedValue = weightedValue?.asValue,
            token = generationService.token().toString(),
            description = null,
            title = null,
            documents = null,
            items = null,
            internalId = null
        )

    private fun defineAwardValue(
        bid: CreateAwardsAuctionEndData.Bid,
        electronicAuctionsByLots: Map<LotId, CreateAwardsAuctionEndData.ElectronicAuctions.Detail>
    ): Money {
        val auctionResults = electronicAuctionsByLots[bid.relatedLots.first()]
        return if (auctionResults != null) {
            val bidFromAuction = auctionResults.electronicAuctionResult.find { it.relatedBid == bid.id }!!
            if (bidFromAuction.value.amount > bid.value.amount)
                bid.value
            else
                Money(bidFromAuction.value.amount, bid.value.currency)

        } else {
            bid.value
        }
    }

    private fun generateAwardAuctionEnd(
        bid: CreateAwardsAuctionEndData.Bid,
        context: CreateAwardsAuctionEndContext,
        weightedValue: Money?,
        awardValue: Money
    ) = Award(
        id = generationService.awardId().toString(),
        status = AwardStatus.PENDING,
        statusDetails = AwardStatusDetails.EMPTY,
        relatedBid = bid.id.toString(),
        relatedLots = bid.relatedLots
            .map { it.toString() },
        value = awardValue.asValue,
        suppliers = bid.tenderers.map { tenderer ->
            OrganizationReference(
                id = tenderer.id,
                name = tenderer.name,
                identifier = tenderer.identifier
                    .let { identifier ->
                        Identifier(
                            id = identifier.id,
                            uri = identifier.uri,
                            scheme = identifier.scheme,
                            legalName = identifier.legalName
                        )
                    },
                additionalIdentifiers = tenderer.additionalIdentifiers
                    .map { additionalIdentifier ->
                        Identifier(
                            id = additionalIdentifier.id,
                            legalName = additionalIdentifier.legalName,
                            scheme = additionalIdentifier.scheme,
                            uri = additionalIdentifier.uri
                        )
                    }
                    .toMutableList(),
                address = tenderer.address
                    .let { address ->
                        Address(
                            streetAddress = address.streetAddress,
                            postalCode = address.postalCode,
                            addressDetails = address.addressDetails
                                .let { addressDetails ->
                                    AddressDetails(
                                        country = addressDetails.country
                                            .let { country ->
                                                CountryDetails(
                                                    id = country.id,
                                                    uri = country.uri,
                                                    scheme = country.scheme,
                                                    description = country.description
                                                )
                                            },
                                        locality = addressDetails.locality
                                            .let { locality ->
                                                LocalityDetails(
                                                    id = locality.id,
                                                    description = locality.description,
                                                    scheme = locality.scheme,
                                                    uri = locality.uri
                                                )
                                            },
                                        region = addressDetails.region
                                            .let { region ->
                                                RegionDetails(
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
                contactPoint = tenderer.contactPoint
                    .let { contactPoint ->
                        ContactPoint(
                            name = contactPoint.name,
                            email = contactPoint.email,
                            faxNumber = contactPoint.faxNumber,
                            telephone = contactPoint.telephone,
                            url = contactPoint.url
                        )
                    },
                persones = tenderer.persones
                    .map { person ->
                        OrganizationReference.Person(
                            id = null,
                            identifier = person.identifier
                                .let { identifier ->
                                    OrganizationReference.Person.Identifier(
                                        id = identifier.id,
                                        scheme = identifier.scheme,
                                        uri = identifier.uri
                                    )
                                },
                            name = person.name,
                            title = person.title,
                            businessFunctions = person.businessFunctions
                                .map { businessFunction ->
                                    OrganizationReference.Person.BusinessFunction(
                                        id = businessFunction.id,
                                        period = businessFunction.period
                                            .let { period ->
                                                OrganizationReference.Person.BusinessFunction.Period(
                                                    startDate = period.startDate
                                                )
                                            },
                                        documents = businessFunction.documents
                                            .map { document ->
                                                OrganizationReference.Person.BusinessFunction.Document(
                                                    id = document.id,
                                                    title = document.title,
                                                    description = document.description,
                                                    documentType = document.documentType.key
                                                )
                                            },
                                        jobTitle = businessFunction.jobTitle,
                                        type = businessFunction.type
                                    )
                                }
                        )
                    },
                details = tenderer.details
                    .let { details ->
                        Details(
                            typeOfSupplier = details.typeOfSupplier,
                            bankAccounts = details.bankAccounts
                                .map { bankAccount ->
                                    Details.BankAccount(
                                        description = bankAccount.description,
                                        identifier = bankAccount.identifier
                                            .let { identifier ->
                                                Details.BankAccount.Identifier(
                                                    id = identifier.id,
                                                    scheme = identifier.scheme
                                                )
                                            },
                                        address = bankAccount.address
                                            .let { address ->
                                                Details.BankAccount.Address(
                                                    streetAddress = address.streetAddress,
                                                    postalCode = address.postalCode,
                                                    addressDetails = address.addressDetails
                                                        .let { addressDetails ->
                                                            Details.BankAccount.Address.AddressDetails(
                                                                country = addressDetails.country
                                                                    .let { country ->
                                                                        Details.BankAccount.Address.AddressDetails.Country(
                                                                            id = country.id,
                                                                            scheme = country.scheme,
                                                                            description = country.description,
                                                                            uri = country.uri
                                                                        )
                                                                    },
                                                                region = addressDetails.region
                                                                    .let { region ->
                                                                        Details.BankAccount.Address.AddressDetails.Region(
                                                                            id = region.id,
                                                                            uri = region.uri,
                                                                            description = region.description,
                                                                            scheme = region.scheme
                                                                        )
                                                                    },
                                                                locality = addressDetails.locality
                                                                    .let { locality ->
                                                                        Details.BankAccount.Address.AddressDetails.Locality(
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
                                        accountIdentification = bankAccount.accountIdentification
                                            .let { accountIdentification ->
                                                Details.BankAccount.AccountIdentification(
                                                    id = accountIdentification.id,
                                                    scheme = accountIdentification.scheme
                                                )
                                            },
                                        additionalAccountIdentifiers = bankAccount.additionalAccountIdentifiers
                                            .map { additionalAccountIdentifier ->
                                                Details.BankAccount.AdditionalAccountIdentifier(
                                                    id = additionalAccountIdentifier.id,
                                                    scheme = additionalAccountIdentifier.scheme
                                                )
                                            },
                                        bankName = bankAccount.bankName
                                    )
                                },
                            legalForm = details.legalForm
                                ?.let { legalForm ->
                                    Details.LegalForm(
                                        id = legalForm.id,
                                        scheme = legalForm.scheme,
                                        uri = legalForm.uri,
                                        description = legalForm.description
                                    )
                                },
                            mainEconomicActivities = details.mainEconomicActivities
                                .map { mainEconomicActivity ->
                                    MainEconomicActivity(
                                        id = mainEconomicActivity.id,
                                        description = mainEconomicActivity.description,
                                        uri = mainEconomicActivity.uri,
                                        scheme = mainEconomicActivity.scheme
                                    )
                                },
                            permits = details.permits
                                .map { permit ->
                                    Details.Permit(
                                        id = permit.id,
                                        scheme = permit.scheme,
                                        url = permit.url,
                                        permitDetails = permit.permitDetails
                                            .let { permitDetail ->
                                                Details.Permit.PermitDetails(
                                                    issuedBy = permitDetail.issuedBy
                                                        .let { issuedBy ->
                                                            Details.Permit.PermitDetails.IssuedBy(
                                                                id = issuedBy.id,
                                                                name = issuedBy.name
                                                            )
                                                        },
                                                    issuedThought = permitDetail.issuedThought
                                                        .let { issuedThought ->
                                                            Details.Permit.PermitDetails.IssuedThought(
                                                                id = issuedThought.id,
                                                                name = issuedThought.name
                                                            )
                                                        },
                                                    validityPeriod = permitDetail.validityPeriod
                                                        .let { validityPeriod ->
                                                            Details.Permit.PermitDetails.ValidityPeriod(
                                                                startDate = validityPeriod.startDate,
                                                                endDate = validityPeriod.endDate
                                                            )
                                                        }
                                                )
                                            }
                                    )
                                },
                            scale = details.scale.key
                        )
                    }
            )
        },
        date = context.startDate,
        bidDate = bid.date,
        weightedValue = weightedValue?.asValue,
        token = generationService.token().toString(),
        description = null,
        title = null,
        documents = null,
        items = null,
        internalId = null
    )

    private fun canCalculateWeightedValue(
        awardCriteria: AwardCriteria,
        awardCriteriaDetails: AwardCriteriaDetails,
        bidId: BidId
    ): Boolean =
        when (awardCriteriaDetails) {
            AwardCriteriaDetails.MANUAL -> {
                when (awardCriteria) {
                    AwardCriteria.PRICE_ONLY -> throw ErrorException(
                        INVALID_STATUS_DETAILS,
                        "Cannot calculate weighted value for award with award criteria: '${awardCriteria}' " +
                            "and award criteria details: '${awardCriteriaDetails}', based on bid '${bidId}'"
                    )
                    AwardCriteria.COST_ONLY,
                    AwardCriteria.QUALITY_ONLY,
                    AwardCriteria.RATED_CRITERIA -> true
                }
            }
            AwardCriteriaDetails.AUTOMATED -> {
                when (awardCriteria) {
                    AwardCriteria.PRICE_ONLY -> false

                    AwardCriteria.COST_ONLY,
                    AwardCriteria.QUALITY_ONLY,
                    AwardCriteria.RATED_CRITERIA -> true
                }
            }
        }

    private fun calculateWeightedValue(
        bid: CreateAwardsData.Bid,
        conversionsByRelatedItem: Map<String, CreateAwardsData.Conversion>
    ): Money {
        val coefficientRates = bid.requirementResponses
            .asSequence()
            .flatMap { response ->
                conversionsByRelatedItem[response.requirement.id]
                    ?.let { conversion ->
                        conversion.coefficients
                            .asSequence()
                            .filter { coefficient ->
                                compare(coefficient.value, response.value)
                            }
                            .map { coefficient ->
                                coefficient.coefficient
                            }
                    }
                    ?: emptySequence()
            }
            .toList()

        return if (coefficientRates.isNotEmpty()) {
            val amount = coefficientRates.fold(bid.value.amount, { acc, rate -> acc.multiply(rate.rate) })
                .setScale(Money.AVAILABLE_SCALE, RoundingMode.HALF_UP)
            Money(amount = amount, currency = bid.value.currency)
        } else
            bid.value
    }

    private fun Money.calculateWeightedValue(coefficientRates: List<CoefficientRate>): Money =
        if (coefficientRates.isNotEmpty()) {
            val amount = coefficientRates.fold(this.amount, { acc, rate -> acc.multiply(rate.rate) })
                .setScale(Money.AVAILABLE_SCALE, RoundingMode.HALF_UP)
            Money(amount = amount, currency = this.currency)
        } else
            this

    private fun getCoefficients(
        bid: CreateAwardsAuctionEndData.Bid,
        conversionsByRelatedItem: Map<String, CreateAwardsAuctionEndData.Conversion>
    ): List<CoefficientRate> = bid.requirementResponses
        .asSequence()
        .flatMap { response ->
            conversionsByRelatedItem[response.requirement.id.toString()]
                ?.let { conversion ->
                    conversion.coefficients
                        .asSequence()
                        .filter { coefficient ->
                            compare(coefficient.value, response.value)
                        }
                        .map { coefficient ->
                            coefficient.coefficient
                        }
                }
                ?: emptySequence()
        }
        .toList()

    private fun compare(coef: CoefficientValue, req: RequirementRsValue): Boolean {
        return when (req) {
            is RequirementRsValue.AsBoolean -> {
                if (coef is CoefficientValue.AsBoolean)
                    req.value == coef.value
                else
                    false
            }
            is RequirementRsValue.AsString -> {
                if (coef is CoefficientValue.AsString)
                    req.value == coef.value
                else
                    false
            }
            is RequirementRsValue.AsNumber -> {
                if (coef is CoefficientValue.AsNumber)
                    req.value == coef.value
                else
                    false
            }
            is RequirementRsValue.AsInteger -> {
                if (coef is CoefficientValue.AsInteger)
                    req.value == coef.value
                else
                    false
            }
        }
    }
}

private fun EvaluateAwardData.validateTextAttributes() {
    award.description.checkForBlank("award.description")
    award.documents.forEachIndexed { i, document ->
        document.title.checkForBlank("award.title.documents[$i]")
        document.description.checkForBlank("award.description.documents[$i]")
    }
}

private fun String?.checkForBlank(name: String) = this.errorIfBlank {
    ErrorException(
        error = ErrorType.INVALID_ATTRIBUTE,
        message = "The attribute '$name' is empty or blank."
    )
}

private val weightedValueComparator = Comparator<Award> { left, right ->
    val result = left.weightedValue!!.amount!!.compareTo(right.weightedValue!!.amount)
    if (result == 0) {
        left.bidDate!!.compareTo(right.bidDate)
    } else
        result
}

private val valueComparator = Comparator<Award> { left, right ->
    val result = left.value!!.amount!!.compareTo(right.value!!.amount)
    if (result == 0) {
        left.bidDate!!.compareTo(right.bidDate)
    } else
        result
}

private val valueOrWeightedValueComparator = Comparator<Award> { left, right ->
    val leftValue = left.weightedValue?.amount ?: left.value!!.amount
    val rightValue = right.weightedValue?.amount ?: right.value!!.amount

    val result = leftValue!!.compareTo(rightValue)
    if (result == 0) {
        left.bidDate!!.compareTo(right.bidDate)
    } else
        result
}
