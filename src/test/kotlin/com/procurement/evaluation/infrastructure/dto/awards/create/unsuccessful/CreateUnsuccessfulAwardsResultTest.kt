package com.procurement.evaluation.infrastructure.dto.awards.create.unsuccessful

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.response.CreateUnsuccessfulAwardsResult
import org.junit.jupiter.api.Test

class CreateUnsuccessfulAwardsResultTest : AbstractDTOTestBase<CreateUnsuccessfulAwardsResult>(
    CreateUnsuccessfulAwardsResult::class.java
) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/awards/create/unsuccessful/create_unsuccessful_award_response_full.json")
    }
}
