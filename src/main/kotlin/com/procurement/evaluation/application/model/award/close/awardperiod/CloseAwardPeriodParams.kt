package com.procurement.evaluation.application.model.award.close.awardperiod

import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseDate
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess
import java.time.LocalDateTime

class CloseAwardPeriodParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val endDate: LocalDateTime
) {
    companion object {
        fun tryCreate(
            cpid: String,
            ocid: String,
            endDate: String
        ): Result<CloseAwardPeriodParams, DataErrors> {

            val parseCpid = parseCpid(value = cpid)
                .onFailure { return it }

            val parseOcid = parseOcid(value = ocid)
                .onFailure { return it }

            val parseEndDate = parseDate(value = endDate, attributeName = "endDate")
                .onFailure { return it }

            return CloseAwardPeriodParams(cpid = parseCpid, ocid = parseOcid, endDate = parseEndDate)
                .asSuccess()
        }
    }
}
