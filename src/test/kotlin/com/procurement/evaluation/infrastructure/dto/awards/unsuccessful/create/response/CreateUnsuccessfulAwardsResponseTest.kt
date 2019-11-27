package com.procurement.evaluation.infrastructure.dto.awards.unsuccessful.create.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.dto.award.unsuccessful.response.CreateUnsuccessfulAwardsResponse
import org.junit.jupiter.api.Test

class CreateUnsuccessfulAwardsResponseTest : AbstractDTOTestBase<CreateUnsuccessfulAwardsResponse>(
    CreateUnsuccessfulAwardsResponse::class.java
) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/awards/unsuccessful/create/response/response_create_unsuccessful_awards_full.json")
    }
}
