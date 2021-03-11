package com.procurement.evaluation.infrastructure.dto.award.`do`.consideration

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.response.DoConsiderationResult
import org.junit.jupiter.api.Test

class DoConsiderationResultTest : AbstractDTOTestBase<DoConsiderationResult>(DoConsiderationResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/do/consideration/do_consideration_result_full.json")
    }
}
