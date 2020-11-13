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
        val periods = periodRepository.findBy(cpid)
            .orThrow { it.exception }

        if (periods.size > 1)
            throw ErrorException(
                error = ErrorType.PERIOD_INVALID,
                message = "Expected 1 record, but founded ${periods.size}"
            )

        if (periods.isEmpty())
            throw ErrorException(ErrorType.PERIOD_NOT_FOUND)

        val period = periods.first()

        val wasApplied = periodRepository.saveEnd(period.cpid, period.ocid, endDate)
            .orThrow { throw it.exception }

        if (!wasApplied)
            throw ErrorException(
                error = ErrorType.DATABASE,
                message = "An error occurred when writing a record(s) of the end award period '$endDate' by cpid '$cpid' and ocid '${period.ocid}' to the database. Record is not exists."
            )

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
            startDate = startDate,
            endDate = endDate
        )
        periodRepository.save(period)
            .doOnFail { throw it.exception }

        return Period(startDate, endDate)
    }
}
