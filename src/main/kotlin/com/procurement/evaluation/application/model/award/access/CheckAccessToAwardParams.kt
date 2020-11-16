package com.procurement.evaluation.application.model.award.access

import com.procurement.evaluation.application.model.parseAwardId
import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.application.model.parseOwner
import com.procurement.evaluation.application.model.parseToken
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess

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
                .onFailure { return it }

            val ocidParsed = parseOcid(ocid)
                .onFailure { return it }

            val tokenParsed = parseToken(token)
                .onFailure { return it }

            val ownerParsed = parseOwner(owner)
                .onFailure { return it }

            val awardIdParsed = parseAwardId(awardId)
                .onFailure { return it }

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
