package com.procurement.evaluation.service

import com.procurement.evaluation.dao.PeriodDao
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.model.dto.ocds.Period
import com.procurement.evaluation.model.entity.PeriodEntity
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PeriodService(private val periodRepository: PeriodDao) {

    fun saveStartOfPeriod(cpid: Cpid, ocid: Ocid, startDate: LocalDateTime, awardCriteria: String): Period {
        val period = PeriodEntity(
            cpid = cpid,
            ocid = ocid,
            awardCriteria = awardCriteria,
            startDate = startDate,
            endDate = null
        )

        periodRepository.save(period)
        return Period(period.startDate!!, null)
    }

    fun saveEndOfPeriod(cpid: Cpid, ocid: Ocid, endDate: LocalDateTime): Period {
        val period = periodRepository.getByCpIdAndStage(cpid, ocid)
        val newPeriod = PeriodEntity(
            cpid = period.cpid,
            ocid = period.ocid,
            awardCriteria = period.awardCriteria,
            startDate = period.startDate!!,
            endDate = endDate
        )
        periodRepository.save(newPeriod)
        return Period(period.startDate, endDate)
    }

    fun savePeriod(
        cpid: Cpid,
        ocid: Ocid,
        startDate: LocalDateTime,
        endDate: LocalDateTime,
        awardCriteria: String
    ): Period {
        val period = PeriodEntity(
            cpid = cpid,
            ocid = ocid,
            awardCriteria = awardCriteria,
            startDate = startDate,
            endDate = endDate
        )
        periodRepository.save(period)
        return Period(startDate, endDate)
    }

    fun saveAwardCriteria(cpid: Cpid, ocid: Ocid, awardCriteria: String) {
        val period = PeriodEntity(
            cpid = cpid,
            ocid = ocid,
            awardCriteria = awardCriteria,
            startDate = null,
            endDate = null
        )
        periodRepository.save(period)
    }
}
