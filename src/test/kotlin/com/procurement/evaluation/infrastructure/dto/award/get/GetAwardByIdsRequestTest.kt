package com.procurement.evaluation.infrastructure.dto.award.get

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.request.GetAwardByIdsRequest
import org.junit.jupiter.api.Test

class GetAwardByIdsRequestTest : AbstractDTOTestBase<GetAwardByIdsRequest>(GetAwardByIdsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/get/get_awards_by_ids_request_full.json")
    }
}
