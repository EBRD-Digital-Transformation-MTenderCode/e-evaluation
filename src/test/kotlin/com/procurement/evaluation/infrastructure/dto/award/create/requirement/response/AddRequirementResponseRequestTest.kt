package com.procurement.evaluation.infrastructure.dto.award.create.requirement.response

import com.procurement.evaluation.infrastructure.AbstractDTOTestBase
import org.junit.jupiter.api.Test

internal class AddRequirementResponseRequestTest :
    AbstractDTOTestBase<AddRequirementResponseRequest>(
        AddRequirementResponseRequest::class.java
    ) {

    @Test
    fun fully() {
        testBindingAndMapping("json/infrastructure/dto/award/create/requirement/response/add_requirement_response_full.json")
    }
}