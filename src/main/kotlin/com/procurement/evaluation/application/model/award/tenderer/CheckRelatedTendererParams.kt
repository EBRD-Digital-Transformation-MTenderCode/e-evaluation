package com.procurement.evaluation.application.model.award.tenderer

import com.procurement.evaluation.application.model.parseAwardId
import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.Result.Companion.failure
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.requirement.RequirementId
import com.procurement.evaluation.domain.model.requirement.tryRequirementId
import com.procurement.evaluation.domain.model.tenderer.TendererId
import com.procurement.evaluation.domain.model.tenderer.tryTendererId
import com.procurement.evaluation.infrastructure.fail.error.DataErrors

class CheckRelatedTendererParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val awardId: AwardId,
    val requirementId: RequirementId,
    val relatedTendererId: TendererId
) {
    companion object {
        fun tryCreate(
            cpid: String,
            ocid: String,
            awardId: String,
            requirementId: String,
            relatedTendererId: String
        ): Result<CheckRelatedTendererParams, DataErrors> {
            val cpidParsed = parseCpid(cpid)
                .doReturn { error -> return failure(error = error) }

            val ocidParsed = parseOcid(ocid)
                .doReturn { error -> return failure(error = error) }

            val awardIdParsed = parseAwardId(awardId)
                .doReturn { error -> return failure(error = error) }

            val requirementIdParsed = requirementId.tryRequirementId()
                .doReturn {
                    return failure(
                        DataErrors.Validation.DataFormatMismatch(
                            name = "requirementId",
                            expectedFormat = "uuid",
                            actualValue = requirementId
                        )
                    )
                }

            val relatedTendererIdParsed = relatedTendererId.tryTendererId()
                .doReturn {
                    return failure(
                        DataErrors.Validation.DataFormatMismatch(
                            name = "relatedTendererId",
                            expectedFormat = "string",
                            actualValue = relatedTendererId
                        )
                    )
                }

            return CheckRelatedTendererParams(
                cpid = cpidParsed,
                ocid = ocidParsed,
                awardId = awardIdParsed,
                relatedTendererId = relatedTendererIdParsed,
                requirementId = requirementIdParsed
            ).asSuccess()
        }
    }
}