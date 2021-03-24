package com.procurement.evaluation.infrastructure.dto.award.finalize.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.response.FinalizeAwardsResult
import org.junit.jupiter.api.Test

class FinalizeAwardsResultTest :
    AbstractDTOTestBase<FinalizeAwardsResult>(FinalizeAwardsResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/finalize/response/response_finalize_awards_full.json")
    }
}
