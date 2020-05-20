package com.procurement.evaluation.application.model.award.access

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
import com.procurement.evaluation.infrastructure.fail.error.DataErrors

class CheckAccessToAwardParams private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val token: Token,
    val owner: Owner,
    val awardId: AwardId
) {
    companion object {
        fun tryCreate(
            cpid: String,
            ocid: String,
            token: String,
            owner: String,
            awardId: String
        ): Result<CheckAccessToAwardParams, DataErrors> {
            val cpidParsed = parseCpid(cpid)
                .doReturn { error -> return failure(error = error) }

            val ocidParsed = parseOcid(ocid)
                .doReturn { error -> return failure(error = error) }

            val tokenParsed = parseToken(token)
                .doReturn { error -> return failure(error = error) }

            val ownerParsed = parseOwner(owner)
                .doReturn { error -> return failure(error = error) }

            val awardIdParsed = parseAwardId(awardId)
                .doReturn { error -> return failure(error = error) }

            return CheckAccessToAwardParams(
                cpid = cpidParsed,
                awardId = awardIdParsed,
                ocid = ocidParsed,
                owner = ownerParsed,
                token = tokenParsed
            ).asSuccess()
        }
    }
}