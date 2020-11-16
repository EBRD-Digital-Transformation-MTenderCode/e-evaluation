package com.procurement.evaluation.application.repository.period

import com.procurement.evaluation.application.repository.period.model.PeriodEntity
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.lib.functional.Result
import java.time.LocalDateTime

interface AwardPeriodRepository {

    fun saveStart(cpid: Cpid, ocid: Ocid, start: LocalDateTime): Result<Boolean, Failure.Incident.Database>

    fun saveEnd(cpid: Cpid, ocid: Ocid, endDate: LocalDateTime): Result<Boolean, Failure.Incident.Database>

    fun findBy(cpid: Cpid): Result<List<PeriodEntity>, Failure.Incident.Database>

    fun findBy(cpid: Cpid, ocid: Ocid): Result<PeriodEntity?, Failure.Incident.Database>

}
