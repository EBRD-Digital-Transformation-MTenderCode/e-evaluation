package com.procurement.evaluation.infrastructure.dto.award.evaluate.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.dto.award.EvaluatedAwardsResponse
import org.junit.jupiter.api.Test

class EvaluatedAwardsResponseTest : AbstractDTOTestBase<EvaluatedAwardsResponse>(EvaluatedAwardsResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/evaluate/response/response_evaluated_awards_full.json")
    }
}
