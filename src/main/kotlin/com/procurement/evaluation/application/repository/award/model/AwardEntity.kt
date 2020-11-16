package com.procurement.evaluation.application.repository.award.model

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
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
    fun checkOwner(owner: Owner) {
        if (this.owner != owner) throw ErrorException(error = ErrorType.INVALID_OWNER)
    }
}
