package com.procurement.evaluation.infrastructure.dto.award.evaluate.request

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v1.model.request.SetAwardForEvaluationRequest
import org.junit.jupiter.api.Test

class SetAwardForEvaluationRequestTest :
    AbstractDTOTestBase<SetAwardForEvaluationRequest>(SetAwardForEvaluationRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/evaluate/request/request_set_award_for_evaluation_full.json")
    }
}