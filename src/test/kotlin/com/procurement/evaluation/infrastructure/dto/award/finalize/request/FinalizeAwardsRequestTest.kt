package com.procurement.evaluation.infrastructure.dto.award.finalize.request

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.request.FinalizeAwardsRequest
import org.junit.jupiter.api.Test

class FinalizeAwardsRequestTest :
    AbstractDTOTestBase<FinalizeAwardsRequest>(FinalizeAwardsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/finalize/request/request_finalize_awards_full.json")
    }
}
