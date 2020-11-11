package com.procurement.evaluation.application.repository.award.model

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import java.util.*

data class AwardEntity(
    val cpid: Cpid,
    val ocid: Ocid,
    val token: UUID,
    val status: String,
    val statusDetails: String,
    val owner: String?,
    val jsonData: String
) {
    fun checkOwner(owner: Owner) {
        if (this.owner != owner.toString()) throw ErrorException(error = ErrorType.INVALID_OWNER)
    }
}
