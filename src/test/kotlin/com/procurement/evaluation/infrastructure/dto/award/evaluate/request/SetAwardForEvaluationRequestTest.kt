package com.procurement.evaluation.infrastructure.dto.award.evaluate.request

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class SetAwardForEvaluationRequestTest :
    AbstractDTOTestBase<SetAwardForEvaluationRequest>(SetAwardForEvaluationRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/evaluate/request/request_set_award_for_evaluation_full.json")
    }
}