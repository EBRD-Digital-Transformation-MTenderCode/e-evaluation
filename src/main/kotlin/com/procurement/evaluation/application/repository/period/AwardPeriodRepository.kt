package com.procurement.evaluation.application.repository.period

import com.procurement.evaluation.application.repository.period.model.PeriodEntity
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.MaybeFail
import java.time.LocalDateTime

interface AwardPeriodRepository {

    fun saveStart(cpid: Cpid, ocid: Ocid, start: LocalDateTime): Result<Boolean, Fail.Incident.Database>

    fun saveEnd(cpid: Cpid, ocid: Ocid, endDate: LocalDateTime): Result<Boolean, Fail.Incident.Database>

    fun findBy(cpid: Cpid): Result<List<PeriodEntity>, Fail.Incident.Database>

    fun findBy(cpid: Cpid, ocid: Ocid): Result<PeriodEntity?, Fail.Incident.Database>


    fun tryFindStartDateByCpidAndOcid(cpid: Cpid, ocid: Ocid): Result<LocalDateTime?, Fail.Incident>

    fun save(entity: PeriodEntity): MaybeFail<Fail.Incident.Database>

    fun findStartDateBy(cpid: Cpid, ocid: Ocid): LocalDateTime?

}
