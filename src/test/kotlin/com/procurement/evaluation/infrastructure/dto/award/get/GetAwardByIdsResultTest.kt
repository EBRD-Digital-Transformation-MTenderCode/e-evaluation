package com.procurement.evaluation.infrastructure.dto.award.get

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.response.GetAwardByIdsResult
import org.junit.jupiter.api.Test

class GetAwardByIdsResultTest : AbstractDTOTestBase<GetAwardByIdsResult>(GetAwardByIdsResult::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/get/get_awards_by_ids_result_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/award/get/get_awards_by_ids_result_required_1.json")
    }

    @Test
    fun required2() {
        testBindingAndMapping("json/infrastructure/dto/award/get/get_awards_by_ids_result_required_2.json")
    }
}
