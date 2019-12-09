package com.procurement.evaluation.infrastructure.dto.award.next.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.dto.award.next.GetNextAwardResponse
import org.junit.jupiter.api.Test

class GetNextAwardResponseTest : AbstractDTOTestBase<GetNextAwardResponse>(GetNextAwardResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/next/response/response_get_next_award_full.json")
    }
}
