package com.procurement.evaluation.application.model.award.start.awardperiod

import com.procurement.evaluation.application.model.parseCpid
import com.procurement.evaluation.application.model.parseDate
import com.procurement.evaluation.application.model.parseOcid
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.fail.error.DataErrors
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess
import java.time.LocalDateTime

class StartAwardPeriodParams(
    val cpid: Cpid,
    val ocid: Ocid,
    val date: LocalDateTime
) {
    companion object {
        fun tryCreate(
            cpid: String,
            ocid: String,
            date: String
        ): Result<StartAwardPeriodParams, DataErrors> {

            val parseCpid = parseCpid(value = cpid)
                .onFailure { return it }

            val parseOcid = parseOcid(value = ocid)
                .onFailure { return it }

            val parseDate = parseDate(value = date, attributeName = "date")
                .onFailure { return it }

            return StartAwardPeriodParams(cpid = parseCpid, ocid = parseOcid, date = parseDate)
                .asSuccess()
        }
    }
}
