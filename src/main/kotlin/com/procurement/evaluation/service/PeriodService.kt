package com.procurement.evaluation.service

import com.procurement.evaluation.dao.PeriodDao
import com.procurement.evaluation.model.dto.ocds.Period
import com.procurement.evaluation.model.entity.PeriodEntity
import com.procurement.evaluation.utils.toDate
import com.procurement.evaluation.utils.toLocal
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

interface PeriodService {

    fun saveStartOfPeriod(cpId: String, stage: String, startDate: LocalDateTime): Period

    fun saveEndOfPeriod(cpId: String, stage: String, endDate: LocalDateTime): Period
}

@Service
class PeriodServiceImpl(private val periodRepository: PeriodDao) : PeriodService {

    override fun saveStartOfPeriod(cpId: String, stage: String, startDate: LocalDateTime): Period {
        val period = getEntity(
                cpId = cpId,
                stage = stage,
                startDate = startDate.toDate(),
                endDate = null
        )
        periodRepository.save(period)
        return Period(period.startDate.toLocal(), null)
    }

    override fun saveEndOfPeriod(cpId: String, stage: String, endDate: LocalDateTime): Period {
        val period = periodRepository.getByCpIdAndStage(cpId, stage)
        val newPeriod = getEntity(
                cpId = period.cpId,
                stage = period.stage,
                startDate = period.startDate,
                endDate = endDate.toDate()
        )
        periodRepository.save(newPeriod)
        return Period(newPeriod.startDate.toLocal(), newPeriod.endDate?.toLocal())
    }

    private fun getEntity(cpId: String,
                          stage: String,
                          startDate: Date,
                          endDate: Date?): PeriodEntity {
        return PeriodEntity(
                cpId = cpId,
                stage = stage,
                startDate = startDate,
                endDate = endDate
        )
    }
}
