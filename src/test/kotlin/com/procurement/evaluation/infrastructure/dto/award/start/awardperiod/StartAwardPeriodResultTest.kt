package com.procurement.evaluation.infrastructure.dto.award.start.awardperiod

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.response.StartAwardPeriodResult
import org.junit.jupiter.api.Test

class StartAwardPeriodResultTest : AbstractDTOTestBase<StartAwardPeriodResult>(StartAwardPeriodResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/start/start_award_period_result_full.json")
    }

}
