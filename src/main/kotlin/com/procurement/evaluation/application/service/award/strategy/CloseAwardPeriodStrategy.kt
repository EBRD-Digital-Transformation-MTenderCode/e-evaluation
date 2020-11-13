package com.procurement.evaluation.application.service.award.strategy

import com.procurement.evaluation.application.model.award.close.awardperiod.CloseAwardPeriodParams
import com.procurement.evaluation.application.repository.period.AwardPeriodRepository
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.fail.error.ValidationError
import com.procurement.evaluation.infrastructure.handler.close.awardperiod.CloseAwardPeriodResult

class CloseAwardPeriodStrategy(val awardPeriodRepository: AwardPeriodRepository) {

    fun execute(params: CloseAwardPeriodParams): Result<CloseAwardPeriodResult, Fail> {

        awardPeriodRepository.tryFindStartDateByCpidAndOcid(cpid = params.cpid, ocid = params.ocid)
            .orForwardFail { error -> return error }
            ?: return Result.failure(ValidationError.PeriodNotFoundOnCloseAwardPeriod())

        awardPeriodRepository.saveEnd(cpid = params.cpid, ocid = params.ocid, endDate = params.endDate)
            .doOnError { error -> return Result.failure(error) }

        return CloseAwardPeriodResult(
            awardPeriod = CloseAwardPeriodResult.AwardPeriod(
                endDate = params.endDate
            )
        )
            .asSuccess()
    }
}
