package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.application.repository.AwardPeriodRepository
import com.procurement.evaluation.application.repository.AwardRepository
import com.procurement.evaluation.domain.model.ProcurementMethod
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.bid.BidId
import com.procurement.evaluation.domain.model.data.CoefficientValue
import com.procurement.evaluation.domain.model.data.RequirementRsValue
import com.procurement.evaluation.domain.model.document.DocumentId
import com.procurement.evaluation.domain.model.enums.OperationType
import com.procurement.evaluation.domain.model.lot.LotId
import com.procurement.evaluation.domain.model.money.Money
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.exception.ErrorType.ALREADY_HAVE_ACTIVE_AWARDS
import com.procurement.evaluation.exception.ErrorType.AWARD_NOT_FOUND
import com.procurement.evaluation.exception.ErrorType.DATA_NOT_FOUND
import com.procurement.evaluation.exception.ErrorType.INVALID_OWNER
import com.procurement.evaluation.exception.ErrorType.INVALID_STATUS
import com.procurement.evaluation.exception.ErrorType.INVALID_STATUS_DETAILS
import com.procurement.evaluation.exception.ErrorType.INVALID_TOKEN
import com.procurement.evaluation.exception.ErrorType.RELATED_LOTS
import com.procurement.evaluation.exception.ErrorType.STATUS_DETAILS_SAVED_AWARD
import com.procurement.evaluation.exception.ErrorType.SUPPLIER_IS_NOT_UNIQUE_IN_AWARD
import com.procurement.evaluation.exception.ErrorType.SUPPLIER_IS_NOT_UNIQUE_IN_LOT
import com.procurement.evaluation.exception.ErrorType.UNKNOWN_SCALE_SUPPLIER
import com.procurement.evaluation.exception.ErrorType.UNKNOWN_SCHEME_IDENTIFIER
import com.procurement.evaluation.exception.ErrorType.WRONG_NUMBER_OF_SUPPLIERS
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
import com.procurement.evaluation.model.dto.ocds.OrganizationReference
import com.procurement.evaluation.model.dto.ocds.RegionDetails
import com.procurement.evaluation.model.dto.ocds.Value
import com.procurement.evaluation.model.dto.ocds.asMoney
import com.procurement.evaluation.model.dto.ocds.asValue
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.service.GenerationService
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toObject
import org.springframework.stereotype.Service
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*

interface AwardService {
    fun create(context: CreateAwardContext, data: CreateAwardData): CreatedAwardData

    fun create(context: CreateAwardsContext, data: CreateAwardsData): CreatedAwardsResult

    fun evaluate(context: EvaluateAwardContext, data: EvaluateAwardData): EvaluateAwardResult

    fun getWinning(context: GetWinningAwardContext): WinningAward?

    fun getEvaluated(context: GetEvaluatedAwardsContext): List<EvaluatedAward>

    fun finalAwardsStatusByLots(
        context: FinalAwardsStatusByLotsContext,
        data: FinalAwardsStatusByLotsData
    ): FinalizedAwardsStatusByLots

    fun completeAwarding(context: CompleteAwardingContext): CompletedAwarding

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
}

@Service
class AwardServiceImpl(
    private val generationService: GenerationService,
    private val awardRepository: AwardRepository,
    private val awardPeriodRepository: AwardPeriodRepository
) : AwardService {

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
        val stage = context.stage

        //VR-7.10.9
        checkNumberSuppliers(suppliers = data.award.suppliers)

        // VR-7.10.6
        checkSchemeOfIdentifier(data)

        // VR-7.10.7
        checkScaleOfSupplier(data)

        //VR-7.10.8(1)
        checkSuppliersIdentifiers(suppliers = data.award.suppliers)

        val awardsEntities = awardRepository.findBy(cpid = cpid)
        val awards = awardsEntities.map {
            toObject(Award::class.java, it.jsonData)
        }

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
            weightedValue = null
        )

        val prevAwardPeriodStart = awardPeriodRepository.findStartDateBy(cpid = cpid, stage = stage)

        val newAwardEntity = AwardEntity(
            cpId = cpid,
            stage = stage,
            status = award.status.toString(),
            statusDetails = award.statusDetails.toString(),
            token = UUID.fromString(award.token),
            owner = context.owner,
            jsonData = toJson(award)
        )

        //FIXME Consistency cannot be guaranteed
        val awardPeriodStart = if (prevAwardPeriodStart != null) {
            prevAwardPeriodStart
        } else {
            val newAwardPeriodStart = context.startDate
            awardPeriodRepository.saveNewStart(cpid = cpid, stage = stage, start = newAwardPeriodStart)
            newAwardPeriodStart
        }

        awardRepository.saveNew(cpid = cpid, award = newAwardEntity)

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
        val schemes = data.mdm.schemes
            .asSequence()
            .map { it.toUpperCase() }
            .toSet()

        val invalidScheme = data.award.suppliers.any { supplier ->
            supplier.identifier.scheme.toUpperCase() !in schemes
        }

        if (invalidScheme)
            throw ErrorException(error = UNKNOWN_SCHEME_IDENTIFIER)
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
        token = award.token!!,
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
        val cpid = context.cpid

        val awardEntity = awardRepository.findBy(cpid = cpid, stage = context.stage, token = context.token)
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
            statusDetails = updatedAward.statusDetails.toString(),
            jsonData = toJson(updatedAward)
        )

        awardRepository.update(cpid = cpid, updatedAward = updatedAwardEntity)

        return getEvaluateAwardResult(updatedAward = updatedAward)
    }

    private fun checkStatusDetails(
        context: EvaluateAwardContext,
        data: EvaluateAwardData,
        award: Award
    ) {
        when (data.award.statusDetails) {
            AwardStatusDetails.UNSUCCESSFUL -> {
                checkStatusDetailsForStage(stage = context.stage, statusDetails = award.statusDetails)
            }
            AwardStatusDetails.ACTIVE -> {
                checkStatusDetailsForStage(stage = context.stage, statusDetails = award.statusDetails)
                checkRelatedAwards(context = context, award = award)
            }

            AwardStatusDetails.PENDING,
            AwardStatusDetails.CONSIDERATION,
            AwardStatusDetails.EMPTY,
            AwardStatusDetails.AWAITING,
            AwardStatusDetails.NO_OFFERS_RECEIVED,
            AwardStatusDetails.LOT_CANCELLED -> throw ErrorException(
                error = INVALID_STATUS_DETAILS,
                message = "Invalid status details of award from request (${data.award.statusDetails.value})."
            )
        }
    }

    private fun checkStatusDetailsForStage(stage: String, statusDetails: AwardStatusDetails) {
        when (stage) {
            "EV" -> {
                when (statusDetails) {
                    AwardStatusDetails.UNSUCCESSFUL,
                    AwardStatusDetails.ACTIVE,
                    AwardStatusDetails.CONSIDERATION -> Unit

                    AwardStatusDetails.PENDING,
                    AwardStatusDetails.EMPTY,
                    AwardStatusDetails.AWAITING,
                    AwardStatusDetails.NO_OFFERS_RECEIVED,
                    AwardStatusDetails.LOT_CANCELLED -> throw ErrorException(
                        error = INVALID_STATUS_DETAILS,
                        message = "Invalid status details of award from database (${statusDetails.value}) by stage 'EV'."
                    )
                }
            }
            "NP" -> {
                when (statusDetails) {
                    AwardStatusDetails.UNSUCCESSFUL,
                    AwardStatusDetails.ACTIVE,
                    AwardStatusDetails.EMPTY -> Unit

                    AwardStatusDetails.CONSIDERATION,
                    AwardStatusDetails.PENDING,
                    AwardStatusDetails.AWAITING,
                    AwardStatusDetails.NO_OFFERS_RECEIVED,
                    AwardStatusDetails.LOT_CANCELLED -> throw ErrorException(
                        error = INVALID_STATUS_DETAILS,
                        message = "Invalid status details of award from database (${statusDetails.value}) by stage 'NP'."
                    )
                }
            }
            else -> throw ErrorException(error = ErrorType.INVALID_STAGE)
        }
    }

    private fun checkRelatedAwards(context: EvaluateAwardContext, award: Award) {
        val lots = award.relatedLots.toSet()
        val relatedAwards = awardRepository.findBy(cpid = context.cpid, stage = context.stage)
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
        relatedLots = document.relatedLots.asSequence().map { it.toString() }.toHashSet()
    )

    /**
     * BR-7.10.6 "statusDetails" (awards)
     *
     * eEvaluation checks the value of award.statusDetails from Request:
     * 1. IF award gets statusDetails == "active" in request, eEvaluation checks the value of statusDetails field in saved version of award:
     *   a. IF statusDetails of award's saved version == "empty", eEvaluation performs next steps:
     *        Saves new award.statusDetails == "active";
     *   b. ELSE IF statusDetails of award's saved version == "active"
     *        eEvaluation does not perform any operation with award.statusDetails;
     *   c. ELSE IF statusDetails of award's saved version == "unsuccessful" eEvaluation performs next steps:
     *        Saves new award.statusDetails ==  "active";
     *   d. ELSE IF statusDetails of award's saved version != "unsuccessful" || "empty" || "active"
     *        eEvaluation returns exception.
     * 2. ELSE (award gets statusDetails == "unsuccessful" in Request), eEvaluation checks the value of statusDetails field in saved version of award:
     *   a. IF statusDetails of award's saved version == "empty", eEvaluation performs next steps:
     *        Saves new award.statusDetails ==  "unsuccessful";
     *   b. ELSE IF statusDetails of award's saved version == "unsuccessful"
     *        eEvaluation does not perform any operation with award.statusDetails;
     *   c. ELSE IF statusDetails of award's saved version == "active", eEvaluation performs next steps:
     *        Saves new award.statusDetails ==  "unsuccessful";
     *   d. ELSE IF statusDetails of award's saved version != "active" || "empty" || "unsuccessful"
     *        eEvaluation returns exception.
     */
    private fun statusDetails(data: EvaluateAwardData, award: Award): AwardStatusDetails {
        return when (data.award.statusDetails) {
            AwardStatusDetails.ACTIVE -> {
                when (award.statusDetails) {
                    AwardStatusDetails.EMPTY -> AwardStatusDetails.ACTIVE
                    AwardStatusDetails.ACTIVE -> AwardStatusDetails.ACTIVE
                    AwardStatusDetails.UNSUCCESSFUL -> AwardStatusDetails.ACTIVE
                    else -> throw ErrorException(error = STATUS_DETAILS_SAVED_AWARD)
                }
            }

            AwardStatusDetails.UNSUCCESSFUL -> {
                when (award.statusDetails) {
                    AwardStatusDetails.EMPTY -> AwardStatusDetails.UNSUCCESSFUL
                    AwardStatusDetails.UNSUCCESSFUL -> AwardStatusDetails.UNSUCCESSFUL
                    AwardStatusDetails.ACTIVE -> AwardStatusDetails.UNSUCCESSFUL
                    else -> throw ErrorException(error = STATUS_DETAILS_SAVED_AWARD)
                }
            }

            else -> throw ErrorException(error = INVALID_STATUS_DETAILS)
        }
    }

    private fun getEvaluateAwardResult(updatedAward: Award) = EvaluateAwardResult(
        award = EvaluateAwardResult.Award(
            id = AwardId.fromString(updatedAward.id),
            date = updatedAward.date!!,
            description = updatedAward.description,
            status = updatedAward.status,
            statusDetails = updatedAward.statusDetails,
            relatedLots = updatedAward.relatedLots
                .map { LotId.fromString(it) },
            relatedBid = updatedAward.relatedBid?.let { BidId.fromString(it) },
            value = updatedAward.value!!.asMoney,
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
                .orEmpty()
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
        val awardEntities: List<AwardEntity> = awardRepository.findBy(cpid = context.cpid, stage = context.stage)
        if (awardEntities.isEmpty())
            throw ErrorException(DATA_NOT_FOUND)

        return getAwardsByLotId(lotId = context.lotId, entities = awardEntities)
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
        awardRepository.findBy(cpid = context.cpid, stage = context.stage)
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
            val status = AwardStatus.fromString(entity.status)
            val details = AwardStatusDetails.fromString(entity.statusDetails)
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

        val stage = getStage(context)
        val updatedAwards = loadAwards(cpid = context.cpid, stage = stage)
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
                    status = updatedAward.status.value,
                    statusDetails = updatedAward.statusDetails.value,
                    jsonData = toJson(updatedAward)
                )

                updatedAward to updatedEntity
            }
            .toMap()

        awardRepository.update(cpid = context.cpid, updatedAwards = updatedAwards.values)

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

    private fun getStage(context: FinalAwardsStatusByLotsContext): String = when (context.pmd) {
        ProcurementMethod.OT, ProcurementMethod.TEST_OT,
        ProcurementMethod.SV, ProcurementMethod.TEST_SV,
        ProcurementMethod.MV, ProcurementMethod.TEST_MV -> "EV"

        ProcurementMethod.DA, ProcurementMethod.TEST_DA,
        ProcurementMethod.NP, ProcurementMethod.TEST_NP,
        ProcurementMethod.OP, ProcurementMethod.TEST_OP -> "NP"

        ProcurementMethod.RT, ProcurementMethod.TEST_RT,
        ProcurementMethod.FA, ProcurementMethod.TEST_FA -> throw ErrorException(ErrorType.INVALID_PMD)
    }

    private fun loadAwards(cpid: String, stage: String): Sequence<AwardEntity> =
        awardRepository.findBy(cpid = cpid, stage = stage)
            .takeIf {
                it.isNotEmpty()
            }
            ?.asSequence()
            ?: throw ErrorException(error = AWARD_NOT_FOUND)

    /**
     * BR-7.4.7 awardPeriod (awardPeriod.endDate)
     * 1. Sets value of Stage parameter == EV and saves it to memory;
     * 2. Finds awardPeriod object in DB by values of CPID from the context of Request and Stage set before;
     * 3. Sets awardPeriod.endDate == startDate value from the context of Request and adds it for Response;
     */
    override fun completeAwarding(context: CompleteAwardingContext): CompletedAwarding {
        val endDate = context.startDate
        awardPeriodRepository.saveEnd(cpid = context.cpid, stage = "EV", end = endDate)
        return CompletedAwarding(awardPeriod = CompletedAwarding.AwardPeriod(endDate = endDate))
    }

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
            val canCalculateWeightedValue = data.canCalculateWeightedValue(bid)
            val weightedValue = if (canCalculateWeightedValue)
                calculateWeightedValue(bid, conversionsByRelatedItem)
            else
                null
            generateAward(bid, context, weightedValue)
        }
        val entities = createdAwards.map { award ->
            AwardEntity(
                cpId = context.cpid,
                stage = context.stage,
                token = UUID.fromString(award.token),
                statusDetails = award.statusDetails.value,
                status = award.status.value,
                owner = context.owner,
                jsonData = toJson(award)
            )
        }
        awardRepository.saveNew(context.cpid, entities)
        return CreatedAwardsResult()
    }

    override fun setAwardForEvaluation(
        context: SetAwardForEvaluationContext,
        data: SetAwardForEvaluationData
    ): SetAwardForEvaluationResult {

        val awardEntityByAwardId: MutableMap<AwardId, AwardEntity> = mutableMapOf()
        val awards: MutableList<Award> = mutableListOf()
        awardRepository.findBy(cpid = context.cpid, stage = context.stage)
            .forEach { entity ->
                val award: Award = toObject(Award::class.java, entity.jsonData)
                val prev = awardEntityByAwardId.put(LotId.fromString(award.id), entity)
                if (prev != null)
                    throw ErrorException(
                        error = ErrorType.INVALID_AWARD_ID,
                        message = "The duplicate of the award identifier '${award.id}' by cpid: '${context.cpid}' and stage: '${context.stage}'."
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
                    status = award.status.value,
                    statusDetails = award.statusDetails.value,
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
                        token = Token.fromString(award.token!!),
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

        awardRepository.update(cpid = context.cpid, updatedAwards = updatedAwardEntities)
        return result
    }

    override fun startAwardPeriod(context: StartAwardPeriodContext): StartAwardPeriodResult {
        awardPeriodRepository.saveNewStart(cpid = context.cpid, stage = context.stage, start = context.startDate)
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
                OperationType.TENDER_UNSUCCESSFUL,
                OperationType.TENDER_PERIOD_END_EV,
                OperationType.TENDER_PERIOD_END_AUCTION -> AwardStatusDetails.NO_OFFERS_RECEIVED
                OperationType.CANCEL_TENDER_EV -> AwardStatusDetails.LOT_CANCELLED
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
                    weightedValue = null
                )
            }

        val awardsEntities = awardsByUnsuccessfulLots.map { award ->
            AwardEntity(
                cpId = context.cpid,
                stage = context.stage,
                owner = context.owner,
                token = Token.fromString(award.token!!),
                status = award.status.value,
                statusDetails = award.statusDetails.value,
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
        awardRepository.saveNew(cpid = context.cpid, awards = awardsEntities)
        return response
    }

    override fun checkStatus(context: CheckAwardStatusContext): CheckAwardStatusResult {
        val awardEntity = awardRepository.findBy(cpid = context.cpid, stage = context.stage, token = context.token)
            ?: throw ErrorException(
                error = AWARD_NOT_FOUND,
                message = "Record of the award by cpid '${context.cpid}', stage '${context.stage}' and token '${context.token}' not found."
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
        val awardEntity = awardRepository.findBy(cpid = context.cpid, stage = context.stage, token = context.token)
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
            statusDetails = updatedAward.statusDetails.value,
            jsonData = toJson(updatedAward)
        )

        val result = StartConsiderationResult(
            award = StartConsiderationResult.Award(
                id = AwardId.fromString(updatedAward.id),
                statusDetails = updatedAward.statusDetails,
                relatedLots = updatedAward.relatedLots.map { LotId.fromString(it) }
            )
        )

        awardRepository.update(cpid = context.cpid, updatedAward = updatedAwardEntity)

        return result
    }

    override fun getNext(context: GetNextAwardContext): GetNextAwardResult {
        val allAwardsToEntities: Map<Award, AwardEntity> =
            awardRepository.findBy(cpid = context.cpid, stage = context.stage)
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
            AwardStatusDetails.ACTIVE -> getAwardForActiveStatusDetails(awards = awardsToEntities.keys)

            AwardStatusDetails.PENDING,
            AwardStatusDetails.CONSIDERATION,
            AwardStatusDetails.EMPTY,
            AwardStatusDetails.AWAITING,
            AwardStatusDetails.NO_OFFERS_RECEIVED,
            AwardStatusDetails.LOT_CANCELLED -> throw ErrorException(
                error = INVALID_STATUS_DETAILS,
                message = "Invalid status details of award from request (${award.statusDetails.value})."
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
                .let {
                    it.copy(
                        status = updatedAward.status.value,
                        statusDetails = updatedAward.statusDetails.value,
                        jsonData = toJson(updatedAward)
                    )
                }
            awardRepository.update(cpid = context.cpid, updatedAward = updatedAwardEntity)

            result
        } else
            GetNextAwardResult(award = null)
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

    private fun getAwardForActiveStatusDetails(awards: Collection<Award>): Award? {
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
                                mainEconomicActivities = details.mainEconomicActivities,
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
                                                                    startDate = validityPeriod.startDate.toString(),
                                                                    endDate = validityPeriod.endDate.toString()
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
            items = null
        )

    private fun CreateAwardsData.canCalculateWeightedValue(
        bid: CreateAwardsData.Bid
    ): Boolean =
        when (this.awardCriteriaDetails) {
            AwardCriteriaDetails.MANUAL -> {
                when (this.awardCriteria) {
                    AwardCriteria.PRICE_ONLY -> throw ErrorException(
                        ErrorType.INVALID_STATUS_DETAILS,
                        "Cannot calculate weighted value for award with award criteria: '${this.awardCriteria}' " +
                            "and award criteria details: '${this.awardCriteriaDetails}', based on bid '${bid.id}'"
                    )
                    AwardCriteria.COST_ONLY,
                    AwardCriteria.QUALITY_ONLY,
                    AwardCriteria.RATED_CRITERIA -> false
                }
            }
            AwardCriteriaDetails.AUTOMATED -> {
                when (this.awardCriteria) {
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

private val weightedValueComparator = Comparator<Award> { left, right ->
    val result = left.weightedValue!!.amount.compareTo(right.weightedValue!!.amount)
    if (result == 0) {
        left.bidDate!!.compareTo(right.bidDate)
    } else
        result
}

private val valueComparator = Comparator<Award> { left, right ->
    val result = left.value!!.amount.compareTo(right.value!!.amount)
    if (result == 0) {
        left.bidDate!!.compareTo(right.bidDate)
    } else
        result
}

private val valueOrWeightedValueComparator = Comparator<Award> { left, right ->
    val leftValue = left.weightedValue?.amount ?: left.value!!.amount
    val rightValue = right.weightedValue?.amount ?: right.value!!.amount

    val result = leftValue.compareTo(rightValue)
    if (result == 0) {
        left.bidDate!!.compareTo(right.bidDate)
    } else
        result
}
