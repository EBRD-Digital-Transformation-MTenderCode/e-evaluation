package com.procurement.evaluation.infrastructure.dto.award.finalize.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class FinalAwardsStatusByLotsResponseTest :
    AbstractDTOTestBase<FinalAwardsStatusByLotsResponse>(FinalAwardsStatusByLotsResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/finalize/response/response_final_awards_status_by_lots_full.json")
    }
}
