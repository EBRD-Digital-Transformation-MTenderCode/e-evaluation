package com.procurement.evaluation.application.repository.period

import com.procurement.evaluation.application.repository.period.model.PeriodEntity
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.lib.functional.MaybeFail
import java.time.LocalDateTime

interface AwardPeriodRepository {

    fun save(entity: PeriodEntity): MaybeFail<Fail.Incident.Database.DatabaseInteractionIncident>

    fun findByCpid(cpid: Cpid): Result<PeriodEntity?, Fail.Incident.Database.DatabaseInteractionIncident>

    fun findStartDateBy(cpid: Cpid, ocid: Ocid): LocalDateTime?

    fun saveNewStart(cpid: Cpid, ocid: Ocid, start: LocalDateTime)

    fun tryFindStartDateByCpidAndOcid(cpid: Cpid, ocid: Ocid): Result<LocalDateTime?, Fail.Incident>

    fun trySaveEnd(cpid: Cpid, ocid: Ocid, endDate: LocalDateTime): Result<Unit, Fail.Incident>
}
