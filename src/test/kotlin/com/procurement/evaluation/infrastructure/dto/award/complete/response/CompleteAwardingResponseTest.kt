package com.procurement.evaluation.infrastructure.dto.award.complete.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v1.model.response.CompleteAwardingResponse
import org.junit.jupiter.api.Test

class CompleteAwardingResponseTest : AbstractDTOTestBase<CompleteAwardingResponse>(CompleteAwardingResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/complete/response/response_complete_awarding_full.json")
    }
}
