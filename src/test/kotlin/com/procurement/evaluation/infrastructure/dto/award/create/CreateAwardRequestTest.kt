package com.procurement.evaluation.infrastructure.dto.award.create

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.request.CreateAwardRequest
import org.junit.jupiter.api.Test

class CreateAwardRequestTest : AbstractDTOTestBase<CreateAwardRequest>(CreateAwardRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/create/create_award_request_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/award/create/create_award_request_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/award/create/create_award_request_required_2.json")
    }

    @Test
    fun required3() {
        testBindingAndMapping("json/infrastructure/dto/award/create/create_award_request_required_3.json")
    }
}
