package com.procurement.evaluation.infrastructure.dto.lot.unsuccessful.request

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v1.model.request.GetUnsuccessfulLotsRequest
import org.junit.jupiter.api.Test

class GetUnsuccessfulLotsRequestTest : AbstractDTOTestBase<GetUnsuccessfulLotsRequest>(GetUnsuccessfulLotsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/lot/unsuccessful/request/request_get_unsuccessful_lots_full.json")
    }
}
