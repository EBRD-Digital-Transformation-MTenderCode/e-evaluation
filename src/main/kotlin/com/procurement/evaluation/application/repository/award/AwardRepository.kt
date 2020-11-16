package com.procurement.evaluation.application.repository.award

import com.procurement.evaluation.application.repository.award.model.AwardEntity
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.infrastructure.fail.Failure
import com.procurement.evaluation.lib.functional.Result

interface AwardRepository {

    fun findBy(cpid: Cpid): Result<List<AwardEntity>, Failure.Incident.Database>

    fun findBy(cpid: Cpid, ocid: Ocid): Result<List<AwardEntity>, Failure.Incident.Database>

    fun findBy(cpid: Cpid, ocid: Ocid, token: Token): Result<AwardEntity?, Failure.Incident.Database>

    fun save(cpid: Cpid, award: AwardEntity): Result<Boolean, Failure.Incident.Database>

    fun save(cpid: Cpid, awards: Collection<AwardEntity>): Result<Boolean, Failure.Incident.Database>

    fun update(cpid: Cpid, updatedAwards: Collection<AwardEntity>): Result<Boolean, Failure.Incident.Database>

    fun update(cpid: Cpid, updatedAward: AwardEntity): Result<Boolean, Failure.Incident.Database>
}
