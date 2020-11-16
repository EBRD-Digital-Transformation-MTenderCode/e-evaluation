package com.procurement.evaluation.infrastructure.dto.award.win

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v1.model.response.WinningAwardResponse
import org.junit.jupiter.api.Test

class WinningAwardResponseTest : AbstractDTOTestBase<WinningAwardResponse>(WinningAwardResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/win/response/response_winning_award_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/award/win/response/response_winning_award_required_1.json")
    }
}
