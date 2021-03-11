package com.procurement.evaluation.infrastructure.dto.award.`do`.consideration

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.request.DoConsiderationRequest
import org.junit.jupiter.api.Test

class DoConsiderationRequestTest : AbstractDTOTestBase<DoConsiderationRequest>(DoConsiderationRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/do/consideration/do_consideration_request_full.json")
    }
}
