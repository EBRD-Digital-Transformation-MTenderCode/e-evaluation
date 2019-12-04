package com.procurement.evaluation.model.entity

import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import java.util.*

data class AwardEntity(

    val cpId: String,

    val stage: String,

    val token: UUID,

    val status: String,

    var statusDetails: String,

    val owner: String,

    var jsonData: String
) {
    fun checkOwner(owner: Owner) {
        if (this.owner != owner.toString()) throw ErrorException(error = ErrorType.OWNER)
    }
}
