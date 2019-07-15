package com.procurement.evaluation.application.repository

import com.procurement.evaluation.model.entity.AwardEntity

interface AwardRepository {
    fun findBy(cpid: String): List<AwardEntity>

    fun saveNew(cpid: String, award: AwardEntity)
}
