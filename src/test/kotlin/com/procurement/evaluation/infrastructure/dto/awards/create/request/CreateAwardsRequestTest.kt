package com.procurement.evaluation.infrastructure.dto.awards.create.request

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v1.model.request.CreateAwardsRequest
import org.junit.jupiter.api.Test

class CreateAwardsRequestTest : AbstractDTOTestBase<CreateAwardsRequest>(
    CreateAwardsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/awards/create/request/request_create_awards_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/awards/create/request/request_create_awards_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/awards/create/request/request_create_awards_required_2.json")
    }

    @Test
    fun required3() {
        testBindingAndMapping("json/infrastructure/dto/awards/create/request/request_create_awards_required_3.json")
    }
}
