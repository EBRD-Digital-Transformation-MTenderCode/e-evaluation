package com.procurement.evaluation.infrastructure.dto.award.check.state

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.handler.v2.model.request.CheckAwardsStateRequest
import org.junit.jupiter.api.Test

class CheckAwardsStateRequestTest : AbstractDTOTestBase<CheckAwardsStateRequest>(CheckAwardsStateRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/check/state/check_awards_state_params_full.json")
    }

    @Test
    fun required1() {
        testBindingAndMapping("json/infrastructure/dto/award/check/state/check_awards_state_params_required_1.json")
    }
}
