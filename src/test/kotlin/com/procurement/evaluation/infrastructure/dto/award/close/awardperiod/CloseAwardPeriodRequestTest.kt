package com.procurement.evaluation.infrastructure.dto.award.close.awardperiod

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.request.CloseAwardPeriodRequest
import org.junit.jupiter.api.Test

class CloseAwardPeriodRequestTest : AbstractDTOTestBase<CloseAwardPeriodRequest>(
    CloseAwardPeriodRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/close/close_award_period_request_full.json")
    }
}
