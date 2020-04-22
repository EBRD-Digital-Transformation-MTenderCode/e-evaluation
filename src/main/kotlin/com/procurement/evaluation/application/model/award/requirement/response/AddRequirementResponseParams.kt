package com.procurement.evaluation.application.model.award.requirement.response

import com.procurement.evaluation.application.model.parseAwardId
import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.Result.Companion.failure
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.data.RequirementRsValue
import com.procurement.evaluation.domain.model.requirement.RequirementId
import com.procurement.evaluation.domain.model.requirement.response.RequirementResponseId
import com.procurement.evaluation.domain.model.requirement.response.ResponderId
import com.procurement.evaluation.domain.model.requirement.response.tryRequirementResponseId
import com.procurement.evaluation.domain.model.requirement.response.tryResponderId
import com.procurement.evaluation.domain.model.requirement.tryRequirementId
import com.procurement.evaluation.domain.model.tenderer.TendererId
import com.procurement.evaluation.domain.model.tenderer.tryTendererId
import com.procurement.evaluation.infrastructure.fail.error.DataErrors

class AddRequirementResponseParams private constructor(
    val cpid: Cpid, val ocid: Ocid, val award: Award
) {
    companion object {
        fun tryCreate(
            cpid: String, ocid: String, award: Award
        ): Result<AddRequirementResponseParams, DataErrors> {
            val cpidParsed = parseCpid(cpid)
                .doReturn { error -> return failure(error = error) }

            val ocidParsed = parseOcid(ocid)
                .doReturn { error -> return failure(error = error) }

            return AddRequirementResponseParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                award = award
            ).asSuccess()
        }
    }

    class Award private constructor(
        val id: AwardId,
        val requirementResponse: RequirementResponse
    ) {
        companion object {
            fun tryCreate(
                id: String,
                requirementResponse: RequirementResponse
            ): Result<Award, DataErrors> {
                val awardIdParsed = parseAwardId(id)
                    .doReturn { error -> return failure(error = error) }

                return Award(
                    id = awardIdParsed,
                    requirementResponse = requirementResponse
                ).asSuccess()
            }
        }

        class RequirementResponse private constructor(
            val id: RequirementResponseId,
            val value: RequirementRsValue,
            val relatedTenderer: RelatedTenderer,
            val requirement: Requirement,
            val responder: Responder
        ) {
            companion object {
                fun tryCreate(
                    id: String,
                    value: RequirementRsValue,
                    relatedTenderer: RelatedTenderer,
                    requirement: Requirement,
                    responder: Responder
                ): Result<RequirementResponse, DataErrors> {
                    val parsedRRequirementResponseId = id.tryRequirementResponseId()
                        .doReturn { error ->
                            return failure(
                                DataErrors.Validation.DataFormatMismatch(
                                    name = "requirementResponseId",
                                    expectedFormat = "string",
                                    actualValue = id
                                )
                            )
                        }
                    return RequirementResponse(
                        id = parsedRRequirementResponseId,
                        value = value,
                        relatedTenderer = relatedTenderer,
                        requirement = requirement,
                        responder = responder
                    ).asSuccess()
                }
            }

            class RelatedTenderer private constructor(
                val id: TendererId
            ) {
                companion object {
                    fun tryCreate(id: String): Result<RelatedTenderer, DataErrors> {
                        val parsedRelatedTendererId = id.tryTendererId()
                            .doReturn { error ->
                                return failure(
                                    DataErrors.Validation.DataFormatMismatch(
                                        name = "relatedTendererId",
                                        expectedFormat = "string",
                                        actualValue = id
                                    )
                                )
                            }
                        return RelatedTenderer(parsedRelatedTendererId).asSuccess()
                    }
                }
            }

            class Requirement private constructor(
                val id: RequirementId
            ) {
                companion object {
                    fun tryCreate(id: String): Result<Requirement, DataErrors> {
                        val parsedRequirementId = id.tryRequirementId()
                            .doReturn { error ->
                                return failure(
                                    DataErrors.Validation.DataFormatMismatch(
                                        name = "requirementId",
                                        expectedFormat = "uuid",
                                        actualValue = id
                                    )
                                )
                            }
                        return Requirement(parsedRequirementId).asSuccess()
                    }
                }
            }

            class Responder private constructor(
                val identifier: Identifier, val name: String
            ) {
                companion object {
                    fun tryCreate(identifier: Identifier, name: String) =
                        Responder(
                            identifier = identifier,
                            name = name
                        ).asSuccess<Responder, DataErrors>()
                }

                class Identifier private constructor(val scheme: String, val id: ResponderId) {
                    companion object {
                        fun tryCreate(scheme: String, id: String): Result<Identifier, DataErrors> {
                            val parsedResponderId = id.tryResponderId()
                                .doReturn { error ->
                                    return failure(
                                        DataErrors.Validation.DataFormatMismatch(
                                            name = "respondererId",
                                            expectedFormat = "string",
                                            actualValue = id
                                        )
                                    )
                                }
                            return Identifier(id = parsedResponderId, scheme = scheme).asSuccess()
                        }
                    }
                }
            }
        }
    }
}