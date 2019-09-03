package com.procurement.evaluation.infrastructure.dto.award.cancel.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class AwardCancellationResponseTest :
    AbstractDTOTestBase<AwardCancellationResponse>(AwardCancellationResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/cancel/response/response_cancel_award_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/award/cancel/response/response_cancel_award_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/award/cancel/response/response_cancel_award_required_2.json")
    }
}
