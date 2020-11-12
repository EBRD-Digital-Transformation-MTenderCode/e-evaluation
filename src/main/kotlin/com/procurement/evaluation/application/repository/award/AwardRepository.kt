package com.procurement.evaluation.application.repository.award

import com.procurement.evaluation.application.repository.award.model.AwardEntity
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.infrastructure.fail.Fail

interface AwardRepository {

    fun findBy(cpid: Cpid): Result<List<AwardEntity>, Fail.Incident.Database>

    fun findBy(cpid: Cpid, ocid: Ocid, token: Token): AwardEntity?

    fun saveNew(cpid: Cpid, award: AwardEntity)

    fun saveNew(cpid: Cpid, awards: Collection<AwardEntity>)

    fun update(cpid: Cpid, updatedAward: AwardEntity)

    fun update(cpid: Cpid, updatedAwards: Collection<AwardEntity>)

    fun findBy(cpid: Cpid, ocid: Ocid): Result<List<AwardEntity>, Fail.Incident.Database>

    fun trySave(cpid: Cpid, awards: Collection<AwardEntity>): Result<Unit, Fail.Incident>

    fun tryUpdate(cpid: Cpid, updatedAward: AwardEntity): Result<Boolean, Fail.Incident>
}
