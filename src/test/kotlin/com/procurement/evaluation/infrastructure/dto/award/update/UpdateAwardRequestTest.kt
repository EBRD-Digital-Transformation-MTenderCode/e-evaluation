package com.procurement.evaluation.infrastructure.dto.award.update

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.request.UpdateAwardRequest
import org.junit.jupiter.api.Test

class UpdateAwardRequestTest : AbstractDTOTestBase<UpdateAwardRequest>(UpdateAwardRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/update/update_award_request_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/award/update/update_award_request_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/award/update/update_award_request_required_2.json")
    }

    @Test
    fun required3() {
        testBindingAndMapping("json/infrastructure/dto/award/update/update_award_request_required_3.json")
    }
}
