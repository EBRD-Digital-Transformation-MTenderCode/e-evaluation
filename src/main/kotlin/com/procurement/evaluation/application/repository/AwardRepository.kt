package com.procurement.evaluation.application.repository

import com.procurement.evaluation.model.entity.AwardEntity
import java.util.*

interface AwardRepository {
    fun findBy(cpid: String): List<AwardEntity>

    fun findBy(cpid: String, stage: String): List<AwardEntity>

    fun findBy(cpid: String, stage: String, token: UUID): AwardEntity?

    fun saveNew(cpid: String, award: AwardEntity)

    fun update(cpid: String, updatedAward: AwardEntity)
}
