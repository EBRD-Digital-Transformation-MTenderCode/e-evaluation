package com.procurement.evaluation.infrastructure.dto.award.consideration.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v1.model.response.StartConsiderationResponse
import org.junit.jupiter.api.Test

class StartConsiderationResponseTest :
    AbstractDTOTestBase<StartConsiderationResponse>(StartConsiderationResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/consideration/response/response_start_consideration_full.json")
    }
}
