package com.procurement.evaluation.application.service.award

import com.procurement.evaluation.application.repository.AwardPeriodRepository
import com.procurement.evaluation.application.repository.AwardRepository
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType.SUPPLIER_IS_NOT_UNIQUE_IN_AWARD
import com.procurement.evaluation.exception.ErrorType.SUPPLIER_IS_NOT_UNIQUE_IN_LOT
import com.procurement.evaluation.exception.ErrorType.UNKNOWN_SCALE_SUPPLIER
import com.procurement.evaluation.exception.ErrorType.UNKNOWN_SCHEME_IDENTIFIER
import com.procurement.evaluation.model.dto.ocds.Address
import com.procurement.evaluation.model.dto.ocds.AddressDetails
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import com.procurement.evaluation.model.dto.ocds.ContactPoint
import com.procurement.evaluation.model.dto.ocds.CountryDetails
import com.procurement.evaluation.model.dto.ocds.Details
import com.procurement.evaluation.model.dto.ocds.Identifier
import com.procurement.evaluation.model.dto.ocds.LocalityDetails
import com.procurement.evaluation.model.dto.ocds.OrganizationReference
import com.procurement.evaluation.model.dto.ocds.RegionDetails
import com.procurement.evaluation.model.dto.ocds.Value
import com.procurement.evaluation.model.entity.AwardEntity
import com.procurement.evaluation.service.GenerationService
import com.procurement.evaluation.utils.toJson
import com.procurement.evaluation.utils.toObject
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

interface AwardService {
    fun create(context: CreateAwardContext, data: CreateAwardData): CreatedAwardData
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
                                    locality = detail.region.let { locality ->
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
                    details = Details(scale = supplier.details.scale)
                )
            },
            documents = null,
            items = null
        )

        val awardPeriodStart = awardPeriodRepository.findStartDateBy(cpid = cpid, stage = stage)
            ?: context.startDate

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
        awardPeriodRepository.saveNewStart(cpid = cpid, stage = stage, start = awardPeriodStart)
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
        val schemes = data.mdm.schemas.toSet()

        val invalidScheme = data.award.suppliers.any { supplier ->
            supplier.identifier.scheme !in schemes
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
        val scales = data.mdm.scales.toSet()

        val invalidScale = data.award.suppliers.any { supplier ->
            supplier.details.scale !in scales
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
                                    locality = detail.region.let { locality ->
                                        CreatedAwardData.Award.Supplier.Address.AddressDetails.Locality(
                                            scheme = locality.scheme!!,
                                            id = locality.id,
                                            description = locality.description!!,
                                            uri = locality.uri!!
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
}
