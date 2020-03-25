package com.procurement.evaluation.application.model.award.requirement.response

import com.procurement.evaluation.application.model.parseAwardId
import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.application.model.parseOwner
import com.procurement.evaluation.application.model.parseToken
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.Result.Companion.failure
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.data.RequirementRsValue
import com.procurement.evaluation.domain.model.requirement.RequirementId
import com.procurement.evaluation.domain.model.requirement.response.RequirementResponseId
import com.procurement.evaluation.domain.model.requirement.response.RespondererId
import com.procurement.evaluation.domain.model.requirement.response.tryRequirementResponseId
import com.procurement.evaluation.domain.model.requirement.response.tryRespondererId
import com.procurement.evaluation.domain.model.requirement.tryRequirementId
import com.procurement.evaluation.domain.model.tenderer.TendererId
import com.procurement.evaluation.domain.model.tenderer.tryTendererId
import com.procurement.evaluation.infrastructure.fail.error.DataErrors

class CreateRequirementResponseParams private constructor(
    val cpid: Cpid, val ocid: Ocid, val award: Award
) {
    companion object {
        fun tryCreate(
            cpid: String, ocid: String, award: Award
        ): Result<CreateRequirementResponseParams, DataErrors> {
            val cpidParsed = parseCpid(cpid)
                .doReturn { error -> return failure(error = error) }

            val ocidParsed = parseOcid(ocid)
                .doReturn { error -> return failure(error = error) }

            return CreateRequirementResponseParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                award = award
            ).asSuccess()
        }
    }

    class Award private constructor(
        val id: AwardId,
        val owner: Owner,
        val token: Token,
        val requirementResponse: RequirementResponse
    ) {
        companion object {
            fun tryCreate(
                id: String,
                owner: String,
                token: String,
                requirementResponse: RequirementResponse
            ): Result<Award, DataErrors> {
                val tokenParsed = parseToken(token)
                    .doReturn { error -> return failure(error = error) }

                val ownerParsed = parseOwner(owner)
                    .doReturn { error -> return failure(error = error) }

                val awardIdParsed = parseAwardId(id)
                    .doReturn { error -> return failure(error = error) }

                return Award(
                    id = awardIdParsed,
                    token = tokenParsed,
                    owner = ownerParsed,
                    requirementResponse = requirementResponse
                ).asSuccess()
            }
        }

        class RequirementResponse private constructor(
            val id: RequirementResponseId,
            val value: RequirementRsValue,
            val relatedTenderer: RelatedTenderer,
            val requirement: Requirement,
            val responderer: Responderer
        ) {
            companion object {
                fun tryCreate(
                    id: String,
                    value: RequirementRsValue,
                    relatedTenderer: RelatedTenderer,
                    requirement: Requirement,
                    responderer: Responderer
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
                        responderer = responderer
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

            class Responderer private constructor(
                val id: RespondererId, val name: String
            ) {
                companion object {
                    fun tryCreate(id: String, name: String): Result<Responderer, DataErrors> {
                        val parsedRespondererId = id.tryRespondererId()
                            .doReturn { error ->
                                return failure(
                                    DataErrors.Validation.DataFormatMismatch(
                                        name = "respondererId",
                                        expectedFormat = "string",
                                        actualValue = id
                                    )
                                )
                            }
                        return Responderer(
                            id = parsedRespondererId,
                            name = name
                        ).asSuccess()
                    }
                }
            }
        }
    }
}