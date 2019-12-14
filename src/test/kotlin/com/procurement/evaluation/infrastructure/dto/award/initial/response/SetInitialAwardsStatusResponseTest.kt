package com.procurement.evaluation.infrastructure.dto.award.initial.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

class SetInitialAwardsStatusResponseTest :
    AbstractDTOTestBase<SetInitialAwardsStatusResponse>(SetInitialAwardsStatusResponse::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/initial/response/response_set_Initial_awards_status_full.json")
    }
}
