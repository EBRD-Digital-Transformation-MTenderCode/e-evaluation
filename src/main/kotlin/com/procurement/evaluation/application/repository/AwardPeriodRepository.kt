package com.procurement.evaluation.application.repository

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.enums.Stage
import com.procurement.evaluation.infrastructure.fail.Fail
import java.time.LocalDateTime

interface AwardPeriodRepository {
    fun findStartDateBy(cpid: String, stage: String): LocalDateTime?

    fun saveNewStart(cpid: String, stage: String, start: LocalDateTime)

    fun saveEnd(cpid: String, stage: String, end: LocalDateTime)

    fun tryFindStartDateByCpidAndStage(cpid: Cpid, stage: Stage): Result<LocalDateTime?, Fail.Incident>

    fun trySaveEnd(cpid: Cpid, stage: Stage, endDate: LocalDateTime): Result<Unit, Fail.Incident>
}
