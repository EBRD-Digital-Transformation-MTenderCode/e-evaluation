package com.procurement.evaluation.infrastructure.dto.award.close.awardperiod

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.close.awardperiod.CloseAwardPeriodResult
import org.junit.jupiter.api.Test

class CloseAwardPeriodResultTest : AbstractDTOTestBase<CloseAwardPeriodResult>(
    CloseAwardPeriodResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/close/close_award_period_result_full.json")
    }
}
