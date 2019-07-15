package com.procurement.evaluation.application.repository

import java.time.LocalDateTime

interface AwardPeriodRepository {
    fun findStartDateBy(cpid: String, stage: String): LocalDateTime?

    fun saveNewStart(cpid: String, stage: String, start: LocalDateTime)
}
