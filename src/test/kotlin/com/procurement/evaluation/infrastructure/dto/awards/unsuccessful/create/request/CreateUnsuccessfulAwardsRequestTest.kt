package com.procurement.evaluation.infrastructure.dto.awards.unsuccessful.create.request

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.dto.award.unsuccessful.request.CreateUnsuccessfulAwardsRequest
import org.junit.jupiter.api.Test

class CreateUnsuccessfulAwardsRequestTest : AbstractDTOTestBase<CreateUnsuccessfulAwardsRequest>(
    CreateUnsuccessfulAwardsRequest::class.java
) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/awards/unsuccessful/create/request/request_create_unsuccessful_awards_full.json")
    }

}
