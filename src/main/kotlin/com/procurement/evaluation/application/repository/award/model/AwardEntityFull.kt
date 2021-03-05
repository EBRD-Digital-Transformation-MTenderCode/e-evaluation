package com.procurement.evaluation.application.repository.award.model

import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Owner
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.model.dto.ocds.Award
import com.procurement.evaluation.model.dto.ocds.AwardStatus
import com.procurement.evaluation.model.dto.ocds.AwardStatusDetails
import java.util.*

class AwardEntityFull private constructor(
    val cpid: Cpid,
    val ocid: Ocid,
    val token: Token,
    val status: AwardStatus,
    val statusDetails: AwardStatusDetails,
    val owner: Owner?,
    val award: Award
) {
    companion object {
        fun create(cpid: Cpid, ocid: Ocid, owner: Owner? = null, award: Award) =
            AwardEntityFull(cpid, ocid, UUID.fromString(award.token), award.status, award.statusDetails, owner, award)
    }
}
