package com.procurement.evaluation.infrastructure.dto.award.create.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v1.model.response.CreateAwardResponse
import org.junit.jupiter.api.Test

class CreateAwardResponseTest : AbstractDTOTestBase<CreateAwardResponse>(CreateAwardResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/create/response/response_create_award_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/award/create/response/response_create_award_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/award/create/response/response_create_award_required_2.json")
    }
}
