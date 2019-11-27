package com.procurement.evaluation.infrastructure.dto.award.period.start.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.dto.award.period.start.StartAwardPeriodResponse
import org.junit.jupiter.api.Test

class StartAwardPeriodResponseTest : AbstractDTOTestBase<StartAwardPeriodResponse>(StartAwardPeriodResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/period/start/response/response_start_award_period_full.json")
    }
}
