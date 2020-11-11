package com.procurement.evaluation.service

import com.procurement.evaluation.application.repository.period.AwardPeriodRepository
import com.procurement.evaluation.application.repository.period.model.PeriodEntity
import com.procurement.evaluation.domain.model.Cpid
import com.procurement.evaluation.domain.model.Ocid
import com.procurement.evaluation.exception.ErrorException
import com.procurement.evaluation.exception.ErrorType
import com.procurement.evaluation.model.dto.ocds.Period
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class PeriodService(private val periodRepository: AwardPeriodRepository) {

    fun saveEndOfPeriod(cpid: Cpid, endDate: LocalDateTime): Period {
        val period = periodRepository.findByCpid(cpid)
            .orThrow { it.exception }
            ?: throw ErrorException(ErrorType.PERIOD_NOT_FOUND)

        val newPeriod = PeriodEntity(
            cpid = period.cpid,
            ocid = period.ocid,
            awardCriteria = period.awardCriteria,
            startDate = period.startDate!!,
            endDate = endDate
        )
        periodRepository.save(newPeriod)
            .doOnFail { throw it.exception }

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
            .doOnFail { throw it.exception }

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
            .doOnFail { throw it.exception }
    }
}
