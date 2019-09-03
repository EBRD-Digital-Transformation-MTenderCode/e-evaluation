package com.procurement.evaluation.infrastructure.dto.award.cancel.request

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class AwardCancellationRequestTest : AbstractDTOTestBase<AwardCancellationRequest>(
    AwardCancellationRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/cancel/request/request_cancel_award_full.json")
    }
}
