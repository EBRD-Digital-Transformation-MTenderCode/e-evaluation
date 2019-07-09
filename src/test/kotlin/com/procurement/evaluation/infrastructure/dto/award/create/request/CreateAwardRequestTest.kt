package com.procurement.evaluation.infrastructure.dto.award.create.request

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class CreateAwardRequestTest : AbstractDTOTestBase<CreateAwardRequest>(CreateAwardRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/create/request/request_create_award_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/award/create/request/request_create_award_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/award/create/request/request_create_award_required_2.json")
    }
}
