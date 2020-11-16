package com.procurement.evaluation.infrastructure.dto.award.finalize.request

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v1.model.request.FinalAwardsStatusByLotsRequest
import org.junit.jupiter.api.Test

class FinalAwardsStatusByLotsRequestTest :
    AbstractDTOTestBase<FinalAwardsStatusByLotsRequest>(FinalAwardsStatusByLotsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/finalize/request/request_final_awards_status_by_lots_full.json")
    }
}
