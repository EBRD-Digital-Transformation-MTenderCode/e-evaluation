package com.procurement.evaluation.infrastructure.dto.award.next.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v1.model.response.GetNextAwardResponse
import org.junit.jupiter.api.Test

class GetNextAwardResponseTest : AbstractDTOTestBase<GetNextAwardResponse>(GetNextAwardResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/next/response/response_get_next_award_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/award/next/response/response_get_next_award_required_1.json")
    }
}
