package com.procurement.evaluation.infrastructure.dto.awards.create.unsuccessful

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.dto.award.create.request.CreateAwardsRequest
import com.procurement.evaluation.infrastructure.handler.create.unsuccessfulaward.CreateUnsuccessfulAwardsRequest
import org.junit.jupiter.api.Test

class CreateUnsuccessfulAwardsRequestTest : AbstractDTOTestBase<CreateUnsuccessfulAwardsRequest>(
    CreateUnsuccessfulAwardsRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/awards/create/unsuccessful/create_unsuccessful_award_request_full.json")
    }
}
