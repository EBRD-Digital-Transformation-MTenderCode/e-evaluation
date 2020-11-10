package com.procurement.evaluation.application.repository

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.fail.Fail
import java.time.LocalDateTime

interface AwardPeriodRepository {
    fun findStartDateBy(cpid: Cpid, ocid: Ocid): LocalDateTime?

    fun saveNewStart(cpid: Cpid, ocid: Ocid, start: LocalDateTime)

    fun saveEnd(cpid: Cpid, ocid: Ocid, end: LocalDateTime)

    fun tryFindStartDateByCpidAndStage(cpid: Cpid, ocid: Ocid): Result<LocalDateTime?, Fail.Incident>

    fun trySaveEnd(cpid: Cpid, ocid: Ocid, endDate: LocalDateTime): Result<Unit, Fail.Incident>
}
