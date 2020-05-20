package com.procurement.evaluation.application.service.award.strategy

import com.procurement.evaluation.application.model.award.close.awardperiod.CloseAwardPeriodParams
import com.procurement.evaluation.application.repository.AwardPeriodRepository
import com.procurement.evaluation.domain.functional.Result
import com.procurement.evaluation.domain.functional.asSuccess
import com.procurement.evaluation.infrastructure.fail.Fail
import com.procurement.evaluation.infrastructure.fail.error.ValidationError
import com.procurement.evaluation.infrastructure.handler.close.awardperiod.CloseAwardPeriodResult

class CloseAwardPeriodStrategy(val awardPeriodRepository: AwardPeriodRepository) {

    fun execute(params: CloseAwardPeriodParams): Result<CloseAwardPeriodResult, Fail> {

        awardPeriodRepository.tryFindStartDateByCpidAndStage(cpid = params.cpid, stage = params.ocid.stage)
            .orForwardFail { error -> return error }
            ?: return Result.failure(ValidationError.PeriodNotFoundOnCloseAwardPeriod())

        awardPeriodRepository.trySaveEnd(cpid = params.cpid, stage = params.ocid.stage, endDate = params.endDate)
            .doOnError { error -> return Result.failure(error) }

        return CloseAwardPeriodResult(
            awardPeriod = CloseAwardPeriodResult.AwardPeriod(
                endDate = params.endDate
            )
        )
            .asSuccess()
    }
}
