package com.procurement.evaluation.infrastructure.dto.award.validate

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.request.ValidateAwardDataRequest
import org.junit.jupiter.api.Test

class ValidateAwardDataRequestTest : AbstractDTOTestBase<ValidateAwardDataRequest>(ValidateAwardDataRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/validate/validate_award_data_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/award/validate/validate_award_data_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/award/validate/validate_award_data_required_2.json")
    }

    @Test
    fun required3() {
        testBindingAndMapping("json/infrastructure/dto/award/validate/validate_award_data_required_3.json")
    }
}
