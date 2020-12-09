package com.procurement.evaluation.infrastructure.dto.award.start.awardperiod

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.request.StartAwardPeriodRequest
import org.junit.jupiter.api.Test

class StartAwardPeriodRequestTest : AbstractDTOTestBase<StartAwardPeriodRequest>(StartAwardPeriodRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/start/start_award_period_request_full.json")
    }
}
