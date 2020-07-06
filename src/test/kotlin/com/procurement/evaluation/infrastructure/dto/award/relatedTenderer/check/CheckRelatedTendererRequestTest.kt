package com.procurement.evaluation.infrastructure.dto.award.relatedTenderer.check

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import com.procurement.evaluation.infrastructure.dto.award.tenderer.CheckRelatedTendererRequest
import org.junit.jupiter.api.Test

class CheckRelatedTendererRequestTest : AbstractDTOTestBase<CheckRelatedTendererRequest>(CheckRelatedTendererRequest::class.java) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/relatedTenderer/check/check_related_tenderer_full.json")
    }
}
