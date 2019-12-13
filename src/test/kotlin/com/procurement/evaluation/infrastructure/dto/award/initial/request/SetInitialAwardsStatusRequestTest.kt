package com.procurement.evaluation.infrastructure.dto.award.initial.request

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class SetInitialAwardsStatusRequestTest :
    AbstractDTOTestBase<SetInitialAwardsStatusRequest>(
        SetInitialAwardsStatusRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/initial/request/request_set_initial_awards_status_full.json")
    }
}
