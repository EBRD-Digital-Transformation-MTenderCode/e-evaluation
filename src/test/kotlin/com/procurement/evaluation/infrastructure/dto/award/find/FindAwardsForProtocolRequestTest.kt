package com.procurement.evaluation.infrastructure.dto.award.find

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.request.FindAwardsForProtocolRequest
import org.junit.jupiter.api.Test

class FindAwardsForProtocolRequestTest : AbstractDTOTestBase<FindAwardsForProtocolRequest>(FindAwardsForProtocolRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/find/find_awards_for_protocol_request_full.json")
    }
}
