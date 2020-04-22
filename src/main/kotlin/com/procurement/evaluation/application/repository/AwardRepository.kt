package com.procurement.evaluation.application.repository

import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.award.AwardId
import com.procurement.evaluation.domain.model.enums.Stage
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.model.entity.AwardEntity
import java.util.*

interface AwardRepository {
    fun findBy(cpid: String): List<AwardEntity>

    fun findBy(cpid: String, stage: String): List<AwardEntity>

    fun findBy(cpid: String, stage: String, token: UUID): AwardEntity?

    fun saveNew(cpid: String, award: AwardEntity)

    fun saveNew(cpid: String, awards: List<AwardEntity>)

    fun update(cpid: String, updatedAward: AwardEntity)

    fun update(cpid: String, updatedAwards: Collection<AwardEntity>)

    fun tryFindBy(cpid: Cpid, stage: Stage): Result<List<AwardEntity>, Fail.Incident>

    fun tryFindBy(cpid: Cpid, stage: Stage, awardId: AwardId): Result<AwardEntity?, Fail>

    fun trySave(cpid: Cpid, awards: List<AwardEntity>): Result<Unit, Fail.Incident>

    fun tryUpdate(cpid: Cpid, updatedAward: AwardEntity): Result<Boolean, Fail.Incident>
}
