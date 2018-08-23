package com.procurement.evaluation.service

import com.procurement.evaluation.dao.PeriodDao
import com.procurement.evaluation.model.dto.ocds.Period
import com.procurement.evaluation.model.entity.PeriodEntity
import com.procurement.evaluation.utils.localNowUTC
import com.procurement.evaluation.utils.toDate
import com.procurement.evaluation.utils.toLocal
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

interface PeriodService {

    fun saveStartOfPeriod(cpId: String, stage: String, startDate: LocalDateTime,awardCriteria: String): Period

    fun saveEndOfPeriod(cpId: String, stage: String, endDate: LocalDateTime): Period

    fun savePeriod(cpId: String, stage: String, startDate: LocalDateTime, endDate: LocalDateTime,awardCriteria: String): Period

    fun checkPeriod(cpId: String, stage: String): Boolean
}

@Service
class PeriodServiceImpl(private val periodRepository: PeriodDao) : PeriodService {

    override fun saveStartOfPeriod(cpId: String, stage: String, startDate: LocalDateTime,awardCriteria: String): Period {
        val period = getEntity(
            cpId = cpId,
            stage = stage,
            awardCriteria = awardCriteria,
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
            awardCriteria = period.awardCriteria,
            startDate = period.startDate,
            endDate = endDate.toDate()
        )
        periodRepository.save(newPeriod)
        return Period(newPeriod.startDate.toLocal(), newPeriod.endDate?.toLocal())
    }

    override fun savePeriod(cpId: String, stage: String, startDate: LocalDateTime, endDate: LocalDateTime,awardCriteria: String): Period {
        val period = getEntity(
            cpId = cpId,
            stage = stage,
            awardCriteria = awardCriteria,
            startDate = startDate.toDate(),
            endDate = endDate.toDate()
        )
        periodRepository.save(period)
        return Period(period.startDate.toLocal(), period.endDate?.toLocal())
    }

    override fun checkPeriod(cpId: String, stage: String): Boolean {
        val localDateTime = localNowUTC()
        val periodEntity = periodRepository.getByCpIdAndStage(cpId, stage)
        val isStartDateValid = localDateTime >= periodEntity.startDate.toLocal()
        var isEndDateValid = true
        if (periodEntity.endDate != null) {
            isEndDateValid = localDateTime <= periodEntity.endDate!!.toLocal()
        }
        return isStartDateValid && isEndDateValid
    }

    private fun getEntity(cpId: String,
                          stage: String,
                          startDate: Date,
                          awardCriteria: String,
                          endDate: Date?): PeriodEntity {
        return PeriodEntity(
            cpId = cpId,
            stage = stage,
            awardCriteria = awardCriteria,
            startDate = startDate,
            endDate = endDate
        )
    }
}
