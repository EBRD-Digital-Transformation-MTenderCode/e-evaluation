package com.procurement.evaluation.infrastructure.dto.award.create

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.response.CreateAwardResult
import org.junit.jupiter.api.Test

class CreateAwardResultTest : AbstractDTOTestBase<CreateAwardResult>(CreateAwardResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/create/create_award_result_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/award/create/create_award_result_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/award/create/create_award_result_required_2.json")
    }
}
