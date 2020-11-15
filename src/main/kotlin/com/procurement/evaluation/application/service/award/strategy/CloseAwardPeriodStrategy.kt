package com.procurement.evaluation.application.service.award.strategy

import com.procurement.evaluation.application.model.award.close.awardperiod.CloseAwardPeriodParams
import com.procurement.evaluation.application.repository.period.AwardPeriodRepository
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.fail.error.ValidationError
import com.procurement.evaluation.infrastructure.handler.v2.model.response.CloseAwardPeriodResult
import com.procurement.evaluation.lib.functional.Result
import com.procurement.evaluation.lib.functional.asSuccess

class CloseAwardPeriodStrategy(val awardPeriodRepository: AwardPeriodRepository) {

    fun execute(params: CloseAwardPeriodParams): Result<CloseAwardPeriodResult, Fail> {

        awardPeriodRepository.findBy(cpid = params.cpid, ocid = params.ocid)
            .onFailure { return it }
            ?: return Result.failure(ValidationError.PeriodNotFoundOnCloseAwardPeriod())

        val wasApplied = awardPeriodRepository.saveEnd(cpid = params.cpid, ocid = params.ocid, endDate = params.endDate)
            .onFailure { return it }
        if(!wasApplied)
            Fail.Incident.Database.RecordIsNotExist(description = "An error occurred when writing a record(s) of the end award period '${params.endDate}' by cpid '${params.cpid.underlying}' and ocid '${params.ocid.underlying}' to the database. Record is not exists.")

        return CloseAwardPeriodResult(
            awardPeriod = CloseAwardPeriodResult.AwardPeriod(
                endDate = params.endDate
            )
        )
            .asSuccess()
    }
}
