package com.procurement.evaluation.infrastructure.dto.lot.unsuccessful.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class GetUnsuccessfulLotsResponseTest :
    AbstractDTOTestBase<GetUnsuccessfulLotsResponse>(GetUnsuccessfulLotsResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/lot/unsuccessful/response/response_get_unsuccessful_lots.json")
    }
}
