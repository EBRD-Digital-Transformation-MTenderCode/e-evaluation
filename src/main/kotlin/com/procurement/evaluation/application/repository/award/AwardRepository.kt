package com.procurement.evaluation.application.repository.award

import com.procurement.evaluation.application.repository.award.model.AwardEntity
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.domain.model.Token
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.infrastructure.fail.Fail

interface AwardRepository {
    fun findBy(cpid: Cpid): List<AwardEntity>

    fun findBy(cpid: Cpid, ocid: Ocid): List<AwardEntity>

    fun findBy(cpid: Cpid, ocid: Ocid, token: Token): AwardEntity?

    fun saveNew(cpid: Cpid, award: AwardEntity)

    fun saveNew(cpid: Cpid, awards: List<AwardEntity>)

    fun update(cpid: Cpid, updatedAward: AwardEntity)

    fun update(cpid: Cpid, updatedAwards: Collection<AwardEntity>)

    fun tryFindBy(cpid: Cpid, ocid: Ocid): Result<List<AwardEntity>, Fail.Incident>

    fun tryFindBy(cpid: Cpid, ocid: Ocid, awardId: AwardId): Result<AwardEntity?, Fail>

    fun trySave(cpid: Cpid, awards: List<AwardEntity>): Result<Unit, Fail.Incident>

    fun tryUpdate(cpid: Cpid, updatedAward: AwardEntity): Result<Boolean, Fail.Incident>
}
