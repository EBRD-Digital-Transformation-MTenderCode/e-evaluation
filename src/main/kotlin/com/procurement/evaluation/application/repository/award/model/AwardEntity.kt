package com.procurement.evaluation.application.repository.award.model

import com.procurement.evaluation.application.service.Transform
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails

data class AwardEntity(
    val cpid: Cpid,
    val ocid: Ocid,
    val token: Token,
    val status: AwardStatus,
    val statusDetails: AwardStatusDetails,
    val owner: Owner?,
    val jsonData: String
) {
    companion object {
        fun create(
            awardEntityFull: AwardEntityFull,
            transform: Transform
        ): Result<AwardEntity, Failure.Incident.Transform.Serialization> =
            AwardEntity(
                cpid = awardEntityFull.cpid,
                ocid = awardEntityFull.ocid,
                token = awardEntityFull.token,
                status = awardEntityFull.status,
                statusDetails = awardEntityFull.statusDetails,
                owner = awardEntityFull.owner,
                jsonData = transform.trySerialization(awardEntityFull).onFailure { return it }
            ).asSuccess()
    }

    fun checkOwner(owner: Owner) {
        if (this.owner != owner) throw ErrorException(error = ErrorType.INVALID_OWNER)
    }
}
